package latmod.xpt.client;

import ftb.lib.api.client.FTBLibClient;
import latmod.xpt.XPTCommon;
import latmod.xpt.blocks.TileTeleporter;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	public void postInit()
	{
		FTBLibClient.addTileRenderer(TileTeleporter.class, RenderTeleporter.instance);
	}
}