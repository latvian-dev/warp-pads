package latmod.xpt;
import java.io.File;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigListRegistry;
import latmod.lib.LMFileUtils;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

public class XPTConfig // XPT
{
	private static ConfigFile configFile;
	public static final ConfigGroup group = new ConfigGroup("general");
	public static final ConfigEntryInt levels_for_1000_blocks = new ConfigEntryInt("levels_for_1000_blocks", new IntBounds(20, 0, 200)).setInfo("Levels required to teleport in same dimension");
	public static final ConfigEntryInt levels_for_crossdim = new ConfigEntryInt("levels_for_crossdim", new IntBounds(30, 0, 200)).setInfo("Levels required to teleport to another dimension");
	public static final ConfigEntryInt cooldown_seconds = new ConfigEntryInt("cooldown_seconds", new IntBounds(3, 1, 3600)).setInfo("Teleporter cooldown");
	public static final ConfigEntryBool enable_crafting = new ConfigEntryBool("enable_crafting", true).setInfo("Enable crafting recipes");
	public static final ConfigEntryBool unlink_broken = new ConfigEntryBool("unlink_broken", false).setInfo("Unlink teleporters when one is broken");
	public static final ConfigEntryBool only_linking_uses_xp = new ConfigEntryBool("only_linking_uses_xp", false).setInfo("Only linking will cost levels, teleporting will be free");
	public static final ConfigEntryBool soft_blocks = new ConfigEntryBool("soft_blocks", true).setInfo("Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue");
	public static final ConfigEntryInt levels_for_recall = new ConfigEntryInt("levels_for_recall", new IntBounds(-1, -1, 200)).setInfo("-1 - Disabled\n0+ - Enables Recall Remote crafting and usage");
	
	public static void load()
	{
		File file = new File(FTBLib.folderConfig, "/LatMod/XPT.cfg");
		if(file.exists())
		{
			Configuration config = new Configuration(file);
			levels_for_1000_blocks.set(config.getInt("levels_for_1000_blocks", "general", 20, 0, 200, ""));
			levels_for_crossdim.set(config.getInt("levels_for_crossdim", "general", 30, 0, 200, ""));
			cooldown_seconds.set(config.getInt("cooldown_seconds", "general", 3, 1, 3600, ""));
			enable_crafting.set(config.getBoolean("enable_crafting", "general", true, ""));
			unlink_broken.set(config.getBoolean("unlink_broken", "general", false, ""));
			only_linking_uses_xp.set(config.getBoolean("only_linking_uses_xp", "general", false, ""));
			soft_blocks.set(config.getBoolean("soft_blocks", "general", true, ""));
			levels_for_recall.set(config.getInt("levels_for_recall", "general", -1, -1, 200, ""));
			
			LMFileUtils.delete(file);
		}
		
		file = new File(FTBLib.folderConfig, "/LatMod/XPT.json");
		configFile = new ConfigFile("xpt", file);
		configFile.configList.setName("XPTeleporters");
		group.addAll(XPTConfig.class);
		configFile.add(group);
		ConfigListRegistry.instance.add(configFile);
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
}