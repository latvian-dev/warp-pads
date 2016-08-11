package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryDouble;
import com.feed_the_beast.ftbl.api.config.ConfigEntryInt;
import com.feed_the_beast.ftbl.api.config.ConfigFile;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.latmod.lib.annotations.Info;
import com.latmod.lib.annotations.NumberBounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class XPTConfig // XPT
{
    public static final ConfigFile configFile = new ConfigFile();

    @Info("Enable crafting recipes")
    public static final ConfigEntryBool enable_crafting = new ConfigEntryBool(true);

    @NumberBounds(min = 0D, max = 5D)
    @Info("Levels required to teleport in same dimension")
    public static final ConfigEntryDouble levels_per_block = new ConfigEntryDouble(0.001D);

    @NumberBounds(min = 0, max = 200)
    @Info("Levels required to teleport to another dimension")
    public static final ConfigEntryInt levels_for_crossdim = new ConfigEntryInt(30);

    @Info("Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue")
    public static final ConfigEntryBool soft_blocks = new ConfigEntryBool(true);

    public static void load()
    {
        configFile.setFile(new File(FTBLib.folderConfig, "XPTeleporters.json"));
        configFile.setDisplayName(new TextComponentString("XPTeleporters"));
        configFile.addGroup("general", XPTConfig.class);
        FTBLibAPI.INSTANCE.registerConfigFile("xpt", configFile);
        configFile.load();
    }

    public static int getLevels(double distance)
    {
        if(distance <= 0D)
        {
            return levels_for_crossdim.getAsInt();
        }

        return (int) (distance * levels_per_block.getAsDouble());
    }

    public static boolean consumeLevels(EntityPlayer ep, int levels, boolean simulate)
    {
        if(levels <= 0 || ep.capabilities.isCreativeMode || ep.experienceLevel >= levels)
        {
            if(!simulate && levels > 0)
            {
                ep.addExperienceLevel(-levels);
            }

            return true;
        }

        return false;
    }
}