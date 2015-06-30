package latmod.xpt;
import java.io.File;

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
		
		if(config.hasChanged()) config.save();
	}
}