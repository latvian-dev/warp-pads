package latmod.xpt;

import latmod.ftbu.core.api.*;
import net.minecraft.command.ICommandSender;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;

@Optional.Interface(modid = FTBUIntegration.MOD_ID, iface = "latmod.ftbu.core.api.IFTBUReloadable")
public class FTBUIntegration implements IFTBUReloadable
{
	public static final String MOD_ID = "FTBU";
	public static final FTBUIntegration instance = new FTBUIntegration();
	
	@Optional.Method(modid = FTBUIntegration.MOD_ID)
	public static void registerHandlers()
	{
		FTBUReloadableRegistry.add(instance);
	}
	
	public void onReloaded(Side s, ICommandSender sender) throws Exception
	{
		if(s.isServer())
		{
			XPTConfig.config.load();
			XPTConfig.reloadConfig();
		}
	}
}