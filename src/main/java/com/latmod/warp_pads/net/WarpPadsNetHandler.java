package com.latmod.warp_pads.net;

import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.latmod.warp_pads.WarpPads;

/**
 * Created by LatvianModder on 28.07.2016.
 */
public class WarpPadsNetHandler
{
    static final NetworkWrapper NET = NetworkWrapper.newWrapper(WarpPads.MOD_ID);

    public static void init()
    {
        NET.register(1, new MessageOpenWarpPadGui());
        NET.register(2, new MessageSelectTeleporter());
        NET.register(3, new MessageSetName());
        NET.register(4, new MessageToggleActive());
        NET.register(5, new MessageTogglePrivacy());
    }
}