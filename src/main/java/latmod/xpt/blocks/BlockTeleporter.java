package latmod.xpt.blocks;

import ftb.lib.api.item.ODItems;
import ftb.lib.api.tile.TileLM;
import latmod.xpt.XPT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockTeleporter extends BlockTeleporterBase
{
	//public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);
	
	public BlockTeleporter(String s)
	{
		super(s);
		isBlockContainer = true;
	}
	
	/*
	public BlockState createBlockState()
	{ return new BlockState(this, TYPE); }
	
	public IBlockState getStateFromMeta(int meta)
	{ return getDefaultState(); }
	
	public int getMetaFromState(IBlockState state)
	{ return 0; }
	
	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos)
	{
		int type = 0;
		
		TileEntity te = w.getTileEntity(pos);
		
		if(te != null && !te.isInvalid() && te instanceof TileTeleporter)
		{
			type = ((TileTeleporter) te).getType();
		}
		
		return state.withProperty(TYPE, type);
	}
	*/
	
	public void onPostLoaded()
	{
		XPT.mod.addTile(TileTeleporter.class, "teleporter");
	}
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addRecipe(new ItemStack(this), "IEI", "IPI", 'E', "blockEmerald", 'I', ODItems.IRON, 'P', Items.ender_pearl);
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return new TileTeleporter(); }
	
	public void onEntityCollidedWithBlock(World w, BlockPos pos, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileTeleporter t = (TileTeleporter) w.getTileEntity(pos);
			if(t != null) t.onPlayerCollided((EntityPlayerMP) e);
		}
	}
}