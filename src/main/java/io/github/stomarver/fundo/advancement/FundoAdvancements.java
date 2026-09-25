package io.github.stomarver.fundo.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

import io.github.stomarver.fundo.Fundo;

/** Awards the two event-based FUNdoBREW advancements from server facts only. */
public final class FundoAdvancements {

	private static final Identifier SPILLED_IT_AGAIN = Fundo.id("spilled_it_again");
	private static final Identifier FOREVER_YOUNG = Fundo.id("forever_young");

	private FundoAdvancements() {
	}

	public static void grantSpilledItAgain(Projectile projectile) {
		if (projectile.getOwner() instanceof ServerPlayer player) {
			award(player, SPILLED_IT_AGAIN, "milk_bottle_broken");
		}
	}

	public static void grantForeverYoung(LivingEntity entity) {
		if (entity instanceof ServerPlayer player) {
			award(player, FOREVER_YOUNG, "received_infinite_effect");
		}
	}

	private static void award(ServerPlayer player, Identifier advancementId, String criterion) {
		AdvancementHolder advancement = player.level().getServer().getAdvancements().get(advancementId);
		if (advancement != null) {
			player.getAdvancements().award(advancement, criterion);
		}
	}
}
