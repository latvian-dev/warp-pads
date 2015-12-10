package latmod.xpt;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockTeleporter extends BlockTeleporterBase 
{
	public BlockTeleporter()
	{
		super();
		isBlockContainer = true;
		textureName = "teleporter";
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
}