package com.latmod.xpt;

import com.latmod.xpt.block.XPTNet;
import com.latmod.xpt.net.XPTNetHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = XPT.MOD_ID, name = "XPTeleporters", version = "@VERSION@", dependencies = "required-after:ftbl")
public class XPT
{
    public static final String MOD_ID = "xpt";

    @Mod.Instance(XPT.MOD_ID)
    public static XPT inst;

    @SidedProxy(clientSide = "com.latmod.xpt.client.XPTClient", serverSide = "com.latmod.xpt.XPTCommon")
    public static XPTCommon proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        XPTItems.init();
        proxy.preInit();
        XPTNetHandler.init();
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        XPTNet.clear();
    }
}