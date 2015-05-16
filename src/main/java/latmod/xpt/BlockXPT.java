package latmod.xpt;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
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
		return icon_intra;
	}
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
}