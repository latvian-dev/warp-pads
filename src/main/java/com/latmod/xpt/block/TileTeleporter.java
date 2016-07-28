package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.security.ISecure;
import com.feed_the_beast.ftbl.api.security.Security;
import com.feed_the_beast.ftbl.api.tile.TileClientActionRegistry;
import com.feed_the_beast.ftbl.api.tile.TileLM;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.latmod.lib.util.LMUtils;
import com.latmod.xpt.XPT;
import com.latmod.xpt.XPTConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TileTeleporter extends TileLM implements ITickable
{
    public static final ResourceLocation TELEPORT = new ResourceLocation(XPT.MOD_ID, "tp");
    public static final ResourceLocation SET_NAME = new ResourceLocation(XPT.MOD_ID, "name");
    public static final ResourceLocation TOGGLE_PRIVACY = new ResourceLocation(XPT.MOD_ID, "privacy");
    public static final ResourceLocation TOGGLE_ACTIVE = new ResourceLocation(XPT.MOD_ID, "active");

    static
    {
        TileClientActionRegistry.INSTANCE.register(TELEPORT, (te, data, player) ->
        {
            if(te instanceof TileTeleporter)
            {
                TileTeleporter teleporter0 = (TileTeleporter) te;

                TileTeleporter teleporter = XPTNet.get(new UUID(data.getLong("M"), data.getLong("L")));

                if(teleporter != null)
                {
                    int levels = teleporter0.getLevels(teleporter);

                    if(XPTConfig.consumeLevels(player, levels, true))
                    {
                        teleporter0.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
                        LMDimUtils.teleportPlayer(player, teleporter.getDimPos());
                        XPTConfig.consumeLevels(player, levels, false);
                        teleporter.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
                    }
                }
            }
        });

        TileClientActionRegistry.INSTANCE.register(SET_NAME, (te, data, player) ->
        {
            if(te instanceof TileTeleporter)
            {
                TileTeleporter teleporter = (TileTeleporter) te;

                if(!teleporter.security.hasOwner() || teleporter.security.getOwner().equals(player.getGameProfile().getId()))
                {
                    teleporter.name = data.getString("N");

                    if(teleporter.name.isEmpty())
                    {
                        teleporter.name = null;
                    }

                    teleporter.markDirty();
                }
            }
        });

        TileClientActionRegistry.INSTANCE.register(TOGGLE_PRIVACY, (te, data, player) ->
        {
            if(te instanceof TileTeleporter)
            {
                TileTeleporter teleporter = (TileTeleporter) te;

                if(!teleporter.security.hasOwner() || teleporter.security.getOwner().equals(player.getGameProfile().getId()))
                {
                    //
                    teleporter.markDirty();
                }
            }
        });

        TileClientActionRegistry.INSTANCE.register(TOGGLE_ACTIVE, (te, data, player) ->
        {
            if(te instanceof TileTeleporter)
            {
                TileTeleporter teleporter = (TileTeleporter) te;

                if(!teleporter.security.hasOwner() || teleporter.security.getOwner().equals(player.getGameProfile().getId()))
                {
                    teleporter.inactive = !teleporter.inactive;
                    teleporter.markDirty();
                }
            }
        });
    }

    public boolean inactive;
    private UUID uuid;
    private String name;

    @Override
    protected Security createSecurity()
    {
        return new Security(ISecure.SAVE_OWNER | ISecure.SAVE_PRIVACY_LEVEL);
    }

    @Nonnull
    public String getName()
    {
        return name == null ? LMUtils.fromUUID(getUUID()) : name;
    }

    @Nonnull
    public UUID getUUID()
    {
        if(uuid == null)
        {
            uuid = UUID.randomUUID();
        }

        return uuid;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        XPTNet.add(this);
    }

    @Override
    public void invalidate()
    {
        XPTNet.remove(this);
        super.invalidate();
    }

    @Override
    public void readTileData(@Nonnull NBTTagCompound tag)
    {
        super.readTileData(tag);

        uuid = LMUtils.fromString(tag.getString("UUID"));

        if(tag.hasKey("Name"))
        {
            name = tag.getString("Name");
        }
        else
        {
            name = null;
        }

        inactive = tag.getBoolean("Inactive");
    }

    @Override
    public void writeTileData(@Nonnull NBTTagCompound tag)
    {
        super.writeTileData(tag);

        tag.setString("UUID", LMUtils.fromUUID(getUUID()));

        if(name != null && !name.isEmpty())
        {
            tag.setString("Name", name);
        }

        if(inactive)
        {
            tag.setBoolean("Inactive", true);
        }
    }

    @Override
    public void readTileClientData(@Nonnull NBTTagCompound tag)
    {
        uuid = LMUtils.fromString(tag.getString("U"));

        if(tag.hasKey("N"))
        {
            name = tag.getString("N");
        }
        else
        {
            name = null;
        }

        inactive = tag.getBoolean("I");
    }

    @Override
    public void writeTileClientData(@Nonnull NBTTagCompound tag)
    {
        tag.setString("U", LMUtils.fromUUID(getUUID()));

        if(name != null && !name.isEmpty())
        {
            tag.setString("N", name);
        }

        if(inactive)
        {
            tag.setBoolean("I", inactive);
        }
    }

    @Override
    public void update()
    {
        checkIfDirty();
    }

    public int getLevels(TileTeleporter teleporter)
    {
        if(teleporter.worldObj.provider.getDimension() == worldObj.provider.getDimension())
        {
            return XPTConfig.getLevels(Math.sqrt(teleporter.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)));
        }

        return XPTConfig.getLevels(0D);
    }

    @Override
    public void onPlacedBy(@Nonnull EntityPlayer ep, @Nonnull ItemStack is, @Nonnull IBlockState state)
    {
        super.onPlacedBy(ep, is, state);

        if(is.hasTagCompound() && is.getTagCompound().hasKey("TeleporterData"))
        {
            readTileData(is.getTagCompound().getCompoundTag("TeleporterData"));
            markDirty();
        }

        if(is.hasDisplayName())
        {
            name = is.getDisplayName();
            markDirty();
        }

        onLoad();
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(pos.getX() - 0.5D, pos.getY(), pos.getZ() - 0.5D, pos.getX() + 1.5D, pos.getY() + 2D, pos.getZ() + 1.5D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 64D;
    }

    @Override
    public boolean isExplosionResistant()
    {
        return true;
    }
}