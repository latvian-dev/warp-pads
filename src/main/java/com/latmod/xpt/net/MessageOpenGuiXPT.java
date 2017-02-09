package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.latmod.xpt.block.TileTeleporter;
import com.latmod.xpt.block.XPTNode;
import com.latmod.xpt.client.GuiTeleporters;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by LatvianModder on 28.07.2016.
 */
public class MessageOpenGuiXPT extends MessageToClient<MessageOpenGuiXPT>
{
    private BlockPos pos;
    private List<XPTNode> teleporters;

    public MessageOpenGuiXPT()
    {
    }

    public MessageOpenGuiXPT(BlockPos p, List<XPTNode> t)
    {
        pos = p;
        teleporters = t;
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
        io.writeInt(teleporters.size());

        for(XPTNode t : teleporters)
        {
            LMNetUtils.writeUUID(io, t.uuid);
            ByteBufUtils.writeUTF8String(io, t.name);
            io.writeShort(t.levels);
            io.writeBoolean(t.available);
        }
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = LMNetUtils.readPos(io);
        int s = io.readInt();
        teleporters = new ArrayList<>();

        for(int i = 0; i < s; i++)
        {
            UUID id = LMNetUtils.readUUID(io);
            String n = ByteBufUtils.readUTF8String(io);
            int l = io.readUnsignedShort();
            boolean a = io.readBoolean();
            teleporters.add(new XPTNode(id, n, l, a));
        }
    }

    @Override
    public void onMessage(MessageOpenGuiXPT m, EntityPlayer player)
    {
        TileEntity te = player.worldObj.getTileEntity(m.pos);

        if(te instanceof TileTeleporter)
        {
            new GuiTeleporters((TileTeleporter) te, m.teleporters).openGui();
        }
    }
}