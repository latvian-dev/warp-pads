package latmod.xpt;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ftb.lib.EventBusHelper;
import ftb.lib.LMMod;

@Mod(modid = XPT.MOD_ID, name = "XPTeleporters", version = "@VERSION@", dependencies = "required-after:FTBL")
public class XPT
{
	protected static final String MOD_ID = "XPT";
	
	@Mod.Instance(XPT.MOD_ID)
	public static XPT inst;
	
	@SidedProxy(clientSide = "latmod.xpt.client.XPTClient", serverSide = "latmod.xpt.XPTCommon")
	public static XPTCommon proxy;
	
	public static LMMod mod;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = LMMod.create(XPT.MOD_ID);
		XPTItems.init();
		XPTConfig.load();
		mod.onPostLoaded();
		EventBusHelper.register(new XPTEventHandler());
		proxy.load();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		if(XPTConfig.enable_crafting.getAsBoolean()) mod.loadRecipes();
	}
}