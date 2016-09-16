package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.config.ConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.impl.ConfigFile;
import com.feed_the_beast.ftbl.api.config.impl.PropertyBool;
import com.feed_the_beast.ftbl.api.config.impl.PropertyDouble;
import com.feed_the_beast.ftbl.api.config.impl.PropertyInt;
import com.latmod.lib.annotations.Info;
import com.latmod.lib.util.LMUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class XPTConfig // XPT
{
    public static final ConfigFile FILE = new ConfigFile();

    @Info("Enable crafting recipes")
    private static final IConfigKey ENABLE_CRAFTING = new ConfigKey("general.enable_crafting", new PropertyBool(true), null);

    @Info("Levels required to teleport in same dimension")
    private static final IConfigKey LEVELS_PER_BLOCK = new ConfigKey("general.levels_per_block", new PropertyDouble(0.001D).setMin(0D).setMax(5D), null);

    @Info("Levels required to teleport to another dimension")
    private static final IConfigKey LEVELS_FOR_CROSSDIM = new ConfigKey("general.levels_for_crossdim", new PropertyInt(30).setMin(0).setMax(200), null);

    @Info("Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue")
    static final IConfigKey SOFT_BLOCKS = new ConfigKey("general.soft_blocks", new PropertyBool(true), null);

    static void load()
    {
        FILE.add(ENABLE_CRAFTING);
        FILE.add(LEVELS_PER_BLOCK);
        FILE.add(LEVELS_FOR_CROSSDIM);
        FILE.add(SOFT_BLOCKS);

        FILE.setFile(new File(LMUtils.folderConfig, "XPTeleporters.json"));
        FTBLibAPI.get().getRegistries().registerConfigFile("xpt", FILE, new TextComponentString("XPTeleporters"));
        FILE.load();
    }

    static boolean enableCrafting()
    {
        return FILE.get(ENABLE_CRAFTING).getBoolean();
    }

    public static int getLevels(double distance)
    {
        if(distance <= 0D)
        {
            return FILE.get(LEVELS_FOR_CROSSDIM).getInt();
        }

        return (int) (distance * FILE.get(LEVELS_PER_BLOCK).getDouble());
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

    public static boolean softBlocks()
    {
        return FILE.get(SOFT_BLOCKS).getBoolean();
    }
}