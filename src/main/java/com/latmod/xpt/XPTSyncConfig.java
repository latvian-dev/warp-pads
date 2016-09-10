package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.ISyncData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by LatvianModder on 17.08.2016.
 */
public class XPTSyncConfig implements ISyncData
{
    @Override
    public NBTTagCompound writeSyncData(EntityPlayerMP player, IForgePlayer forgePlayer)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("SB", XPTConfig.soft_blocks.getAsBoolean());
        return nbt;
    }

    @Override
    public void readSyncData(NBTTagCompound nbt)
    {
        XPTConfig.soft_blocks.set(nbt.getBoolean("SB"));
    }
}