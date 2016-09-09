package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.latmod.lib.util.LMNetUtils;
import com.latmod.lib.util.LMServerUtils;
import com.latmod.xpt.XPTConfig;
import com.latmod.xpt.block.TileTeleporter;
import com.latmod.xpt.block.XPTNet;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class MessageSelectTeleporter extends MessageToServer<MessageSelectTeleporter>
{
    private BlockPos pos;
    private UUID uuid;

    public MessageSelectTeleporter()
    {
    }

    public MessageSelectTeleporter(BlockPos p, UUID id)
    {
        pos = p;
        uuid = id;
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
        LMNetUtils.writeUUID(io, uuid);
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = LMNetUtils.readPos(io);
        uuid = LMNetUtils.readUUID(io);
    }

    @Override
    public void onMessage(MessageSelectTeleporter m, EntityPlayerMP player)
    {
        TileEntity te = player.worldObj.getTileEntity(m.pos);

        if(te instanceof TileTeleporter)
        {
            TileTeleporter teleporter0 = (TileTeleporter) te;

            TileTeleporter teleporter = XPTNet.get(m.uuid);

            if(teleporter != null)
            {
                int levels = teleporter0.getLevels(teleporter);

                if(XPTConfig.consumeLevels(player, levels, true))
                {
                    teleporter0.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
                    LMServerUtils.teleportPlayer(player, teleporter.getDimPos());
                    XPTConfig.consumeLevels(player, levels, false);
                    teleporter.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
                }
            }
        }
    }
}
