package latmod.xpt;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;

public class BlockXPT extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	public IIcon icon_intra, icon_extra;
	
	public BlockXPT()
	{
		super(Material.wood);
		isBlockContainer = true;
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
	}
	
	public TileEntity createNewTileEntity(World w, int m)
	{ return new TileXPT(); }
	
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
		TileXPT t = (TileXPT)w.getTileEntity(x, y, z);
		
		if(t != null && !t.isInvalid())
			t.onRightClick(ep, ep.getHeldItem());
		
		return true;
	}
	
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileXPT t = (TileXPT)w.getTileEntity(x, y, z);
			
			if(t != null && !t.isInvalid())
				t.onPlayerCollided((EntityPlayerMP)e);
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
	
	public void breakBlock(World w, int x, int y, int z, Block b, int m)
	{
		if(!w.isRemote)
		{
			TileXPT t = (TileXPT)w.getTileEntity(x, y, z);
			if(t != null && !t.isInvalid())
				t.onBroken();
		}
		
		super.breakBlock(w, x, y, z, b, m);
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