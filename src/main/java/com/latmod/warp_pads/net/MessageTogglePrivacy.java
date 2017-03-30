package com.latmod.warp_pads.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.latmod.warp_pads.block.TileWarpPad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class MessageTogglePrivacy extends MessageToServer<MessageTogglePrivacy>
{
    private BlockPos pos;
    private boolean next;

    public MessageTogglePrivacy()
    {
    }

    public MessageTogglePrivacy(BlockPos p, boolean n)
    {
        pos = p;
        next = n;
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
        io.writeBoolean(next);
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = LMNetUtils.readPos(io);
        next = io.readBoolean();
    }

    @Override
    public void onMessage(MessageTogglePrivacy m, EntityPlayer player)
    {
        TileEntity te = player.world.getTileEntity(m.pos);

        if(te instanceof TileWarpPad)
        {
            TileWarpPad teleporter = (TileWarpPad) te;

            if(teleporter.isOwner(player.getGameProfile().getId()))
            {
                teleporter.togglePrivacyLevel();
            }
        }
    }
}