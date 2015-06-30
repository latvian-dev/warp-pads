package latmod.xpt.client;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import latmod.xpt.*;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
	public void load()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileXPT.class, RenderXPT.instance);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(XPT.block), RenderXPT.instance);
	}
}