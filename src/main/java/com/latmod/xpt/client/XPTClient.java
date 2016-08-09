package com.latmod.xpt.client;

import com.latmod.xpt.XPTCommon;
import com.latmod.xpt.XPTItems;
import com.latmod.xpt.block.TileTeleporter;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
    @Override
    public void preInit()
    {
        XPTItems.TELEPORTER.registerDefaultModel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporter.class, new RenderTeleporter());
    }
}