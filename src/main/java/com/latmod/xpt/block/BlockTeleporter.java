package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.block.BlockLM;
import com.feed_the_beast.ftbl.api.item.ODItems;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMMod;
import com.feed_the_beast.ftbl.util.MathHelperMC;
import com.latmod.xpt.XPT;
import com.latmod.xpt.XPTConfig;
import com.latmod.xpt.net.MessageOpenGuiXPT;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockTeleporter extends BlockLM
{
    public static final AxisAlignedBB AABB[] = MathHelperMC.getRotatedBoxes(new AxisAlignedBB(0D, 0D, 0D, 1D, 1D / 8D, 1D));

    public BlockTeleporter()
    {
        super(Material.IRON);
        setHardness(1F);
        setResistance(100000F);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setDefaultState(blockState.getBaseState().withProperty(BlockDirectional.FACING, EnumFacing.DOWN));
    }

    @Override
    public LMMod getMod()
    {
        return XPT.mod;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World w, @Nonnull IBlockState state)
    {
        return new TileTeleporter();
    }

    @Override
    public void loadTiles()
    {
        FTBLib.addTile(TileTeleporter.class, getRegistryName());
    }

    @Override
    public void loadRecipes()
    {
        getMod().recipes.addRecipe(new ItemStack(this, 2), "IEI", "IPI", 'E', "blockEmerald", 'I', ODItems.IRON, 'P', Items.ENDER_PEARL);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Nonnull
    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess w, BlockPos pos)
    {
        return AABB[state.getValue(BlockDirectional.FACING).ordinal()];
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn)
    {
        if(!XPTConfig.soft_blocks.getAsBoolean())
        {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
        }
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

    @Nonnull
    @Override
    @Deprecated
    public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot)
    {
        return state.withProperty(BlockDirectional.FACING, rot.rotate(state.getValue(BlockDirectional.FACING)));
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(BlockDirectional.FACING)));
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(BlockDirectional.FACING, facing.getOpposite());
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(BlockDirectional.FACING).ordinal();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockDirectional.FACING);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            TileEntity te = worldIn.getTileEntity(pos);

            if(te instanceof TileTeleporter && !(playerIn instanceof FakePlayer))
            {
                TileTeleporter teleporter = (TileTeleporter) te;
                EntityPlayerMP ep = (EntityPlayerMP) playerIn;
                List<XPTNode> teleporters = new ArrayList<>();

                for(TileTeleporter teleporter1 : XPTNet.getTeleporters(ep))
                {
                    if(teleporter1 != teleporter)
                    {
                        int levels = teleporter.getLevels(teleporter1);
                        teleporters.add(new XPTNode(teleporter1.getUUID(), teleporter1.getName(), levels, !teleporter1.inactive && XPTConfig.consumeLevels(playerIn, levels, true)));
                    }
                }

                new MessageOpenGuiXPT(pos, teleporters).sendTo(ep);
            }
        }

        return true;
    }

    @Override
    public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack)
    {
        if(te instanceof TileTeleporter)
        {
            ItemStack itemstack = new ItemStack(this, 1, 0);

            itemstack.setTagCompound(new NBTTagCompound());
            NBTTagCompound tag = new NBTTagCompound();
            ((TileTeleporter) te).writeTileData(tag);
            itemstack.getTagCompound().setTag("TeleporterData", tag);

            spawnAsEntity(worldIn, pos, itemstack);
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune)
    {
        List<ItemStack> ret = new ArrayList<>();
        ItemStack itemstack = new ItemStack(this, 1, 0);

        TileEntity te = world.getTileEntity(pos);

        if(te instanceof TileTeleporter)
        {
            itemstack.setTagCompound(new NBTTagCompound());
            NBTTagCompound tag = new NBTTagCompound();
            ((TileTeleporter) te).writeTileData(tag);
            itemstack.getTagCompound().setTag("TeleporterData", tag);
        }

        ret.add(itemstack);
        return ret;
    }
}