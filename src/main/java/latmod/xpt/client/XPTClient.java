package latmod.xpt.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.*;
import latmod.xpt.*;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	public void load()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporter.class, RenderTeleporter.instance);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(XPT.teleporter), RenderTeleporter.instance);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(XPT.teleporter_recall), RenderTeleporterRecall.instance);
	}
}