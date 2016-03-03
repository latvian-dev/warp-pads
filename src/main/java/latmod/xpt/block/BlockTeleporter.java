package latmod.xpt.block;

import latmod.xpt.XPT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockTeleporter extends BlockTeleporterBase
{
	public BlockTeleporter(String s)
	{
		super(s);
		isBlockContainer = true;
	}
	
	public boolean hasTileEntity(IBlockState state)
	{ return true; }
	
	public TileEntity createTileEntity(World w, IBlockState state)
	{ return new TileTeleporter(); }
	
	public void onPostLoaded()
	{
		super.onPostLoaded();
		XPT.mod.addTile(TileTeleporter.class, "teleporter");
	}
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addRecipe(new ItemStack(this), "IEI", "IPI", 'E', "blockEmerald", 'I', "ingotIron", 'P', Items.ender_pearl);
	}
	
	public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileTeleporter t = (TileTeleporter) w.getTileEntity(pos);
			if(t != null) t.onPlayerCollided((EntityPlayerMP) e);
		}
	}
}