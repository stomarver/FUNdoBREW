package io.github.stomarver.fundo.debug;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.ActionLogMode;
import io.github.stomarver.fundo.config.FundoConfig;

public final class ActionLogs {

	private static final DateTimeFormatter REAL_TIME = DateTimeFormatter
			.ofPattern("uuuu-MM-dd HH:mm:ss.SSS XXX")
			.withZone(ZoneId.systemDefault());

	private static final Stream INFINITE = new Stream(
			"fundo_infinite.log", "fundo/infinite", () -> FundoConfig.infinite_potions
					&& FundoConfig.infinite_effect_actions_log != ActionLogMode.DISABLED,
			() -> FundoConfig.infinite_effect_actions_log);
	private static final Stream POTIONS = new Stream(
			"fundo_potions.log", "fundo/potions", () -> FundoConfig.potion_actions_log != ActionLogMode.DISABLED,
			() -> FundoConfig.potion_actions_log);

	private static boolean sessionOpen;
	private static String worldName = "";
	private static long sessionStartTick;
	private static ServerLevel sessionLevel;

	private ActionLogs() {
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(ActionLogs::close, "FUNdoBREW-action-log-close"));
	}

	public static synchronized void sessionOpened(ServerLevel overworld, String newWorldName) {
		closeStreams();
		sessionOpen = true;
		sessionLevel = overworld;
		worldName = newWorldName;
		sessionStartTick = overworld.getGameTime();
		INFINITE.beginSession();
		POTIONS.beginSession();
		INFINITE.sessionMarker(overworld, "=== session opened: \"" + worldName + "\" ===");
		POTIONS.sessionMarker(overworld, "=== session opened: \"" + worldName + "\" ===");
	}

	public static synchronized void sessionClosed(ServerLevel overworld, String closingWorldName) {
		INFINITE.sessionMarker(overworld, "=== session closed: \"" + closingWorldName + "\" (log complete) ===");
		POTIONS.sessionMarker(overworld, "=== session closed: \"" + closingWorldName + "\" (log complete) ===");
		closeStreams();
		sessionOpen = false;
		sessionLevel = null;
		worldName = "";
	}

	/**
	 * Applies the current log settings to the running server session. A newly
	 * enabled stream starts immediately; a disabled stream closes its writer
	 * immediately. Re-enabling a stream appends to its current world session.
	 */
	public static synchronized void refresh() {
		if (!sessionOpen || sessionLevel == null) {
			return;
		}
		INFINITE.refresh(sessionLevel, worldName);
		POTIONS.refresh(sessionLevel, worldName);
	}

	public static void infinite(Level level, String message) {
		INFINITE.write(level, message);
	}

	public static void potions(Level level, String message) {
		POTIONS.write(level, message);
	}

	public static boolean potionsEnabled() {
		return POTIONS.isEnabled();
	}

	public static boolean infiniteEnabled() {
		return INFINITE.isEnabled();
	}

	public static synchronized void close() {
		closeStreams();
	}

	private static void closeStreams() {
		INFINITE.close();
		POTIONS.close();
	}

	private static synchronized long ticksSinceSession(Level level) {
		return level.getGameTime() - sessionStartTick;
	}

	public static String subject(Entity entity) {
		StringBuilder base = new StringBuilder();
		base.append(entity.getType());
		String uuid = entity.getStringUUID();
		base.append('#').append(uuid, 0, Math.min(8, uuid.length()));
		if (entity instanceof Player player) {
			base.append('(').append(player.getGameProfile().name());
			if (entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
				base.append('/').append(serverPlayer.gameMode.getGameModeForPlayer().getSerializedName());
			} else {
				base.append(player.isCreative() ? "/creative" : "");
			}
			base.append(')');
		}
		return base.toString();
	}

	private static final class Stream {
		private final String fileName;
		private final String label;
		private final Supplier<Boolean> enabled;
		private final Supplier<ActionLogMode> mode;
		private BufferedWriter writer;
		private boolean failed;
		private boolean openedThisSession;
		private Instant previousRealTime;
		private long previousTick;

		private Stream(String fileName, String label, Supplier<Boolean> enabled, Supplier<ActionLogMode> mode) {
			this.fileName = fileName;
			this.label = label;
			this.enabled = enabled;
			this.mode = mode;
		}

		private boolean isEnabled() {
			return sessionOpen && Boolean.TRUE.equals(enabled.get()) && mode.get() != ActionLogMode.DISABLED;
		}

		private synchronized void beginSession() {
			failed = false;
			openedThisSession = false;
			previousRealTime = null;
			previousTick = 0L;
		}

		private synchronized void refresh(ServerLevel level, String activeWorldName) {
			if (!isEnabled()) {
				close();
				return;
			}
			if (writer == null && !failed) {
				write(level, "=== logging enabled: \"" + activeWorldName + "\" ===");
			}
		}

		private synchronized void sessionMarker(ServerLevel level, String message) {
			write(level, message);
		}

		private synchronized void write(Level level, String message) {
			if (!(level instanceof ServerLevel) || !isEnabled() || failed) {
				return;
			}
			try {
				ensureWriter();
				Instant now = Instant.now();
				long ticks = ticksSinceSession(level);
				writer.write('[' + timestamp(now, ticks) + "] [" + label + "] " + message);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				failed = true;
				close();
				Fundo.LOGGER.warn("{} action log disabled: {}", label, e.toString());
			}
		}

		private String timestamp(Instant now, long ticks) {
			ActionLogMode timestampMode = mode.get();
			String result = switch (timestampMode) {
				case REAL_TIME -> REAL_TIME.format(now);
				case REAL_TIME_DIFFERENCE -> "Δ" + formatDuration(previousRealTime == null
						? Duration.ZERO : Duration.between(previousRealTime, now));
				case TICKS -> "+" + ticks + "t";
				case TICKS_DIFFERENCE -> "Δ" + (previousRealTime == null ? 0L : ticks - previousTick) + "t";
				case DISABLED -> "disabled";
			};
			previousRealTime = now;
			previousTick = ticks;
			return result;
		}

		private static String formatDuration(Duration duration) {
			long millis = Math.max(0L, duration.toMillis());
			long minutes = millis / 60_000L;
			long seconds = (millis / 1_000L) % 60L;
			long remainder = millis % 1_000L;
			return String.format("%02d:%02d.%03d", minutes, seconds, remainder);
		}

		private void ensureWriter() throws IOException {
			if (writer != null) {
				return;
			}
			Path path = FabricLoader.getInstance().getGameDir().resolve("logs").resolve(fileName);
			Files.createDirectories(path.getParent());
			if (openedThisSession) {
				writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
						StandardOpenOption.APPEND, StandardOpenOption.WRITE);
			} else {
				writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
				openedThisSession = true;
			}
			Fundo.LOGGER.info("{} action log -> {}", label, path.toAbsolutePath());
		}

		private synchronized void close() {
			if (writer == null) {
				return;
			}
			try {
				writer.flush();
				writer.close();
			} catch (IOException ignored) {

			} finally {
				writer = null;
			}
		}
	}
}
