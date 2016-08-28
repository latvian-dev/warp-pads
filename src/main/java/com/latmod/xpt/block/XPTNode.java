package com.latmod.xpt.block;

import java.util.UUID;

/**
 * Created by LatvianModder on 28.07.2016.
 */
public class XPTNode
{
    public final UUID uuid;
    public final String name;
    public final int levels;
    public final boolean available;

    public XPTNode(UUID id, String n, int l, boolean a)
    {
        uuid = id;
        name = n;
        levels = l;
        available = a;
    }

    public int hashCode()
    {
        return uuid.hashCode();
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
        else if(o instanceof XPTNode)
        {
            return ((XPTNode) o).uuid.equals(uuid);
        }
        else if(o instanceof UUID)
        {
            return o.equals(uuid);
        }

        return false;
    }

    public String toString()
    {
        return name;
    }
}
