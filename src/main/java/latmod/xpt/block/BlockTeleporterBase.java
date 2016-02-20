package latmod.xpt.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;
import ftb.lib.LMMod;
import ftb.lib.api.block.BlockLM;
import latmod.xpt.*;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.*;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockTeleporterBase extends BlockLM
{
	public BlockTeleporterBase(String s)
	{
		super(s, Material.iron);
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
		setHardness(1F);
		setResistance(100000F);
		setCreativeTab(XPT.creativeTab);
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
	
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTabToDisplayOn()
	{ return XPT.creativeTab; }
	
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{ return true; }
	
	public void loadRecipes()
	{
		if(XPTConfig.levels_for_recall.get() > 0)
			GameRegistry.addRecipe(new ShapedOreRecipe(this, "IEI", "IPI", 'E', "blockEmerald", 'I', "ingotGold", 'P', Items.ender_eye));
	}
	
	public void setBlockBoundsForItemRender() { }
	
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) { }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		if(XPTConfig.soft_blocks.get()) return null;
		setBlockBoundsBasedOnState(w, x, y, z);
		return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
	
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
}