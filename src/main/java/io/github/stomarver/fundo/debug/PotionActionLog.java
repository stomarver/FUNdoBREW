package io.github.stomarver.fundo.debug;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import io.github.stomarver.fundo.effect.InfinitePotionMark;
import io.github.stomarver.fundo.item.FundoItems;

public final class PotionActionLog {

	private PotionActionLog() {
	}

	public static boolean tracks(ItemStack stack) {
		Item item = stack.getItem();
		return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION
				|| item == Items.GLASS_BOTTLE || FundoItems.isNormalMilkBottle(item)
				|| item == FundoItems.SPLASH_MILK_BOTTLE || item == FundoItems.LINGERING_MILK_BOTTLE;
	}

	public static String describe(ItemStack stack) {
		StringBuilder line = new StringBuilder();
		line.append("item=").append(BuiltInRegistries.ITEM.getKey(stack.getItem()))
				.append(" count=").append(stack.getCount());
		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		if (contents != null) {
			Optional<Identifier> potion = contents.potion()
					.flatMap(Holder::unwrapKey).map(key -> key.identifier());
			int effectCount = 0;
			long endless = 0L;
			for (MobEffectInstance effect : contents.getAllEffects()) {
				effectCount++;
				if (effect.isInfiniteDuration()) {
					endless++;
				}
			}
			line.append(" potion=").append(potion.map(Identifier::toString).orElse("custom"))
					.append(" effects=").append(effectCount);
			if (endless > 0L) {
				line.append(" endless=").append(endless);
			}
		}
		if (InfinitePotionMark.isMarked(stack)) {
			line.append(" mark=fundo:infinite");
		}
		return line.toString();
	}

	public static String projectile(Entity projectile) {
		return "entity=" + ActionLogs.subject(projectile) + " pos=" + position(projectile.position());
	}

	public static String hit(HitResult hit) {
		String target = switch (hit.getType()) {
			case ENTITY -> "entity=" + ActionLogs.subject(((EntityHitResult) hit).getEntity());
			case BLOCK -> "block";
			case MISS -> "miss";
		};
		return "hit=" + target + " at=" + position(hit.getLocation());
	}

	public static String position(Vec3 position) {
		return String.format("(%.3f,%.3f,%.3f)", position.x, position.y, position.z);
	}
}
