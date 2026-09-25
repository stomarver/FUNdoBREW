package io.github.stomarver.fundo.item;

import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.level.Level;

import io.github.stomarver.fundo.entity.ThrownLingeringMilkBottle;

public class LingeringMilkBottleItem extends LingeringPotionItem {

	public LingeringMilkBottleItem(Properties properties) {
		super(properties);
	}

	@Override
	protected AbstractThrownPotion createPotion(ServerLevel level, LivingEntity shooter, ItemStack stack) {
		return new ThrownLingeringMilkBottle(level, shooter, stack);
	}

	@Override
	protected AbstractThrownPotion createPotion(Level level, Position position, ItemStack stack) {
		return new ThrownLingeringMilkBottle(level, position.x(), position.y(), position.z(), stack);
	}
}
