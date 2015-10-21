package latmod.xpt;

import cpw.mods.fml.common.Optional;
import ftb.lib.api.EventFTBReload;

public class XPTEventHandler
{
	@Optional.Method(modid = "FTBL")
	public void onReloaded(EventFTBReload e)
	{
		if(e.side.isServer())
		{
			XPTConfig.config.load();
			XPTConfig.reloadConfig();
		}
	}
}