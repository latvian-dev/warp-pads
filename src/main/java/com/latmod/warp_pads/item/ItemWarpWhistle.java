package com.latmod.warp_pads.item;

import com.feed_the_beast.ftbl.lib.item.ItemLM;
import com.latmod.warp_pads.WarpPads;

/**
 * Created by LatvianModder on 19.02.2017.
 */
public class ItemWarpWhistle extends ItemLM
{
    public ItemWarpWhistle()
    {
        super(WarpPads.MOD_ID + ":warp_whistle");
        setMaxStackSize(1);
    }
}