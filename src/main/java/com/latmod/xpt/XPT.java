package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.item.ODItems;
import com.feed_the_beast.ftbl.api.recipes.LMRecipes;
import com.latmod.xpt.block.XPTNet;
import com.latmod.xpt.net.XPTNetHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        XPTItems.init();
        XPTConfig.load();
        proxy.preInit();
        FTBLibAPI.get().getRegistries().syncedData().register(new ResourceLocation(MOD_ID, "config"), new XPTSyncConfig());
        XPTNetHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if(XPTConfig.enable_crafting.getAsBoolean())
        {
            LMRecipes.INSTANCE.addRecipe(new ItemStack(XPTItems.TELEPORTER),
                    " G ",
                    "DPD",
                    "III",
                    'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
                    'P', ODItems.ENDERPEARL,
                    'G', ODItems.GLOWSTONE,
                    'D', ODItems.DIAMOND);
        }
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        XPTNet.clear();
    }
}