package latmod.xpt;
import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class XPTConfig // XPT
{
	public static Configuration config;
	public static float levels_for_1000_blocks;
	public static float levels_for_crossdim;
	public static int cooldown_seconds;
	public static boolean enable_crafting;
	
	public static void load(FMLPreInitializationEvent e)
	{
		config = new Configuration(new File(e.getModConfigurationDirectory(), "/LatMod/XPT.cfg"));
		
		levels_for_1000_blocks = config.getFloat("levels_for_1000_blocks", "general", 20F, 0F, 200F, "Levels required to teleport to another dimension");
		levels_for_crossdim = config.getFloat("levels_for_crossdim", "general", 30F, 0F, 200F, "Levels required to teleport to another dimension");
		cooldown_seconds = config.getInt("cooldown_seconds", "general", 3, 1, 3600, "Teleporter cooldown");
		enable_crafting = config.getBoolean("enable_crafting", "general", true, "Enable crafting recipes");
		
		if(config.hasChanged()) config.save();
	}
}