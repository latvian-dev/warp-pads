package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.events.SyncDataEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LatvianModder on 19.05.2016.
 */
public class XPTEventHandler
{
    @SubscribeEvent
    public void syncData(SyncDataEvent event)
    {
        if(event.getSide().isServer())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("SB", XPTConfig.soft_blocks.getAsBoolean());
            event.getData().setTag("XPT", tag);
        }
        else
        {
            NBTTagCompound tag = event.getData().getCompoundTag("XPT");
            XPTConfig.soft_blocks.set(tag.getBoolean("SB"));
        }
    }
}
