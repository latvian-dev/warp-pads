package latmod.xpt;

import ftb.lib.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = XPT.MOD_ID, name = "XPTeleporters", version = "@VERSION@", dependencies = "required-after:FTBL")
public class XPT
{
	protected static final String MOD_ID = "XPT";
	
	@Mod.Instance(XPT.MOD_ID)
	public static XPT inst;
	
	@SidedProxy(clientSide = "latmod.xpt.client.XPTClient", serverSide = "latmod.xpt.XPTCommon")
	public static XPTCommon proxy;
	
	public static LMMod mod;
	public static CreativeTabLM tab;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = LMMod.create(XPT.MOD_ID);
		tab = new CreativeTabLM("xpt").setMod(mod);
		XPTItems.init();
		XPTConfig.load();
		tab.addIcon(new ItemStack(XPTItems.teleporter));
		proxy.load();
		mod.onPostLoaded();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		if(XPTConfig.enable_crafting.get()) mod.loadRecipes();
	}
}