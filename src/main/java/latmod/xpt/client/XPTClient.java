package latmod.xpt.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.*;
import latmod.xpt.XPTCommon;
import latmod.xpt.block.TileTeleporter;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	public void load()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileTeleporter.class, RenderTeleporter.instance);
	}
}