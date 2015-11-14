package latmod.xpt;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.api.EventFTBReload;

public class XPTEventHandler
{
	@SubscribeEvent
	public void onReloaded(EventFTBReload e)
	{
		if(e.side.isServer())
		{
			XPTConfig.config.load();
			XPTConfig.reloadConfig();
		}
	}
}