package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.security.Security;
import com.feed_the_beast.ftbl.api.tile.TileClientAction;
import com.feed_the_beast.ftbl.api.tile.TileLM;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.latmod.lib.util.LMUtils;
import com.latmod.xpt.XPT;
import com.latmod.xpt.XPTConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class TileTeleporter extends TileLM
{
    public static final TileClientAction TELEPORT = new TileClientAction(new ResourceLocation(XPT.MOD_ID, "tp"))
    {
        @Override
        public void onAction(TileEntity te, NBTTagCompound data, EntityPlayerMP player)
        {
            UUID id = LMUtils.fromString(data.getString("ID"));
            
            /*
                cooldown = XPTConfig.cooldownTicks();
                
                playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);

                if(LMDimUtils.teleportPlayer(ep, linked))
                {
                    XPTConfig.consumeLevels(ep, levels);
                    markDirty();

                    if(t != null)
                    {
                        t.cooldown = cooldown;
                        t.markDirty();
                    }

                    playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
                }
                */
        }
    };

    public UUID uuid;
    public int cooldown;
    public int pcooldown;
    private String name;

    public static TileTeleporter getTileXPT(BlockDimPos pos)
    {
        if(pos == null)
        {
            return null;
        }

        World w = LMDimUtils.getWorld(pos.getDim());

        if(w != null)
        {
            TileEntity te = w.getTileEntity(pos);

            if(te != null && te instanceof TileTeleporter)
            {
                return (TileTeleporter) te;
            }
        }

        return null;
    }

    @Override
    protected Security createSecurity()
    {
        return new Security(true, true);
    }

    @Nonnull
    @Override
    public String getName()
    {
        return name == null ? (uuid == null ? "<error>" : LMUtils.fromUUID(uuid)) : name;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        XPTNet.add(this);
    }

    @Override
    public void onChunkUnload()
    {
        XPTNet.remove(this);
        super.onChunkUnload();
    }

    @Override
    public void readTileData(@Nonnull NBTTagCompound tag)
    {
        super.readTileData(tag);

        uuid = LMUtils.fromString(tag.getString("UUID"));

        if(uuid == null)
        {
            uuid = UUID.randomUUID();
        }

        pcooldown = cooldown = tag.getInteger("Cooldown");

        if(tag.hasKey("Name"))
        {
            name = tag.getString("Name");
        }
        else
        {
            name = null;
        }
    }

    @Override
    public void writeTileData(@Nonnull NBTTagCompound tag)
    {
        super.writeTileData(tag);

        tag.setString("UUID", LMUtils.fromUUID(uuid));
        tag.setInteger("Cooldown", cooldown);

        if(name != null && !name.isEmpty())
        {
            tag.setString("Name", name);
        }
    }

    @Override
    public void readTileClientData(@Nonnull NBTTagCompound tag)
    {
        uuid = LMUtils.fromString(tag.getString("U"));

        if(uuid == null)
        {
            uuid = UUID.randomUUID();
        }

        pcooldown = cooldown = tag.getInteger("C");

        if(tag.hasKey("N"))
        {
            name = tag.getString("N");
        }
        else
        {
            name = null;
        }
    }

    @Override
    public void writeTileClientData(@Nonnull NBTTagCompound tag)
    {
        if(uuid != null)
        {
            tag.setString("U", LMUtils.fromUUID(uuid));
        }

        if(cooldown != 0)
        {
            tag.setInteger("C", cooldown);
        }

        if(name != null && !name.isEmpty())
        {
            tag.setString("N", name);
        }
    }

    @Override
    public void onUpdate()
    {
        pcooldown = cooldown;
        if(cooldown < 0)
        {
            cooldown = 0;
        }

        if(cooldown > 0)
        {
            cooldown--;
            if(cooldown == 0 && getSide().isServer())
            {
                markDirty();
            }
        }
    }

    @Override
    public boolean onRightClick(@Nonnull EntityPlayer ep, @Nullable ItemStack is, @Nonnull EnumFacing side, @Nonnull EnumHand hand, float x, float y, float z)
    {
        if(worldObj.isRemote)
        {
            return true;
        }

        if(is != null && is.getItem() == Items.NAME_TAG)
        {
            if(is.hasDisplayName())
            {
                name = is.getDisplayName();

                if(!ep.capabilities.isCreativeMode)
                {
                    is.stackSize--;
                }

                markDirty();
            }

            return true;
        }
        else
        {
            if(cooldown <= 0 && !(ep instanceof FakePlayer))
            {
                Collection<TileTeleporter> teleporters = XPTNet.getTeleporters((EntityPlayerMP) ep);
                FTBLib.printChat(ep, "Available Teleporters: " + teleporters.size() + ":");

                for(TileTeleporter tile : teleporters)
                {
                    boolean crossdim = tile.getWorld().provider.getDimension() != worldObj.provider.getDimension();
                    int levels = XPTConfig.only_linking_uses_xp.getAsBoolean() ? 0 : getLevels(tile.getPos(), crossdim);
                    FTBLib.printChat(ep, "[" + ForgeWorldMP.inst.getPlayer(security.getOwner()) + "] " + tile.getName() + " [" + levels + ": " + XPTConfig.consumeLevels(ep, levels, true) + "]");
                }
            }

            //FIXME: Open the gui
        }

        return true;
    }

    private int getLevels(Vec3i pos, boolean crossdim)
    {
        if(crossdim)
        {
            return XPTConfig.levels_for_crossdim.getAsInt();
        }
        double dist = Math.sqrt(getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D));
        return Math.max(0, ((XPTConfig.levels_for_1000_blocks.getAsInt() > 0) ? MathHelper.ceiling_double_int(XPTConfig.levels_for_1000_blocks.getAsInt() * dist / 1000D) : 0) - 1);
    }

    @Override
    public void onPlacedBy(@Nonnull EntityPlayer ep, @Nonnull ItemStack is, @Nonnull IBlockState state)
    {
        super.onPlacedBy(ep, is, state);
        if(is.hasDisplayName())
        {
            name = is.getDisplayName();
        }
        markDirty();
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        double d = 0.5D;
        return new AxisAlignedBB(getPos().getX() - d, getPos().getY(), getPos().getZ() - d, getPos().getX() + 1D + d, getPos().getY() + 2D, getPos().getZ() + 1D + d);
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

    @Override
    public void onBroken(@Nonnull IBlockState state)
    {
        super.onBroken(state);
        onChunkUnload();
    }
}