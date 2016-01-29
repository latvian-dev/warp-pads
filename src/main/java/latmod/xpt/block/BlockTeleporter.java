package latmod.xpt.block;

import ftb.lib.api.tile.TileLM;
import latmod.xpt.XPT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockTeleporter extends BlockTeleporterBase
{
	public BlockTeleporter(String s)
	{
		super(s);
		isBlockContainer = true;
		textureName = "teleporter";
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return new TileTeleporter(); }
	
	public void onPostLoaded()
	{
		XPT.mod.addTile(TileTeleporter.class, "teleporter");
	}
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addRecipe(new ItemStack(this), "IEI", "IPI", 'E', "blockEmerald", 'I', "ingotIron", 'P', Items.ender_pearl);
	}
	
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity e)
	{
		if(e != null && !e.isDead && e instanceof EntityPlayerMP)
		{
			TileTeleporter t = (TileTeleporter) w.getTileEntity(x, y, z);
			if(t != null) t.onPlayerCollided((EntityPlayerMP) e);
		}
	}
}