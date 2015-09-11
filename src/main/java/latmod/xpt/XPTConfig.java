package latmod.xpt;
import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

public class XPTConfig // XPT
{
	public static final String category = "general";
	
	public static Configuration config;
	public static int levels_for_1000_blocks;
	public static int levels_for_crossdim;
	public static int cooldown_seconds;
	public static boolean enable_crafting;
	public static boolean unlink_broken;
	public static boolean only_linking_uses_xp;
	public static boolean soft_blocks;
	public static int levels_for_recall;
	
	public static void load(FMLPreInitializationEvent e)
	{
		config = new Configuration(new File(e.getModConfigurationDirectory(), "/LatMod/XPT.cfg"));
		reloadConfig();
		config.getCategory(category).remove("use_food_levels");
		if(config.hasChanged()) config.save();
	}
	
	public static void reloadConfig()
	{
		levels_for_1000_blocks = config.getInt("levels_for_1000_blocks", category, 20, 0, 200, "Levels required to teleport in same dimension\n");
		levels_for_crossdim = config.getInt("levels_for_crossdim", category, 30, 0, 200, "Levels required to teleport to another dimension\n");
		cooldown_seconds = config.getInt("cooldown_seconds", category, 3, 1, 3600, "Teleporter cooldown\n");
		enable_crafting = config.getBoolean("enable_crafting", category, true, "Enable crafting recipes\n");
		unlink_broken = config.getBoolean("unlink_broken", category, false, "Unlink teleporters when one is broken\n");
		only_linking_uses_xp = config.getBoolean("only_linking_uses_xp", category, false, "Only linking will cost levels, teleporting will be free\n");
		soft_blocks = config.getBoolean("soft_blocks", category, true, "Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue");
		levels_for_recall = config.getInt("levels_for_recall", category, -1, -1, 200, "-1 - Disabled\n0+ - Enables Recall Remote crafting and usage");
	}
	
	public static boolean canConsumeLevels(EntityPlayer ep, int levels)
	{
		if(levels <= 0 || ep.capabilities.isCreativeMode) return true;
		return ep.experienceLevel >= levels;
	}
	
	public static void consumeLevels(EntityPlayer ep, int levels)
	{
		if(levels <= 0 || ep.capabilities.isCreativeMode) return;
		ep.addExperienceLevel(-levels);
	}
}