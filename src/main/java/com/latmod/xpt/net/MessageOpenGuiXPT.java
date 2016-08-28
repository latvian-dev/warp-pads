package com.latmod.xpt.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.latmod.lib.util.LMNetUtils;
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
            LMNetUtils.writeString(io, t.name);
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
            String n = LMNetUtils.readString(io);
            int l = io.readUnsignedShort();
            boolean a = io.readBoolean();
            teleporters.add(new XPTNode(id, n, l, a));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage(MessageOpenGuiXPT m, Minecraft mc)
    {
        TileEntity te = mc.theWorld.getTileEntity(m.pos);

        if(te instanceof TileTeleporter)
        {
            new GuiTeleporters((TileTeleporter) te, m.teleporters).openGui();
        }
    }
}