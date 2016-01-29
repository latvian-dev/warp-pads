package latmod.xpt;


import ftb.lib.LMMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = XPT.MOD_ID, name = "XPTeleporters", version = "@VERSION@", dependencies = "required-after:FTBL", acceptedMinecraftVersions = "[1.8.8,1.9)")
public class XPT
{
	protected static final String MOD_ID = "XPT";
	
	@Mod.Instance(XPT.MOD_ID)
	public static XPT inst;
	
	@SidedProxy(clientSide = "latmod.xpt.client.XPTClient", serverSide = "latmod.xpt.XPTCommon")
	public static XPTCommon proxy;
	
	public static LMMod mod;
	
	public CreativeTabs creativeTab;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = LMMod.create(XPT.MOD_ID);
		XPTConfig.load();
		XPTItems.init();
		
		creativeTab = mod.createTab("xpt", new ItemStack(XPTItems.teleporter));
		
		mod.onPostLoaded();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		if(XPTConfig.enable_crafting.get()) mod.loadRecipes();
		proxy.postInit();
	}
}