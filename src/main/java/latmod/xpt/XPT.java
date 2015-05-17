package latmod.xpt;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = XPT.MOD_ID, name = "XP Teleporters", version = "@VERSION@")
public class XPT
{
	protected static final String MOD_ID = "XPT";
	
	@Mod.Instance(XPT.MOD_ID)
	public static XPT inst;
	
	@SidedProxy(clientSide = "latmod.xpt.client.XPTClient", serverSide = "latmod.xpt.XPTCommon")
	public static XPTCommon proxy;
	
	public static final BlockXPT block = new BlockXPT();
	public static final ItemXPT item = new ItemXPT();
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		XPTConfig.load(e);
		
		block.setBlockName("xpt:teleporter");
		block.setHardness(1F);
		block.setCreativeTab(CreativeTabs.tabTransport);
		
		item.setUnlocalizedName("xpt:link_card");
		item.setTextureName("xpt:link_card");
		item.setCreativeTab(CreativeTabs.tabTransport);
		item.setMaxStackSize(1);
		
		GameRegistry.registerBlock(block, "teleporter");
		GameRegistry.registerItem(item, "link_card");
		GameRegistry.registerTileEntity(TileXPT.class, "xpt.teleporter");
		
		proxy.load();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		if(XPTConfig.enable_crafting)
		{
			block.loadRecipes();
			item.loadRecipes();
		}
	}
}