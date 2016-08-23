package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.item.ODItems;
import com.feed_the_beast.ftbl.api.recipes.IRecipeHandler;
import com.feed_the_beast.ftbl.api.recipes.IRecipes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * Created by LatvianModder on 23.08.2016.
 */
public class XPTRecipes implements IRecipeHandler
{
    @Override
    public boolean isActive()
    {
        return XPTConfig.enable_crafting.getAsBoolean();
    }

    @Override
    public void loadRecipes(IRecipes recipes)
    {
        recipes.addRecipe(new ItemStack(XPTItems.TELEPORTER),
                " G ", "DPD", "III",
                'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
                'P', ODItems.ENDERPEARL,
                'G', ODItems.GLOWSTONE,
                'D', ODItems.DIAMOND);
    }
}