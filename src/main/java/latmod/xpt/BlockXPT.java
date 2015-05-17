package latmod.xpt;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import cpw.mods.fml.relauncher.*;

public class BlockXPT extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	public IIcon icon_intra, icon_extra;
	
	public BlockXPT()
	{
		super(Material.wood);
		isBlockContainer = true;
	}
	
	public TileEntity createNewTileEntity(World w, int m)
	{ return new TileXPT(); }
	
	public void loadRecipes()
	{
	}
	
	public void setBlockBoundsForItemRender()
	{ setBlockBounds(0F, 0.5F - 1F / 16F, 0F, 1F, 0.5F + 1F / 16F, 1F); }
	
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z)
	{ setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F); }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{ setBlockBoundsBasedOnState(w, x, y, z); return super.getCollisionBoundingBoxFromPool(w, x, y, z); }
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		TileXPT t = (TileXPT)w.getTileEntity(x, y, z);
		
		if(t != null && !t.isInvalid())
			t.onRightClick(ep, ep.getHeldItem());
		
		return true;
	}
	
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayer)
		{
			TileXPT t = (TileXPT)w.getTileEntity(x, y, z);
			
			if(t != null && !t.isInvalid())
				t.onPlayerCollided((EntityPlayer)e);
		}
	}
	
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase el, ItemStack is)
	{
		if(!w.isRemote && el instanceof EntityPlayer && is != null)
		{
			TileXPT t = (TileXPT)w.getTileEntity(x, y, z);
			if(t != null && !t.isInvalid())
				t.onPlacedBy((EntityPlayer)el, is);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("xpt:teleporter");
		icon_intra = ir.registerIcon("xpt:teleporter_intra");
		icon_extra = ir.registerIcon("xpt:teleporter_extra");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s)
	{
		if(s != 1) return blockIcon;
		
		TileXPT t = (TileXPT)iba.getTileEntity(x, y, z);
		
		if(t != null && !t.isInvalid())
		{
			int i = t.getIconID();
			if(i == 1) return icon_intra;
			if(i == 2) return icon_extra;
		}
		
		return blockIcon;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int m)
	{ return (s == 1) ? icon_intra : blockIcon; }
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
}