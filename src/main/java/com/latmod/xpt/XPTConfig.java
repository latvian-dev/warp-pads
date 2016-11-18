package com.latmod.xpt;

import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import net.minecraft.entity.player.EntityPlayer;

public class XPTConfig // XPT
{
    public static final PropertyBool ENABLE_CRAFTING = new PropertyBool(true);
    public static final PropertyDouble LEVELS_PER_BLOCK = new PropertyDouble(0.001D).setMin(0D).setMax(5D);
    public static final PropertyInt LEVELS_FOR_CROSSDIM = new PropertyInt(30).setMin(0).setMax(200);
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