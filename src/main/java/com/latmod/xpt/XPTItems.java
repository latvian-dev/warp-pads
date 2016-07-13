package com.latmod.xpt;

import com.latmod.xpt.block.BlockTeleporter;

/**
 * Created by LatvianModder on 29.01.2016.
 */
public class XPTItems
{
    public static BlockTeleporter teleporter;

    public static void init()
    {
        teleporter = XPT.mod.register("teleporter", new BlockTeleporter());
    }
}