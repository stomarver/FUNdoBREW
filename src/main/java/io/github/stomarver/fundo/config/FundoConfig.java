package io.github.stomarver.fundo.config;

import com.google.gson.annotations.SerializedName;

import eu.midnightdust.lib.config.MidnightConfig;

import io.github.stomarver.fundo.debug.ActionLogs;

@SuppressWarnings("unused")
public class FundoConfig extends MidnightConfig {

	public static final String GENERAL = "general";
	public static final String MISC = "misc";
	public static final String DEBUG = "debug";

	@Entry(category = GENERAL) public static boolean milk_changes = true;

	@Entry(category = GENERAL) public static boolean infinite_potions = true;

	@Comment(category = GENERAL, centered = false) public static Comment features_restart_notice;

	@Comment(category = GENERAL, centered = false) public static Comment main_section_spacer;

	@Comment(category = GENERAL, centered = false) public static Comment quality_of_life;

	@SerializedName("brewing_speed_multiplier")
	@Entry(category = GENERAL, name = "fundo.midnightconfig.brewing_speed_multiplier", min = 1)
	public static int brewingSpeedMultiplier = 1;

	public static int brewingSpeedMultiplier() {
		return Math.max(1, brewingSpeedMultiplier);
	}

	@Override
	public void writeChanges() {
		super.writeChanges();
		ActionLogs.refresh();
	}

	@Entry(category = GENERAL) public static boolean increased_potion_stacking = true;

	@Comment(category = MISC, centered = false) public static Comment compatibility;

	@Entry(category = MISC) public static boolean farmers_delight = true;

	@Comment(category = MISC, centered = false) public static Comment compatibility_restart_notice;

	@Entry(category = DEBUG) public static ActionLogMode infinite_effect_actions_log = ActionLogMode.DISABLED;

	@Entry(category = DEBUG) public static ActionLogMode potion_actions_log = ActionLogMode.DISABLED;

	@Comment(category = DEBUG, centered = false) public static Comment debug_section_spacer;

	@Comment(category = DEBUG, centered = false) public static Comment visual;

	@Entry(category = DEBUG) public static PotionHitboxVisibility show_potion_hitboxes = PotionHitboxVisibility.OFF;

	public enum PotionHitboxVisibility {
		OFF,
		IN_OVERLAY,
		ALWAYS
	}
}
