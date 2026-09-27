package io.github.stomarver.fundo.sound;

import io.github.stomarver.fundo.Fundo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

 




public final class FundoSounds {
	public static SoundEvent MILK_AMBIENT;
	public static SoundEvent MILK_BUCKET_FILL;
	public static SoundEvent MILK_BUCKET_EMPTY;
	public static SoundEvent MILK_BOTTLE_FILL;
	public static SoundEvent MILK_BOTTLE_EMPTY;
	public static SoundEvent MILK_DRIP;
	public static SoundEvent MILK_BOTTLE_BREAK;

	public static SoundEvent MILK_ICE_BREAK;
	public static SoundEvent MILK_ICE_STEP;
	public static SoundEvent MILK_ICE_PLACE;
	public static SoundEvent MILK_ICE_HIT;
	public static SoundEvent MILK_ICE_FALL;
	public static SoundEvent MILK_ICE_FORM;
	public static SoundEvent MILK_ICE_MELT;
	public static SoundType MILK_ICE;

	private FundoSounds() {
	}

	private static SoundEvent register(String path) {
		return Registry.register(BuiltInRegistries.SOUND_EVENT, Fundo.id(path),
				SoundEvent.createVariableRangeEvent(Fundo.id(path)));
	}

	public static void register() {

		MILK_AMBIENT = register("milk_ambient");
		MILK_BUCKET_FILL = register("milk_bucket_fill");
		MILK_BUCKET_EMPTY = register("milk_bucket_empty");
		MILK_BOTTLE_FILL = register("milk_bottle_fill");
		MILK_BOTTLE_EMPTY = register("milk_bottle_empty");
		MILK_DRIP = register("milk_drip");
		MILK_BOTTLE_BREAK = register("milk_bottle_break");

		MILK_ICE_BREAK = register("milk_ice_break");
		MILK_ICE_STEP = register("milk_ice_step");
		MILK_ICE_PLACE = register("milk_ice_place");
		MILK_ICE_HIT = register("milk_ice_hit");
		MILK_ICE_FALL = register("milk_ice_fall");
		MILK_ICE_FORM = register("milk_ice_form");
		MILK_ICE_MELT = register("milk_ice_melt");
		MILK_ICE = new SoundType(1.0F, 1.0F,
				MILK_ICE_BREAK, MILK_ICE_STEP, MILK_ICE_PLACE, MILK_ICE_HIT, MILK_ICE_FALL);
	}
}
