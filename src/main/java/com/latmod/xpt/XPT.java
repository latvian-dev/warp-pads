package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.latmod.xpt.block.XPTNet;
import com.latmod.xpt.net.XPTNetHandler;
import net.minecraft.util.ResourceLocation;
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
        FTBLibAPI.get().getRegistries().syncedData().register(new ResourceLocation(MOD_ID, "config"), new XPTSyncConfig());
        FTBLibAPI.get().getRegistries().recipeHandlers().register(new ResourceLocation(MOD_ID, "blocks"), new XPTRecipes());

        XPTNetHandler.init();
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        XPTNet.clear();
    }
}