package com.latmod.xpt;

import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.latmod.xpt.block.TileTeleporter;
import com.latmod.xpt.item.XPTItems;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by LatvianModder on 23.01.2017.
 */
public class XPTCommon
{
    public void preInit()
    {
        LMUtils.register(XPTItems.TELEPORTER);
        GameRegistry.registerTileEntity(TileTeleporter.class, "xpt.teleporter");
    }
}