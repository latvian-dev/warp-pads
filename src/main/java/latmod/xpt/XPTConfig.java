package latmod.xpt;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigRegistry;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;

public class XPTConfig // XPT
{
	private static ConfigFile configFile;
	public static final ConfigEntryInt levels_for_1000_blocks = new ConfigEntryInt("levels_for_1000_blocks", new IntBounds(20, 0, 200)).sync().setInfo("Levels required to teleport in same dimension");
	public static final ConfigEntryInt levels_for_crossdim = new ConfigEntryInt("levels_for_crossdim", new IntBounds(30, 0, 200)).sync().setInfo("Levels required to teleport to another dimension");
	public static final ConfigEntryInt cooldown_seconds = new ConfigEntryInt("cooldown_seconds", new IntBounds(3, 1, 3600)).sync().setInfo("Teleporter cooldown");
	public static final ConfigEntryBool enable_crafting = new ConfigEntryBool("enable_crafting", true).setInfo("Enable crafting recipes");
	public static final ConfigEntryBool unlink_broken = new ConfigEntryBool("unlink_broken", false).setInfo("Unlink teleporters when one is broken");
	public static final ConfigEntryBool only_linking_uses_xp = new ConfigEntryBool("only_linking_uses_xp", false).setInfo("Only linking will cost levels, teleporting will be free");
	public static final ConfigEntryBool soft_blocks = new ConfigEntryBool("soft_blocks", true).sync().setInfo("Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue");
	public static final ConfigEntryInt levels_for_recall = new ConfigEntryInt("levels_for_recall", new IntBounds(-1, -1, 200)).sync().setInfo("-1 - Disabled\n0+ - Enables Recall Remote crafting and usage");
	
	public static void load()
	{
		File file = new File(FTBLib.folderConfig, "/LatMod/XPT.json");
		configFile = new ConfigFile("xpt", file);
		configFile.configGroup.setName("XPTeleporters");
		configFile.add(new ConfigGroup("general").addAll(XPTConfig.class, null, false));
		ConfigRegistry.add(configFile);
		configFile.load();
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
	
	public static int cooldownTicks()
	{ return cooldown_seconds.get() * 20; }
}