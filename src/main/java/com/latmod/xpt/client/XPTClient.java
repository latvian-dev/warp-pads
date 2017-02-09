package com.latmod.xpt.client;

import com.latmod.xpt.XPTCommon;
import com.latmod.xpt.block.TileTeleporter;
import com.latmod.xpt.item.XPTItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by LatvianModder on 23.01.2017.
 */
public class XPTClient extends XPTCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(XPTItems.TELEPORTER), 0, new ModelResourceLocation(XPTItems.TELEPORTER.getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporter.class, new RenderTeleporter());
    }
}
