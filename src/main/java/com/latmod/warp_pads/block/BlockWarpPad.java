package com.latmod.warp_pads.block;

import com.feed_the_beast.ftbl.lib.block.BlockLM;
import com.feed_the_beast.ftbl.lib.block.EnumHorizontalOffset;
import com.latmod.warp_pads.WarpPads;
import com.latmod.warp_pads.net.MessageOpenWarpPadGui;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockWarpPad extends BlockLM
{
    public static final PropertyEnum<EnumHorizontalOffset> PART = PropertyEnum.create("part", EnumHorizontalOffset.class);
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 10D / 16D, 1D);
    public static final AxisAlignedBB AABB_COLLISION = new AxisAlignedBB(0D, 0D, 0D, 1D, 8D / 16D, 1D);

    public BlockWarpPad()
    {
        super(WarpPads.MOD_ID + ":warp_pad", Material.GLASS, MapColor.LIGHT_BLUE);
        setHardness(1F);
        setResistance(10000000F);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World w, IBlockState state)
    {
        EnumHorizontalOffset part = state.getValue(PART);
        return part.isCenter() ? new TileWarpPad(false) : new TileWarpPadPart(part);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PART);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PART, EnumHorizontalOffset.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PART).ordinal();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return state.getValue(PART).isCenter() ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess w, BlockPos pos)
    {
        return AABB;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return AABB_COLLISION;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
    {
        return !(entity instanceof EntityDragon || entity instanceof EntityWither);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            pos = state.getValue(PART).opposite().offset(pos);
            TileEntity te = worldIn.getTileEntity(pos);

            if(te instanceof TileWarpPad && !(playerIn instanceof FakePlayer))
            {
                TileWarpPad teleporter = (TileWarpPad) te;
                EntityPlayerMP ep = (EntityPlayerMP) playerIn;
                List<WarpPadNode> teleporters = new ArrayList<>();

                for(TileWarpPad teleporter1 : WarpPadsNet.getTeleporters(ep))
                {
                    if(teleporter1 != teleporter)
                    {
                        WarpPadNode n = new WarpPadNode();
                        n.pos = teleporter1.getDimPos();
                        n.name = teleporter1.getName();
                        n.energy = teleporter.getEnergyRequired(teleporter1);
                        n.available = !teleporter1.inactive && teleporter.consumeEnergy(playerIn, n.energy, true);
                        teleporters.add(n);
                    }
                }

                new MessageOpenWarpPadGui(pos, teleporters).sendTo(ep);
            }
        }

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        for(EnumHorizontalOffset v : EnumHorizontalOffset.VALUES)
        {
            BlockPos pos1 = v.offset(pos);

            if(!worldIn.getBlockState(pos1).getBlock().isReplaceable(worldIn, pos1))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        for(EnumHorizontalOffset v : EnumHorizontalOffset.VALUES)
        {
            if(!v.isCenter())
            {
                worldIn.setBlockState(v.offset(pos), getDefaultState().withProperty(PART, v));
            }
        }

        for(EnumHorizontalOffset v : EnumHorizontalOffset.VALUES)
        {
            TileEntity tileEntity = worldIn.getTileEntity(v.offset(pos));

            if(tileEntity instanceof TileWarpPadBase)
            {
                ((TileWarpPadBase) tileEntity).checkUpdates = true;
            }
        }

        TileEntity te = worldIn.getTileEntity(pos);

        if(te != null)
        {
            if(te instanceof TileWarpPad)
            {
                ((TileWarpPad) te).setOwner(placer.getUniqueID());
            }

            te.markDirty();
            te.onLoad();
        }
    }

    public boolean canExist(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);

        if(state.getBlock() != this)
        {
            return false;
        }

        BlockPos center = state.getValue(PART).opposite().offset(pos);

        for(EnumHorizontalOffset v : EnumHorizontalOffset.VALUES)
        {
            state = world.getBlockState(v.offset(center));

            if(state.getBlock() != this || state.getValue(PART) != v)
            {
                return false;
            }
        }

        return true;
    }
}