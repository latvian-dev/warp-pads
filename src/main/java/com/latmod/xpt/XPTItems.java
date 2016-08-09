package com.latmod.xpt;

import com.feed_the_beast.ftbl.util.FTBLib;
import com.latmod.xpt.block.BlockTeleporter;
import com.latmod.xpt.block.TileTeleporter;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 29.01.2016.
 */
public class XPTItems
{
    public static final BlockTeleporter TELEPORTER = FTBLib.register(new ResourceLocation(XPT.MOD_ID, "teleporter"), new BlockTeleporter());

    public static void init()
    {
        FTBLib.addTile(TileTeleporter.class, TELEPORTER.getRegistryName());
    }
}