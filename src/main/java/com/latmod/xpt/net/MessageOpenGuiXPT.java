package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.latmod.xpt.block.TileTeleporter;
import com.latmod.xpt.block.XPTNode;
import com.latmod.xpt.client.GuiTeleporters;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by LatvianModder on 28.07.2016.
 */
public class MessageOpenGuiXPT extends MessageToClient<MessageOpenGuiXPT>
{
    public BlockPos teleporterPos;
    public List<XPTNode> teleporters;

    public MessageOpenGuiXPT()
    {
    }

    public MessageOpenGuiXPT(BlockPos pos, List<XPTNode> t)
    {
        teleporterPos = pos;
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
        io.writeInt(teleporterPos.getX());
        io.writeInt(teleporterPos.getY());
        io.writeInt(teleporterPos.getZ());
        io.writeInt(teleporters.size());

        for(XPTNode t : teleporters)
        {
            writeUUID(io, t.uuid);
            writeString(io, t.name);
            io.writeShort(t.levels);
            io.writeBoolean(t.available);
        }
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        int x = io.readInt();
        int y = io.readInt();
        int z = io.readInt();
        teleporterPos = new BlockPos(x, y, z);
        int s = io.readInt();
        teleporters = new ArrayList<>();

        for(int i = 0; i < s; i++)
        {
            UUID id = readUUID(io);
            String n = readString(io);
            int l = io.readUnsignedShort();
            boolean a = io.readBoolean();
            teleporters.add(new XPTNode(id, n, l, a));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage(MessageOpenGuiXPT m, Minecraft mc)
    {
        TileEntity te = mc.theWorld.getTileEntity(m.teleporterPos);

        if(te instanceof TileTeleporter)
        {
            new GuiTeleporters((TileTeleporter) te, m.teleporters).openGui();
        }
    }
}