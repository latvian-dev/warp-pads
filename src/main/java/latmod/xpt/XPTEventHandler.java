package latmod.xpt;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ftb.lib.api.EventFTBSync;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by LatvianModder on 23.04.2016.
 */
public class XPTEventHandler
{
	@SubscribeEvent
	public void syncData(EventFTBSync e)
	{
		if(e.side.isServer())
		{
			NBTTagCompound tag = new NBTTagCompound();
			
			tag.setShort("CS", (short) XPTConfig.cooldown_seconds.getAsInt());
			tag.setBoolean("SB", XPTConfig.soft_blocks.getAsBoolean());
			
			e.syncData.setTag("XPT", tag);
		}
		else
		{
			NBTTagCompound tag = e.syncData.getCompoundTag("XPT");
			
			XPTConfig.cooldown_seconds.set(tag.getShort("CS"));
			XPTConfig.soft_blocks.set(tag.getBoolean("SB"));
		}
	}
}
