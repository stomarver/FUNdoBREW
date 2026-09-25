package io.github.stomarver.fundo.client.potionhitbox;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.gizmos.Gizmo;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.phys.Vec3;

import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.network.PotionHitboxImpactPayload;

public final class PotionHitboxGizmos {

	private static final int SOLID_CREAM = 0xFFFFF1D6;

	private static final int CREAM_RGB = 0x00FFF1D6;
	private static final int FADE_START_ALPHA = 0xB0;
	private static final int FADE_END_ALPHA = 0x20;

	private static final float VANILLA_HITBOX_STROKE = 2.5F;

	private static final long IMPACT_SOLID_TICKS = 1L;

	private static final long IMPACT_FADE_TICKS = 12L;

	private static final int VANILLA_CIRCLE_VERTICES = 20;
	private static final double VANILLA_CIRCLE_STEP = Math.PI * 2.0D / VANILLA_CIRCLE_VERTICES;

	private static final List<SphereImpact> SPHERE_IMPACTS = new ArrayList<>();
	private static final List<CylinderImpact> CYLINDER_IMPACTS = new ArrayList<>();

	private PotionHitboxGizmos() {
	}

	public static void init() {
		LevelRenderEvents.BEFORE_GIZMOS.register(context -> {
			if (!PotionHitboxDebug.isEnabled(Minecraft.getInstance())) {
				return;
			}
			ClientLevel level = Minecraft.getInstance().level;
			if (level == null) {
				return;
			}
			try (Gizmos.TemporaryCollection ignored =
					context.levelRenderer().collectPerFrameRenderThreadGizmos()) {
				for (var entity : level.entitiesForRendering()) {
					if (entity instanceof AreaEffectCloud cloud && !cloud.isWaiting()) {
						submitCylinder(PotionHitboxes.cloudCylinder(cloud), SOLID_CREAM);
					}
				}
				submitImpactMarkers(level);
			}
		});
	}

	public static void recordServerImpact(PotionHitboxImpactPayload payload) {
		if (!PotionHitboxDebug.isEnabled(Minecraft.getInstance())) {
			return;
		}
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}
		long createdAtTick = level.getGameTime();
		if (payload.cylinder()) {
			CYLINDER_IMPACTS.add(new CylinderImpact(level, payload.asCylinder(), createdAtTick));
		} else {
			SPHERE_IMPACTS.add(new SphereImpact(level, payload.asSphere(), createdAtTick));
		}
	}

	private static void submitCylinder(PotionHitboxes.Cylinder cylinder, int color) {
		Vec3 bottom = new Vec3(cylinder.centerX(), cylinder.baseY(), cylinder.centerZ());
		GizmoStyle style = GizmoStyle.stroke(color, VANILLA_HITBOX_STROKE);
		Gizmos.circle(bottom, (float) cylinder.radius(), style).setAlwaysOnTop();
		Gizmos.circle(bottom.add(0.0D, cylinder.height(), 0.0D),
				(float) cylinder.radius(), style).setAlwaysOnTop();
	}

	private static void submitSphere(PotionHitboxes.Sphere sphere, int color) {
		Vec3 center = new Vec3(sphere.centerX(), sphere.centerY(), sphere.centerZ());
		float radius = (float) sphere.radius();
		GizmoStyle style = GizmoStyle.stroke(color, VANILLA_HITBOX_STROKE);

		Gizmos.circle(center, radius, style).setAlwaysOnTop();

		Gizmos.addGizmo(new CardinalCircleGizmo(center, radius, CardinalPlane.EAST_WEST_VERTICAL,
				color, VANILLA_HITBOX_STROKE)).setAlwaysOnTop();
		Gizmos.addGizmo(new CardinalCircleGizmo(center, radius, CardinalPlane.NORTH_SOUTH_VERTICAL,
				color, VANILLA_HITBOX_STROKE)).setAlwaysOnTop();
	}

	public static int activeImpactMarkerCount(ClientLevel activeLevel) {
		pruneImpactMarkers(activeLevel, activeLevel.getGameTime());
		return SPHERE_IMPACTS.size() + CYLINDER_IMPACTS.size();
	}

	private static void submitImpactMarkers(ClientLevel activeLevel) {
		long now = activeLevel.getGameTime();
		pruneImpactMarkers(activeLevel, now);
		for (SphereImpact marker : SPHERE_IMPACTS) {
			submitSphere(marker.shape(), markerColor(marker.createdAtTick(), now));
		}
		for (CylinderImpact marker : CYLINDER_IMPACTS) {
			submitCylinder(marker.shape(), markerColor(marker.createdAtTick(), now));
		}
	}

	private static void pruneImpactMarkers(ClientLevel activeLevel, long now) {
		SPHERE_IMPACTS.removeIf(marker -> marker.level() != activeLevel || expired(marker.createdAtTick(), now));
		CYLINDER_IMPACTS.removeIf(marker -> marker.level() != activeLevel || expired(marker.createdAtTick(), now));
	}

	private static boolean expired(long createdAtTick, long now) {
		return now >= createdAtTick + IMPACT_SOLID_TICKS + IMPACT_FADE_TICKS;
	}

	private static int markerColor(long createdAtTick, long now) {
		long age = now - createdAtTick;
		if (age < IMPACT_SOLID_TICKS) {
			return SOLID_CREAM;
		}

		long fadeIndex = age - IMPACT_SOLID_TICKS;
		int alpha = FADE_START_ALPHA + (int) ((FADE_END_ALPHA - FADE_START_ALPHA) * fadeIndex
				/ (IMPACT_FADE_TICKS - 1L));
		return (alpha << 24) | CREAM_RGB;
	}

	private record SphereImpact(ClientLevel level, PotionHitboxes.Sphere shape, long createdAtTick) {
	}

	private record CylinderImpact(ClientLevel level, PotionHitboxes.Cylinder shape, long createdAtTick) {
	}

	private enum CardinalPlane {

		EAST_WEST_VERTICAL,

		NORTH_SOUTH_VERTICAL
	}

	private record CardinalCircleGizmo(Vec3 center, float radius, CardinalPlane plane, int color, float width)
			implements Gizmo {

		@Override
		public void emit(GizmoPrimitives primitives, float partialTick) {
			Vec3 previous = point(0.0D);
			for (int segment = 1; segment <= VANILLA_CIRCLE_VERTICES; segment++) {
				Vec3 current = point(segment * VANILLA_CIRCLE_STEP);
				primitives.addLine(previous, current, color, width);
				previous = current;
			}
		}

		private Vec3 point(double angle) {
			double horizontal = Math.cos(angle) * radius;
			double vertical = Math.sin(angle) * radius;
			return switch (plane) {
				case EAST_WEST_VERTICAL -> center.add(horizontal, vertical, 0.0D);
				case NORTH_SOUTH_VERTICAL -> center.add(0.0D, vertical, horizontal);
			};
		}
	}
}
