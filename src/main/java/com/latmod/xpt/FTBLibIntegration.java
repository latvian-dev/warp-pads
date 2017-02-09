package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.IRecipes;
import com.feed_the_beast.ftbl.lib.item.ODItems;
import com.latmod.xpt.item.XPTItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

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
        XPTConfig.init(reg);
        reg.addSyncData(XPT.MOD_ID, new XPTSyncData());
    }

    @Override
    public void registerRecipes(IRecipes recipes)
    {
        recipes.addRecipe(new ItemStack(XPTItems.TELEPORTER),
                " G ", "DPD", "III",
                'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
                'P', ODItems.ENDERPEARL,
                'G', ODItems.GLOWSTONE,
                'D', ODItems.DIAMOND);
    }
}