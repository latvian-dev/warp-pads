package latmod.xpt.block;

import ftb.lib.LMMod;
import ftb.lib.api.block.BlockLM;
import latmod.xpt.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockTeleporterBase extends BlockLM
{
	public BlockTeleporterBase(String s)
	{
		super(s, Material.iron);
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
		setHardness(1F);
		setResistance(100000F);
		setCreativeTab(XPT.tab);
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
	
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{ return true; }
	
	public void loadRecipes()
	{
		if(XPTConfig.levels_for_recall.get() > 0)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(this, "IEI", "IPI", 'E', "blockEmerald", 'I', "ingotGold", 'P', Items.ender_eye));
		}
	}
	
	public void setBlockBoundsForItemRender()
	{
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
	}
	
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	{
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
	}
	
	public AxisAlignedBB getCollisionBoundingBox(World w, BlockPos pos, IBlockState state)
	{
		if(XPTConfig.soft_blocks.get()) return null;
		setBlockBoundsBasedOnState(w, pos);
		return super.getCollisionBoundingBox(w, pos, state);
	}
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
	
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
}