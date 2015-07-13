package latmod.xpt;
import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

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
	public static int use_food_levels;
	public static boolean soft_blocks;
	public static int levels_for_recall;
	
	public static void load(FMLPreInitializationEvent e)
	{
		config = new Configuration(new File(e.getModConfigurationDirectory(), "/LatMod/XPT.cfg"));
		
		levels_for_1000_blocks = config.getInt("levels_for_1000_blocks", category, 20, 0, 200, "Levels required to teleport in same dimension\n");
		levels_for_crossdim = config.getInt("levels_for_crossdim", category, 30, 0, 200, "Levels required to teleport to another dimension\n");
		cooldown_seconds = config.getInt("cooldown_seconds", category, 3, 1, 3600, "Teleporter cooldown\n");
		enable_crafting = config.getBoolean("enable_crafting", category, true, "Enable crafting recipes\n");
		unlink_broken = config.getBoolean("unlink_broken", category, false, "Unlink teleporters when one is broken\n");
		only_linking_uses_xp = config.getBoolean("only_linking_uses_xp", category, false, "Only linking will cost levels, teleporting will be free\n");
		use_food_levels = config.getInt("use_food_levels", category, 0, 0, 2, "0 - Disabled\n1 - Will teleport if required food is > 20 && existing > 0\n2 - Won't teleport if required food > 20\n");
		soft_blocks = config.getBoolean("soft_blocks", category, true, "Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue");
		levels_for_recall = config.getInt("levels_for_recall", category, -1, -1, 200, "-1 - Disabled\n0+ - Enables Recall Remote crafting and usage");
		
		if(config.hasChanged()) config.save();
	}
	
	public static boolean canConsumeLevels(EntityPlayer ep, int levels)
	{
		if(levels <= 0 || ep.capabilities.isCreativeMode) return true;
		
		if(use_food_levels == 0)
			return ep.experienceLevel >= levels;
		
		int foodLevels = ep.getFoodStats().getFoodLevel();
		return foodLevels > 0 && (use_food_levels == 1 || (foodLevels >= Math.min(levels, 20)));
	}
	
	public static void consumeLevels(EntityPlayer ep, int levels)
	{
		if(levels <= 0 || ep.capabilities.isCreativeMode) return;
		
		if(use_food_levels == 0)
			ep.addExperienceLevel(-levels);
		else
			ep.getFoodStats().addStats(-Math.min(ep.getFoodStats().getFoodLevel(), Math.min(20, levels)), 0F);
	}
}