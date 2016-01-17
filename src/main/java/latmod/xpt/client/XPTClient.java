package latmod.xpt.client;

import ftb.lib.api.client.FTBLibClient;
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
		
		XPTItems.teleporter.registerModel();
		XPTItems.teleporter_recall.registerModel();
		XPTItems.link_card.registerModel();
		XPTItems.mirror.registerModel();
	}
}