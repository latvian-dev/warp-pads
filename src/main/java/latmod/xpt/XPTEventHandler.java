package latmod.xpt;

import latmod.ftbu.core.api.EventFTBUReload;
import cpw.mods.fml.common.Optional;

public class XPTEventHandler
{
	@Optional.Method(modid = "FTBU")
	public void onReloaded(EventFTBUReload e)
	{
		if(e.side.isServer())
		{
			XPTConfig.config.load();
			XPTConfig.reloadConfig();
		}
	}
}