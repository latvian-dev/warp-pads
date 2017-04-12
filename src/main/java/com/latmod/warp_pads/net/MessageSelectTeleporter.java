package com.latmod.warp_pads.net;

import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.latmod.warp_pads.block.TileWarpPad;
import com.latmod.warp_pads.block.WarpPadsNet;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class MessageSelectTeleporter extends MessageToServer<MessageSelectTeleporter>
{
    private BlockPos pos;
    private BlockDimPos dst;

    public MessageSelectTeleporter()
    {
    }

    public MessageSelectTeleporter(BlockPos p, BlockDimPos p1)
    {
        pos = p;
        dst = p1;
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
        NetUtils.writeDimPos(io, dst);
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = NetUtils.readPos(io);
        dst = NetUtils.readDimPos(io);
    }

    @Override
    public void onMessage(MessageSelectTeleporter m, EntityPlayer player)
    {
        TileEntity te = player.world.getTileEntity(m.pos);

        if(te instanceof TileWarpPad)
        {
            TileWarpPad teleporter0 = (TileWarpPad) te;
            TileWarpPad teleporter = WarpPadsNet.get(m.dst);

            if(teleporter != null)
            {
                int levels = teleporter0.getEnergyRequired(teleporter);

                if(teleporter0.consumeEnergy(player, levels, true))
                {
                    teleporter0.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
                    ServerUtils.teleportPlayer(player, teleporter.getDimPos());
                    teleporter0.consumeEnergy(player, levels, false);
                    teleporter.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
                }
            }
        }
    }
}