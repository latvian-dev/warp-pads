package latmod.xpt.client;

import ftb.lib.FTBLibClient;
import latmod.xpt.*;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	public void load()
	{
		FTBLibClient.addTileRenderer(TileTeleporter.class, RenderTeleporter.instance);
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(XPT.teleporter), RenderTeleporter.instance);
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(XPT.teleporter_recall), RenderTeleporterRecall.instance);
	}
}