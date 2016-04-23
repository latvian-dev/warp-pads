package latmod.xpt.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.client.FTBLibClient;
import latmod.xpt.XPTCommon;
import latmod.xpt.XPTItems;
import latmod.xpt.block.TileTeleporter;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	@Override
	public void load()
	{
		FTBLibClient.addTileRenderer(TileTeleporter.class, new RenderTeleporter());
		FTBLibClient.addItemRenderer(XPTItems.teleporter, new RenderTeleporter.Item());
	}
}