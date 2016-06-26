package com.latmod.xpt;

import com.latmod.xpt.block.BlockTeleporter;
import com.latmod.xpt.item.ItemLinkCard;

/**
 * Created by LatvianModder on 29.01.2016.
 */
public class XPTItems
{
    public static BlockTeleporter teleporter;
    public static ItemLinkCard link_card;

    public static void init()
    {
        teleporter = XPT.mod.register("teleporter", new BlockTeleporter());
        link_card = XPT.mod.register("link_card", new ItemLinkCard());
    }
}