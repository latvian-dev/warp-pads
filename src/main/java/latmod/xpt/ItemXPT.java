package latmod.xpt;

import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemXPT extends Item
{
	public ItemXPT()
	{
	}
	
	public void loadRecipes()
	{
		GameRegistry.addRecipe(new ShapelessOreRecipe(this, "gemDiamond", "gemEmerald", Items.paper, Items.ender_pearl));
	}
	
	public boolean hasEffect(ItemStack is, int pass)
	{ return is.hasTagCompound() && is.stackTagCompound.hasKey("Coords"); }
}
