package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.events.RegisterRecipesEvent;
import com.feed_the_beast.ftbl.lib.item.ODItems;
import com.latmod.xpt.block.BlockTeleporter;
import com.latmod.xpt.block.TileTeleporter;
import com.latmod.xpt.client.RenderTeleporter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LatvianModder on 23.10.2016.
 */
public class XPTEventHandler
{
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) throws Exception
    {
        event.getRegistry().register(new BlockTeleporter().setRegistryName(XPT.MOD_ID, "teleporter"));
        GameRegistry.registerTileEntity(TileTeleporter.class, "xpt.teleporter");
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) throws Exception
    {
        event.getRegistry().register(new ItemBlock(XPTBlocks.teleporter).setRegistryName(XPT.MOD_ID, "teleporter"));
    }

    @SubscribeEvent
    public void registerRecipes(RegisterRecipesEvent event) throws Exception
    {
        //@formatter:off
        event.getRecipes().addRecipe(new ItemStack(XPTItems.teleporter),
                " G ", "DPD", "III",
                'I', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
                'P', ODItems.ENDERPEARL,
                'G', ODItems.GLOWSTONE,
                'D', ODItems.DIAMOND);
        //@formatter:on
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) throws Exception
    {
        ModelLoader.setCustomModelResourceLocation(XPTItems.teleporter, 0, new ModelResourceLocation(XPTItems.teleporter.getRegistryName(), "inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporter.class, new RenderTeleporter());
    }
}