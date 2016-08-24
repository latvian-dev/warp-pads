package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.security.ISecure;
import com.feed_the_beast.ftbl.api.security.Security;
import com.feed_the_beast.ftbl.api.tile.TileLM;
import com.latmod.lib.util.LMStringUtils;
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
        return name == null ? LMStringUtils.fromUUID(getUUID()) : name;
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
    public void writeTileData(@Nonnull NBTTagCompound nbt)
    {
        super.writeTileData(nbt);

        nbt.setString("UUID", LMStringUtils.fromUUID(getUUID()));

        if(name != null && !name.isEmpty())
        {
            nbt.setString("Name", name);
        }

        if(inactive)
        {
            nbt.setBoolean("Inactive", true);
        }
    }

    @Override
    public void readTileData(@Nonnull NBTTagCompound nbt)
    {
        super.readTileData(nbt);

        uuid = LMStringUtils.fromString(nbt.getString("UUID"));

        if(nbt.hasKey("Name"))
        {
            name = nbt.getString("Name");
        }
        else
        {
            name = null;
        }

        inactive = nbt.getBoolean("Inactive");
    }

    @Override
    public void writeTileClientData(@Nonnull NBTTagCompound nbt)
    {
        super.writeTileClientData(nbt);
        nbt.setLong("UM", getUUID().getMostSignificantBits());
        nbt.setLong("UL", getUUID().getLeastSignificantBits());

        if(name != null && !name.isEmpty())
        {
            nbt.setString("N", name);
        }

        if(inactive)
        {
            nbt.setBoolean("I", inactive);
        }
    }

    @Override
    public void readTileClientData(@Nonnull NBTTagCompound nbt)
    {
        super.readTileClientData(nbt);
        uuid = new UUID(nbt.getLong("UM"), nbt.getLong("UL"));

        if(nbt.hasKey("N"))
        {
            name = nbt.getString("N");
        }
        else
        {
            name = null;
        }

        inactive = nbt.getBoolean("I");
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