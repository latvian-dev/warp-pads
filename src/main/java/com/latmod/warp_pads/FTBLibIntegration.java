package com.latmod.warp_pads;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.IRecipes;
import com.feed_the_beast.ftbl.lib.item.ODItems;
import com.latmod.warp_pads.item.WarpPadsItems;
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
        WarpPadsConfig.init(reg);
    }

    @Override
    public void registerRecipes(IRecipes recipes)
    {
        recipes.addRecipe(new ItemStack(WarpPadsItems.WARP_PAD, 2),
                "EDE", "QBQ",
                'E', ODItems.ENDERPEARL,
                'B', Blocks.BEACON,
                'D', ODItems.DIAMOND,
                'Q', ODItems.QUARTZ_BLOCK);
    }
}