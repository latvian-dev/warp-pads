package com.latmod.warp_pads.block;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.latmod.warp_pads.FTBLibIntegration;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LatvianModder on 10.07.2016.
 */
public class WarpPadsNet
{
    private static final Map<BlockDimPos, TileWarpPad> NET = new HashMap<>();
    private static final Comparator<TileWarpPad> COMPARATOR = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());

    public static void clear()
    {
        NET.clear();
    }

    @Nullable
    public static TileWarpPad get(BlockDimPos pos)
    {
        return NET.get(pos);
    }

    public static void add(TileWarpPad teleporter)
    {
        if(teleporter.getOwner() != null && teleporter.hasWorld() && teleporter.isServerSide())
        {
            NET.put(teleporter.getDimPos(), teleporter);
        }
    }

    public static void remove(TileWarpPad teleporter)
    {
        if(teleporter.hasWorld() && teleporter.isServerSide())
        {
            NET.remove(teleporter.getDimPos());
        }
    }

    public static Collection<TileWarpPad> getTeleporters(EntityPlayerMP player)
    {
        IUniverse world = FTBLibIntegration.API.getUniverse();

        IForgePlayer p = world.getPlayer(player);
        List<TileWarpPad> list = new ArrayList<>();

        if(p == null || NET.isEmpty())
        {
            return list;
        }

        for(TileWarpPad tile : NET.values())
        {
            if(tile.getOwner() != null && tile.hasWorld() && !tile.isInvalid())
            {
                IForgePlayer owner = world.getPlayer(tile.getOwner());

                if(owner != null)
                {
                    if(p.canInteract(owner, tile.getPrivacyLevel()))
                    {
                        list.add(tile);
                    }
                }
            }
        }

        if(list.size() > 1)
        {
            Collections.sort(list, COMPARATOR);
        }

        return list;
    }
}