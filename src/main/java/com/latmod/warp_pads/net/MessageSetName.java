package com.latmod.warp_pads.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.latmod.warp_pads.block.TileWarpPad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class MessageSetName extends MessageToServer<MessageSetName>
{
    private BlockPos pos;
    private String name;

    public MessageSetName()
    {
    }

    public MessageSetName(BlockPos p, String n)
    {
        pos = p;
        name = n;
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return WarpPadsNetHandler.NET;
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        LMNetUtils.writePos(io, pos);
        ByteBufUtils.writeUTF8String(io, name);
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = LMNetUtils.readPos(io);
        name = ByteBufUtils.readUTF8String(io);
    }

    @Override
    public void onMessage(MessageSetName m, EntityPlayer player)
    {
        TileEntity te = player.world.getTileEntity(m.pos);

        if(te instanceof TileWarpPad)
        {
            TileWarpPad teleporter = (TileWarpPad) te;

            if(teleporter.isOwner(player.getGameProfile().getId()))
            {
                teleporter.setName(m.name);
            }
        }
    }
}