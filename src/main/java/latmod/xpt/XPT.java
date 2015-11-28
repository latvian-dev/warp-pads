package latmod.xpt;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = XPT.MOD_ID, name = "XPTeleporters", version = "@VERSION@", dependencies = "required-after:FTBL")
public class XPT
{
	protected static final String MOD_ID = "XPT";
	
	@Mod.Instance(XPT.MOD_ID)
	public static XPT inst;
	
	@SidedProxy(clientSide = "latmod.xpt.client.XPTClient", serverSide = "latmod.xpt.XPTCommon")
	public static XPTCommon proxy;
	
	public static final BlockTeleporter teleporter = new BlockTeleporter();
	public static final BlockTeleporterRecall teleporter_recall = new BlockTeleporterRecall();
	public static final ItemLinkCard link_card = new ItemLinkCard();
	public static final ItemMagicalMirror mirror = new ItemMagicalMirror();
	public static final CreativeTabXPT creativeTab = new CreativeTabXPT();
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		XPTConfig.load();
		
		teleporter.setBlockName("xpt:teleporter");
		teleporter.setHardness(1F);
		teleporter.setResistance(100000F);
		teleporter.setCreativeTab(creativeTab);
		
		teleporter_recall.setBlockName("xpt:teleporter_recall");
		teleporter_recall.setHardness(1F);
		teleporter_recall.setResistance(100000F);
		teleporter_recall.setCreativeTab(creativeTab);
		
		link_card.setUnlocalizedName("xpt:link_card");
		link_card.setTextureName("xpt:link_card");
		link_card.setCreativeTab(creativeTab);
		link_card.setMaxStackSize(1);
		
		mirror.setUnlocalizedName("xpt:mirror");
		mirror.setTextureName("xpt:mirror");
		mirror.setCreativeTab(creativeTab);
		mirror.setMaxStackSize(1);
		
		GameRegistry.registerBlock(teleporter, "teleporter");
		GameRegistry.registerBlock(teleporter_recall, "teleporter_recall");
		GameRegistry.registerItem(link_card, "link_card");
		GameRegistry.registerItem(mirror, "mirror");
		GameRegistry.registerTileEntity(TileTeleporter.class, "xpt.teleporter");
		
		proxy.load();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		if(XPTConfig.enable_crafting.get())
		{
			teleporter.loadRecipes();
			teleporter_recall.loadRecipes();
			link_card.loadRecipes();
			mirror.loadRecipes();
		}
	}
}