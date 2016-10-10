package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.RegistryObject;
import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigFileProvider;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;

public class XPTConfig // XPT
{
    @RegistryObject(XPT.MOD_ID)
    public static final IConfigFileProvider FILE = () -> new File(LMUtils.folderConfig, "XPTeleporters.json");

    @ConfigValue(id = "general.enable_crafting", file = XPT.MOD_ID, info = "Enable crafting recipes")
    public static final PropertyBool ENABLE_CRAFTING = new PropertyBool(true);

    @ConfigValue(id = "general.levels_per_block", file = XPT.MOD_ID, info = "Levels required to teleport in same dimension")
    public static final PropertyDouble LEVELS_PER_BLOCK = new PropertyDouble(0.001D).setMin(0D).setMax(5D);

    @ConfigValue(id = "general.levels_for_crossdim", file = XPT.MOD_ID, info = "Levels required to teleport to another dimension")
    public static final PropertyInt LEVELS_FOR_CROSSDIM = new PropertyInt(30).setMin(0).setMax(200);

    @ConfigValue(id = "general.soft_blocks", file = XPT.MOD_ID, info = "Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue")
    public static final PropertyBool SOFT_BLOCKS = new PropertyBool(true);

    public static int getLevels(double distance)
    {
        if(distance <= 0D)
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