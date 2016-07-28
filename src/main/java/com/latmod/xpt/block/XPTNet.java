package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.latmod.lib.RemoveFilter;
import com.latmod.lib.util.LMMapUtils;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 10.07.2016.
 */
public class XPTNet
{
    private static final Map<UUID, TileTeleporter> NET = new HashMap<>();
    private static final Comparator<TileTeleporter> COMPARATOR = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());
    private static final RemoveFilter<Map.Entry<UUID, TileTeleporter>> REMOVE_FILTER = entry -> !entry.getValue().security.hasOwner() || entry.getValue().isInvalid() || !entry.getValue().hasWorldObj();

    public static void clear()
    {
        NET.clear();
    }

    public static void cleanup()
    {
        LMMapUtils.removeAll(NET, REMOVE_FILTER);
    }

    public static TileTeleporter get(UUID id)
    {
        return NET.get(id);
    }

    public static void add(TileTeleporter teleporter)
    {
        if(teleporter.security.hasOwner() && teleporter.hasWorldObj() && teleporter.getSide().isServer())
        {
            NET.put(teleporter.getUUID(), teleporter);
            FTBLib.dev_logger.info("Added " + teleporter.getPos());
        }
    }

    public static void remove(TileTeleporter teleporter)
    {
        if(teleporter.hasWorldObj() && teleporter.getSide().isServer())
        {
            NET.remove(teleporter.getUUID());
            FTBLib.dev_logger.info("Removed " + teleporter.getPos());
        }
    }

    public static Collection<TileTeleporter> getTeleporters(EntityPlayerMP player)
    {
        ForgePlayer p = ForgeWorldMP.inst.getPlayer(player);
        List<TileTeleporter> list = new ArrayList<>();

        if(p == null || NET.isEmpty())
        {
            return list;
        }

        for(TileTeleporter tile : NET.values())
        {
            if(tile.security.hasOwner() && tile.hasWorldObj() && !tile.isInvalid())
            {
                ForgePlayer owner = ForgeWorldMP.inst.getPlayer(tile.security.getOwner());

                if(owner != null)
                {
                    if(tile.security.getPrivacyLevel().canInteract(owner, p))
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