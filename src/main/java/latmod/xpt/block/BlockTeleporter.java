package latmod.xpt.block;

import ftb.lib.LMMod;
import ftb.lib.api.block.BlockLM;
import ftb.lib.api.item.ODItems;
import latmod.xpt.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class BlockTeleporter extends BlockLM
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	private static final double H0 = 1D / 8D;
	private static final double H1 = 1D - H0;
	public static final AxisAlignedBB AABB[] = {new AxisAlignedBB(0D, 0D, 0D, 1D, H0, 1D), new AxisAlignedBB(0D, H1, 0D, 1D, 1D, 1D), new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, H0), new AxisAlignedBB(0D, 0D, H1, 1D, 1D, 1D), new AxisAlignedBB(0D, 0D, 0D, H0, 1D, 1D), new AxisAlignedBB(H1, 0D, 0D, 1D, 1D, 1D)};
	
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
	
	public int damageDropped(IBlockState state)
	{ return 0; }
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess w, BlockPos pos)
	{ return AABB[state.getValue(FACING).ordinal()]; }
	
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World w, BlockPos pos)
	{ return XPTConfig.soft_blocks.getAsBoolean() ? NULL_AABB : getBoundingBox(state, w, pos); }
	
	public boolean isOpaqueCube(IBlockState state)
	{ return false; }
	
	public boolean isFullCube(IBlockState state)
	{ return false; }
	
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
	
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{ return state.withProperty(FACING, rot.rotate(state.getValue(FACING))); }
	
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{ return state.withRotation(mirrorIn.toRotation(state.getValue(FACING))); }
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{ return getDefaultState().withProperty(FACING, facing.getOpposite()); }
	
	public IBlockState getStateFromMeta(int meta)
	{ return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)); }
	
	public int getMetaFromState(IBlockState state)
	{ return state.getValue(FACING).ordinal(); }
	
	protected BlockStateContainer createBlockState()
	{ return new BlockStateContainer(this, FACING); }
	
	public IBlockState getModelState()
	{ return getDefaultState().withProperty(FACING, EnumFacing.DOWN); }
}