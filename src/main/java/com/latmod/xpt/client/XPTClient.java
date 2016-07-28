package com.latmod.xpt.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.latmod.xpt.XPTCommon;
import com.latmod.xpt.block.TileTeleporter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
    @Override
    public void preInit()
    {
        FTBLibClient.addTileRenderer(TileTeleporter.class, new RenderTeleporter());
    }
}