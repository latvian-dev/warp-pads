package latmod.xpt.client;
import latmod.xpt.*;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	public void load()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileXPT.class, new RenderXPT());
	}
}