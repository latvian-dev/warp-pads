package latmod.xpt;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
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
		proxy.load();
		mod.onPostLoaded();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		if(XPTConfig.enable_crafting.getAsBoolean()) mod.loadRecipes();
	}
}