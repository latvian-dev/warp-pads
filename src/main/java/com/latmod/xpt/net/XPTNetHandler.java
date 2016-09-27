package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.latmod.xpt.XPT;

/**
 * Created by LatvianModder on 28.07.2016.
 */
public class XPTNetHandler
{
    static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper(XPT.MOD_ID);

    public static void init()
    {
        NET.register(1, new MessageOpenGuiXPT());
        NET.register(2, new MessageSelectTeleporter());
        NET.register(3, new MessageSetName());
        NET.register(4, new MessageToggleActive());
        NET.register(5, new MessageTogglePrivacy());
    }
}