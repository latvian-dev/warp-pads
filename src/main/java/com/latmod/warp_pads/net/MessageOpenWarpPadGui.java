package com.latmod.warp_pads.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.latmod.warp_pads.block.TileWarpPad;
import com.latmod.warp_pads.block.WarpPadNode;
import com.latmod.warp_pads.client.GuiWarpPad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 28.07.2016.
 */
public class MessageOpenWarpPadGui extends MessageToClient<MessageOpenWarpPadGui>
{
    private BlockPos pos;
    private List<WarpPadNode> nodes;

    public MessageOpenWarpPadGui()
    {
    }

    public MessageOpenWarpPadGui(BlockPos p, List<WarpPadNode> t)
    {
        pos = p;
        nodes = t;
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
        io.writeInt(nodes.size());

        for(WarpPadNode t : nodes)
        {
            t.write(io);
        }
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        pos = LMNetUtils.readPos(io);
        int s = io.readInt();
        nodes = new ArrayList<>();

        for(int i = 0; i < s; i++)
        {
            WarpPadNode n = new WarpPadNode();
            n.read(io);
            nodes.add(n);
        }
    }

    @Override
    public void onMessage(MessageOpenWarpPadGui m, EntityPlayer player)
    {
        TileEntity te = player.world.getTileEntity(m.pos);

        if(te instanceof TileWarpPad)
        {
            new GuiWarpPad((TileWarpPad) te, m.nodes).openGui();
        }
    }
}