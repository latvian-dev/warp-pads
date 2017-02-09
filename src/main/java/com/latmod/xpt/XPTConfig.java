package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;

public class XPTConfig // XPT
{
    private static final PropertyDouble LEVELS_PER_BLOCK = new PropertyDouble(0.001D).setMin(0D).setMax(5D);
    private static final PropertyInt LEVELS_FOR_CROSSDIM = new PropertyInt(30).setMin(0).setMax(200);
    public static final PropertyBool SOFT_BLOCKS = new PropertyBool(true);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfigFileProvider(XPT.MOD_ID, () -> new File(LMUtils.folderConfig, "XPTeleporters.json"));

        reg.addConfig(XPT.MOD_ID, "general.levels_per_block", LEVELS_PER_BLOCK).setInfo("Levels required to teleport in same dimension");
        reg.addConfig(XPT.MOD_ID, "general.levels_for_crossdim", LEVELS_FOR_CROSSDIM).setInfo("Levels required to teleport to another dimension");
        reg.addConfig(XPT.MOD_ID, "general.soft_blocks", SOFT_BLOCKS).setInfo("Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue");
    }

    public static int getLevels(double distance)
    {
        if(distance < 0D)
        {
            return LEVELS_FOR_CROSSDIM.getInt();
        }

        return (int) (distance * LEVELS_PER_BLOCK.getDouble());
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