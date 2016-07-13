package com.latmod.xpt;

import com.feed_the_beast.ftbl.util.LMMod;
import com.latmod.xpt.block.XPTNet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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

    public static LMMod mod;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        mod = LMMod.create(XPT.MOD_ID);
        XPTItems.init();
        XPTConfig.load();
        proxy.load();
        MinecraftForge.EVENT_BUS.register(new XPTEventHandler());
        mod.onPostLoaded();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if(XPTConfig.enable_crafting.getAsBoolean())
        {
            mod.loadRecipes();
        }
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        XPTNet.clear();
    }
}