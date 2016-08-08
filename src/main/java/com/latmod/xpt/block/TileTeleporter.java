package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.security.ISecure;
import com.feed_the_beast.ftbl.api.security.Security;
import com.feed_the_beast.ftbl.api.tile.TileLM;
import com.latmod.lib.util.LMUtils;
import com.latmod.xpt.XPTConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TileTeleporter extends TileLM implements ITickable
{
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

    public void setName(String n)
    {
        name = n;

        if(name != null && name.isEmpty())
        {
            name = null;
        }

        markDirty();
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
    public void onPlacedBy(@Nonnull EntityLivingBase el, @Nonnull ItemStack is, @Nonnull IBlockState state)
    {
        super.onPlacedBy(el, is, state);

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