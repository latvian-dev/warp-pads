package com.latmod.warp_pads.net;

import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.latmod.warp_pads.block.TileWarpPad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class MessageToggleActive extends MessageToServer<MessageToggleActive>
{
    private BlockPos pos;

    public MessageToggleActive()
    {
    }

    public MessageToggleActive(BlockPos p)
    {
        pos = p;
    }

    @Override
    public NetworkWrapper getWrapper()
    {
        return WarpPadsNetHandler.NET;
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        NetUtils.writePos(io, pos);
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = NetUtils.readPos(io);
    }

    @Override
    public void onMessage(MessageToggleActive m, EntityPlayer player)
    {
        TileEntity te = player.world.getTileEntity(m.pos);

        if(te instanceof TileWarpPad)
        {
            TileWarpPad teleporter = (TileWarpPad) te;

            if(teleporter.isOwner(player.getGameProfile().getId()))
            {
                teleporter.inactive = !teleporter.inactive;
                teleporter.markDirty();
            }
        }
    }
}