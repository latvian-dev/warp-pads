package latmod.xpt;

import com.feed_the_beast.ftbl.util.LMMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = XPT.MOD_ID, name = "XPTeleporters", version = "@VERSION@", dependencies = "required-after:ftbl")
public class XPT
{
    protected static final String MOD_ID = "xpt";

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
        if(XPTConfig.enable_crafting.getAsBoolean()) { mod.loadRecipes(); }
    }
}