package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
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
    }
}