package latmod.xpt;

import net.minecraft.item.*;

public class ItemXPT extends Item
{
	public ItemXPT()
	{
	}
	
	public void loadRecipes()
	{
	}
	
	public boolean hasEffect(ItemStack is, int pass)
	{ return is.hasTagCompound() && is.stackTagCompound.hasKey("Coords"); }
}
