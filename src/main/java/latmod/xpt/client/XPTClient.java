package latmod.xpt.client;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.FTBLibClient;
import latmod.xpt.*;
import latmod.xpt.block.TileTeleporter;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	public void load()
	{
		FTBLibClient.addTileRenderer(TileTeleporter.class, new RenderTeleporter());
		FTBLibClient.addItemRenderer(XPTItems.teleporter, new RenderTeleporter.Item());
	}
}