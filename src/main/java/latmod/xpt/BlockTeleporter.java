package latmod.xpt;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.*;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;

public class BlockTeleporter extends BlockContainer
{
	public BlockTeleporter()
	{
		super(Material.wood);
		isBlockContainer = true;
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
	}
	
	public TileEntity createNewTileEntity(World w, int m)
	{ return new TileTeleporter(); }
	
	public void loadRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(this, "IEI", "IPI",
				'E', "blockEmerald",
				'I', "ingotIron",
				'P', Items.ender_pearl));
	}
	
	public void setBlockBoundsForItemRender() { }
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) { }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		if(XPTConfig.soft_blocks) return null;
		setBlockBoundsBasedOnState(w, x, y, z);
		return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		TileTeleporter t = (TileTeleporter)w.getTileEntity(x, y, z);
		if(t != null) t.onRightClick(ep, ep.getHeldItem());
		return true;
	}
	
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileTeleporter t = (TileTeleporter)w.getTileEntity(x, y, z);
			if(t != null) t.onPlayerCollided((EntityPlayerMP)e);
		}
	}
	
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase el, ItemStack is)
	{
		if(!w.isRemote && el instanceof EntityPlayer && is != null)
		{
			TileTeleporter t = (TileTeleporter)w.getTileEntity(x, y, z);
			if(t != null) t.onPlacedBy((EntityPlayer)el, is);
		}
	}
	
	public void breakBlock(World w, int x, int y, int z, Block b, int m)
	{
		if(!w.isRemote)
		{
			TileTeleporter t = (TileTeleporter)w.getTileEntity(x, y, z);
			if(t != null) t.onBroken();
		}
		
		super.breakBlock(w, x, y, z, b, m);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{ blockIcon = ir.registerIcon("xpt:teleporter"); }
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
	
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{ return !(entity instanceof EntityDragon || entity instanceof EntityWither); }
}