package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.feed_the_beast.ftbl.util.LMNetUtils;
import com.latmod.xpt.block.TileTeleporter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class MessageToggleActive extends MessageToServer<MessageToggleActive>
{
    public BlockPos pos;

    public MessageToggleActive()
    {
    }

    public MessageToggleActive(BlockPos p)
    {
        pos = p;
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
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = LMNetUtils.readPos(io);
    }

    @Override
    public void onMessage(MessageToggleActive m, EntityPlayerMP player)
    {
        TileEntity te = player.worldObj.getTileEntity(m.pos);

        if(te instanceof TileTeleporter)
        {
            TileTeleporter teleporter = (TileTeleporter) te;

            if(teleporter.security.isOwner(player))
            {
                teleporter.inactive = !teleporter.inactive;
                teleporter.markDirty();
            }
        }
    }
}
