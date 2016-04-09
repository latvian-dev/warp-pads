package latmod.xpt.block;

import ftb.lib.LMMod;
import ftb.lib.api.block.BlockLM;
import ftb.lib.api.item.ODItems;
import latmod.xpt.*;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.*;

public class BlockTeleporter extends BlockLM
{
	public BlockTeleporter(String s)
	{
		super(s, Material.iron);
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
		setHardness(1F);
		setResistance(100000F);
		setCreativeTab(CreativeTabs.tabTransport);
		textureName = "teleporter";
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
	
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{ return true; }
	
	public boolean hasTileEntity(int meta)
	{ return true; }
	
	public TileEntity createTileEntity(World world, int metadata)
	{ return new TileTeleporter(); }
	
	public void onPostLoaded()
	{
		XPT.mod.addTile(TileTeleporter.class, "teleporter");
	}
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addRecipe(new ItemStack(this), "IEI", "IPI", 'E', "blockEmerald", 'I', ODItems.IRON, 'P', Items.ender_pearl);
	}
	
	public void setBlockBoundsForItemRender() { }
	
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) { }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		if(XPTConfig.soft_blocks.getAsBoolean()) return null;
		setBlockBoundsBasedOnState(w, x, y, z);
		return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
	
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
	
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileTeleporter t = (TileTeleporter) w.getTileEntity(x, y, z);
			if(t != null) t.onPlayerCollided((EntityPlayerMP) e);
		}
	}
}