package latmod.xpt;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;

public class CreativeTabXPT extends CreativeTabs
{
	public CreativeTabXPT()
	{ super("xpt"); }
	
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{ return Item.getItemFromBlock(XPT.teleporter); }
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
    public void displayAllReleventItems(List l)
    {
		l.add(new ItemStack(XPT.teleporter));
		l.add(new ItemStack(XPT.link_card));
		l.add(new ItemStack(XPT.recall_remote));
    }
}