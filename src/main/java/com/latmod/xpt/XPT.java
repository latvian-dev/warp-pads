package com.latmod.xpt;

import com.latmod.xpt.block.XPTNet;
import com.latmod.xpt.net.XPTNetHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = XPT.MOD_ID, name = "XPTeleporters", version = "@VERSION@", dependencies = "required-after:ftbl")
public class XPT
{
    public static final String MOD_ID = "xpt";

    @Mod.Instance(XPT.MOD_ID)
    public static XPT inst;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new XPTEventHandler());
        XPTNetHandler.init();
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        XPTNet.clear();
    }
}