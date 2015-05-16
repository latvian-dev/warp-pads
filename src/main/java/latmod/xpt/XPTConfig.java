package latmod.xpt;
import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class XPTConfig
{
	public static Configuration config;
	
	public static void load(FMLPreInitializationEvent e)
	{
		config = new Configuration(new File(e.getModConfigurationDirectory(), "/LatMod/EMC_Condenser.cfg"));
		
		
		if(config.hasChanged()) config.save();
	}
	
	public static class General
	{
		public static boolean enableBlacklist;
		public static float ununblockEnchantPower;
		public static boolean removeNoEMCTooltip;
		public static int ticksToInfuse;
		public static boolean forceVanillaRecipes;
		public static boolean forceVanillaEMC;
	}
}