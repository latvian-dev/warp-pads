package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.RegistryObject;
import com.feed_the_beast.ftbl.api.recipes.IRecipeHandler;
import com.feed_the_beast.ftbl.lib.item.ODItems;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.latmod.xpt.block.BlockTeleporter;
import com.latmod.xpt.block.TileTeleporter;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 29.01.2016.
 */
public class XPTItems
{
    public static final BlockTeleporter TELEPORTER = new BlockTeleporter();

    public static void init()
    {
        LMUtils.register(new ResourceLocation(XPT.MOD_ID, "teleporter"), TELEPORTER);
        LMUtils.addTile(TileTeleporter.class, TELEPORTER.getRegistryName());
    }

    @RegistryObject
    public static final IRecipeHandler RECIPES = recipes ->
    {
        recipes.addRecipe(new ItemStack(XPTItems.TELEPORTER),
                " G ", "DPD", "III",
                'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
                'P', ODItems.ENDERPEARL,
                'G', ODItems.GLOWSTONE,
                'D', ODItems.DIAMOND);
    };
}