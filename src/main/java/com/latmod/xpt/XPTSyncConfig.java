package com.latmod.xpt;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by LatvianModder on 17.08.2016.
 */
public class XPTSyncConfig implements INBTSerializable<NBTTagCompound>
{
    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("SB", XPTConfig.soft_blocks.getAsBoolean());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        XPTConfig.soft_blocks.set(nbt.getBoolean("SB"));
    }
}