package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.security.EnumPrivacyLevel;
import com.feed_the_beast.ftbl.lib.tile.TileLM;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.latmod.xpt.XPTConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileTeleporter extends TileLM implements ITickable
{
    public static final String ITEM_NBT_KEY = "TeleporterData";

    private UUID owner, uuid;
    public boolean inactive;
    private String name = "";
    private EnumPrivacyLevel privacyLevel = EnumPrivacyLevel.TEAM;

    public String getName()
    {
        return name.isEmpty() ? LMStringUtils.fromUUID(getUUID()) : name;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentString(getName());
    }

    public void setName(String n)
    {
        name = n;
        markDirty();
    }

    @Nullable
    public UUID getOwner()
    {
        return owner;
    }

    public void setOwner(UUID id)
    {
        owner = id;
    }

    public UUID getUUID()
    {
        if(uuid == null)
        {
            uuid = UUID.randomUUID();
        }

        return uuid;
    }

    public EnumPrivacyLevel getPrivacyLevel()
    {
        return privacyLevel;
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
    public void writeTileData(NBTTagCompound nbt)
    {
        super.writeTileData(nbt);

        if(owner != null)
        {
            nbt.setString("Owner", LMStringUtils.fromUUID(owner));
        }

        nbt.setByte("Privacy", (byte) privacyLevel.ordinal());
        nbt.setString("UUID", LMStringUtils.fromUUID(getUUID()));
        nbt.setString("Name", name);
        nbt.setBoolean("Inactive", inactive);
    }

    @Override
    public void readTileData(NBTTagCompound nbt)
    {
        super.readTileData(nbt);

        owner = nbt.hasKey("Owner") ? LMStringUtils.fromString(nbt.getString("Owner")) : null;
        privacyLevel = EnumPrivacyLevel.VALUES[nbt.getByte("Privacy")];
        uuid = LMStringUtils.fromString(nbt.getString("UUID"));
        name = nbt.getString("Name");
        inactive = nbt.getBoolean("Inactive");
    }

    @Override
    public void writeTileClientData(NBTTagCompound nbt)
    {
        super.writeTileClientData(nbt);
        nbt.setLong("UM", getUUID().getMostSignificantBits());
        nbt.setLong("UL", getUUID().getLeastSignificantBits());

        if(!name.isEmpty())
        {
            nbt.setString("N", name);
        }

        if(inactive)
        {
            nbt.setBoolean("I", true);
        }
    }

    @Override
    public void readTileClientData(NBTTagCompound nbt)
    {
        super.readTileClientData(nbt);
        uuid = new UUID(nbt.getLong("UM"), nbt.getLong("UL"));
        name = nbt.getString("N");
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

        return XPTConfig.getLevels(-1D);
    }

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
}