package latmod.xpt.blocks;

import ftb.lib.LMMod;
import ftb.lib.api.block.*;
import ftb.lib.api.tile.TileLM;
import latmod.xpt.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

import java.util.List;

public abstract class BlockTeleporterBase extends BlockLM
{
	public BlockTeleporterBase(String s)
	{
		super(s, Material.iron);
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
		isBlockContainer = false;
		setHardness(1F);
		setResistance(100000F);
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
	
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTabToDisplayOn()
	{ return XPT.inst.creativeTab; }
	
	public boolean canHarvestBlock(IBlockAccess w, BlockPos pos, EntityPlayer player)
	{ return true; }
	
	public void onPostLoaded()
	{
	}
	
	public void setBlockBoundsForItemRender() { }
	
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) { }
	
	public AxisAlignedBB getCollisionBoundingBox(World w, BlockPos pos, IBlockState state)
	{
		if(XPTConfig.soft_blocks.get()) return null;
		return super.getCollisionBoundingBox(w, pos, state);
	}
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean isFullCube()
	{ return false; }
	
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
	
	public TileLM createNewTileEntity(World w, int m)
	{ return null; }
	
	public Class<? extends ItemBlockLM> getItemBlock()
	{ return ItemBlockLM.class; }
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer ep, List<String> l, boolean adv)
	{
	}
}