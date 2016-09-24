package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.ISyncData;
import com.feed_the_beast.ftbl.api.SyncData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 17.08.2016.
 */
public enum XPTSyncConfig implements ISyncData
{
    @SyncData
    INSTANCE;

    @Override
    public ResourceLocation getID()
    {
        return new ResourceLocation(XPT.MOD_ID, "config");
    }

    @Override
    public NBTTagCompound writeSyncData(EntityPlayerMP player, IForgePlayer forgePlayer)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("SB", XPTConfig.SOFT_BLOCKS.serializeNBT());
        return nbt;
    }

    @Override
    public void readSyncData(NBTTagCompound nbt)
    {
        XPTConfig.SOFT_BLOCKS.deserializeNBT(nbt.getTag("SB"));
    }
}