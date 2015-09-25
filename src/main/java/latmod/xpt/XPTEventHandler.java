package latmod.xpt;

import cpw.mods.fml.common.Optional;
import latmod.ftbu.api.EventFTBUReload;

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