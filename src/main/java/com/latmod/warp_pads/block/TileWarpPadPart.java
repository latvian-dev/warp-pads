package com.latmod.warp_pads.block;

import com.feed_the_beast.ftbl.lib.block.EnumHorizontalOffset;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 19.02.2017.
 */
public class TileWarpPadPart extends TileWarpPadBase
{
    private EnumHorizontalOffset part;
    private TileWarpPad warpPad = null;

    public TileWarpPadPart()
    {
    }

    public TileWarpPadPart(EnumHorizontalOffset p)
    {
        part = p;
        checkUpdates = false;
    }

    @Override
    public void writeTileData(NBTTagCompound nbt)
    {
        nbt.setByte("Part", (byte) getPart().ordinal());
    }

    @Override
    public void readTileData(NBTTagCompound nbt)
    {
        part = EnumHorizontalOffset.VALUES[nbt.getByte("Part")];
    }

    @Override
    public void updateContainingBlockInfo()
    {
        super.updateContainingBlockInfo();
        warpPad = null;
    }

    @Override
    public EnumHorizontalOffset getPart()
    {
        if(part == null)
        {
            part = EnumHorizontalOffset.VALUES[getBlockMetadata()];
        }

        return part;
    }

    @Nullable
    private TileWarpPad getWarpPad()
    {
        if(warpPad == null)
        {
            TileEntity tileEntity = world.getTileEntity(getPart().offset(pos));

            if(tileEntity instanceof TileWarpPad)
            {
                warpPad = (TileWarpPad) tileEntity;
            }
        }

        return warpPad;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
        TileWarpPad pad = getWarpPad();
        return pad != null && pad.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        TileWarpPad pad = getWarpPad();
        return pad != null ? pad.getCapability(capability, facing) : null;
    }
}