package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IUniverse;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.latmod.xpt.FTBLibIntegration;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
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

    public static void clear()
    {
        NET.clear();
    }

    @Nullable
    public static TileTeleporter get(UUID id)
    {
        return NET.get(id);
    }

    public static void add(TileTeleporter teleporter)
    {
        if(teleporter.getOwner() != null && teleporter.hasWorldObj() && teleporter.isServerSide())
        {
            NET.put(teleporter.getUUID(), teleporter);
            LMUtils.DEV_LOGGER.info("Added " + teleporter.getPos());
        }
    }

    public static void remove(TileTeleporter teleporter)
    {
        if(teleporter.hasWorldObj() && teleporter.isServerSide())
        {
            NET.remove(teleporter.getUUID());
            LMUtils.DEV_LOGGER.info("Removed " + teleporter.getPos());
        }
    }

    public static Collection<TileTeleporter> getTeleporters(EntityPlayerMP player)
    {
        IUniverse world = FTBLibIntegration.API.getUniverse();

        IForgePlayer p = world.getPlayer(player);
        List<TileTeleporter> list = new ArrayList<>();

        if(p == null || NET.isEmpty())
        {
            return list;
        }

        for(TileTeleporter tile : NET.values())
        {
            if(tile.getOwner() != null && tile.hasWorldObj() && !tile.isInvalid())
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