package com.latmod.warp_pads;

import com.latmod.warp_pads.block.WarpPadsNet;
import com.latmod.warp_pads.net.WarpPadsNetHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = WarpPads.MOD_ID, name = "Warp Pads", version = "@VERSION@", dependencies = "required-after:ftbl")
public class WarpPads
{
    public static final String MOD_ID = "warp_pads";

    @Mod.Instance(WarpPads.MOD_ID)
    public static WarpPads inst;

    @SidedProxy(serverSide = "com.latmod.warp_pads.WarpPadsCommon", clientSide = "com.latmod.warp_pads.client.WarpPadsClient")
    public static WarpPadsCommon PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        WarpPadsNetHandler.init();
        PROXY.preInit();
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        WarpPadsNet.clear();
    }
}