package latmod.xpt.block;

import ftb.lib.LMMod;
import ftb.lib.api.block.BlockLM;
import ftb.lib.api.item.ODItems;
import latmod.xpt.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class BlockTeleporter extends BlockLM
{
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D / 8D, 1D);
	
	public BlockTeleporter()
	{
		super(Material.iron);
		setHardness(1F);
		setResistance(100000F);
		setCreativeTab(CreativeTabs.tabTransport);
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
	
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{ return true; }
	
	public boolean hasTileEntity(IBlockState state)
	{ return true; }
	
	public TileEntity createTileEntity(World w, IBlockState state)
	{ return new TileTeleporter(); }
	
	public void onPostLoaded()
	{
		super.onPostLoaded();
		getMod().addTile(TileTeleporter.class, getRegistryName().getResourcePath());
	}
	
	public void loadRecipes()
	{
		getMod().recipes.addRecipe(new ItemStack(this, 2), "IEI", "IPI", 'E', "blockEmerald", 'I', ODItems.IRON, 'P', Items.ender_pearl);
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess w, BlockPos pos)
	{ return AABB; }
	
	public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{ return XPTConfig.soft_blocks.getAsBoolean() ? NULL_AABB : AABB; }
	
	public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileTeleporter t = (TileTeleporter) w.getTileEntity(pos);
			if(t != null) t.onPlayerCollided((EntityPlayerMP) e);
		}
	}
	
	public boolean isOpaqueCube(IBlockState state)
	{ return false; }
	
	public boolean isFullCube(IBlockState state)
	{ return false; }
	
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
}