package latmod.xpt;

import ftb.lib.api.item.ODItems;
import ftb.lib.api.tile.TileLM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockTeleporter extends BlockTeleporterBase
{
	public BlockTeleporter(String s)
	{
		super(s);
		isBlockContainer = true;
		getMod().addTile(TileTeleporter.class, "teleporter");
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return new TileTeleporter(); }
	
	public void loadRecipes()
	{
		getMod().recipes.addRecipe(new ItemStack(this), "IEI", "IPI", 'E', "blockEmerald", 'I', ODItems.IRON, 'P', Items.ender_pearl);
	}
	
	public void onEntityCollidedWithBlock(World w, BlockPos pos, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileTeleporter t = (TileTeleporter) w.getTileEntity(pos);
			if(t != null) t.onPlayerCollided((EntityPlayerMP) e);
		}
	}
}