package com.latmod.xpt.block;

import com.feed_the_beast.ftbl.api.block.BlockLM;
import com.latmod.xpt.XPTConfig;
import com.latmod.xpt.net.MessageOpenGuiXPT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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

public class BlockTeleporter extends BlockLM
{
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D / 8D, 1D);

    public BlockTeleporter()
    {
        super(Material.IRON);
        setHardness(1F);
        setResistance(100000F);
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
        return new TileTeleporter();
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess w, BlockPos pos)
    {
        return AABB;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn)
    {
        if(!XPTConfig.SOFT_BLOCKS.getBoolean())
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
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack)
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

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
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