package latmod.xpt;

import com.feed_the_beast.ftbl.api.events.SyncWorldEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LatvianModder on 19.05.2016.
 */
public class XPTEventHandler
{
    @SubscribeEvent
    public void syncData(SyncWorldEvent e)
    {
        if(e.world.getSide().isServer())
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
