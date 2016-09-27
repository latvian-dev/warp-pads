package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.latmod.xpt.block.TileTeleporter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

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
        return XPTNetHandler.NET;
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        LMNetUtils.writePos(io, pos);
        LMNetUtils.writeString(io, name);
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = LMNetUtils.readPos(io);
        name = LMNetUtils.readString(io);
    }

    @Override
    public void onMessage(MessageSetName m, EntityPlayerMP player)
    {
        TileEntity te = player.worldObj.getTileEntity(m.pos);

        if(te instanceof TileTeleporter)
        {
            TileTeleporter teleporter = (TileTeleporter) te;

            if(teleporter.security.isOwner(player))
            {
                teleporter.setName(m.name);
            }
        }
    }
}
