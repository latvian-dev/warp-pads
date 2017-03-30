package com.latmod.warp_pads.block;

import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Created by LatvianModder on 28.07.2016.
 */
public class WarpPadNode
{
    public BlockDimPos pos;
    public String name;
    public int energy;
    public boolean available;

    public void write(ByteBuf io)
    {
        LMNetUtils.writeDimPos(io, pos);
        ByteBufUtils.writeUTF8String(io, name);
        io.writeInt(energy);
        io.writeBoolean(available);
    }

    public void read(ByteBuf io)
    {
        pos = LMNetUtils.readDimPos(io);
        name = ByteBufUtils.readUTF8String(io);
        energy = io.readInt();
        available = io.readBoolean();
    }

    public int hashCode()
    {
        return pos.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        else if(o == this)
        {
            return true;
        }
        else if(o instanceof WarpPadNode)
        {
            return ((WarpPadNode) o).pos.equals(pos);
        }
        else if(o instanceof BlockDimPos)
        {
            return o.equals(pos);
        }

        return false;
    }

    public String toString()
    {
        return name;
    }
}