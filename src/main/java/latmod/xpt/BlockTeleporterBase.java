package latmod.xpt;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.*;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockTeleporterBase extends BlockContainer
{
	public BlockTeleporterBase()
	{
		super(Material.iron);
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
		isBlockContainer = false;
	}
	
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
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{ blockIcon = ir.registerIcon("xpt:" + textureName); }
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
	
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
	
	public boolean hasTileEntity(int metadata)
	{ return isBlockContainer; }
	
	public TileEntity createNewTileEntity(World w, int m)
	{ return null; }
}