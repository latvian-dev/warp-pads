package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.IRecipes;
import com.feed_the_beast.ftbl.lib.item.ODItems;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.io.File;

/**
 * Created by LatvianModder on 20.09.2016.
 */
public enum FTBLibIntegration implements IFTBLibPlugin
{
    @FTBLibPlugin
    INSTANCE;

    public static FTBLibAPI API;

    @Override
    public void init(FTBLibAPI api)
    {
        API = api;
    }

    @Override
    public void registerCommon(IFTBLibRegistry reg)
    {
        reg.addConfigFileProvider(XPT.MOD_ID, () -> new File(LMUtils.folderConfig, "XPTeleporters.json"));

        reg.addConfig(XPT.MOD_ID, "general.enable_crafting", XPTConfig.ENABLE_CRAFTING).setInfo("Enable crafting recipes");
        reg.addConfig(XPT.MOD_ID, "general.levels_per_block", XPTConfig.LEVELS_PER_BLOCK).setInfo("Levels required to teleport in same dimension");
        reg.addConfig(XPT.MOD_ID, "general.levels_for_crossdim", XPTConfig.LEVELS_FOR_CROSSDIM).setInfo("Levels required to teleport to another dimension");
        reg.addConfig(XPT.MOD_ID, "general.soft_blocks", XPTConfig.SOFT_BLOCKS).setInfo("Soft blocks are like torches - you can walk trough them, BUT it solved the 'getting stuck in block' issue");

        reg.addSyncData(XPT.MOD_ID, new XPTSyncData());
    }

    @Override
    public void registerRecipes(IRecipes recipes)
    {
        //@formatter:off
        recipes.addRecipe(new ItemStack(XPTItems.teleporter),
                " G ", "DPD", "III",
                'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
                'P', ODItems.ENDERPEARL,
                'G', ODItems.GLOWSTONE,
                'D', ODItems.DIAMOND);
        //@formatter:on
    }
}