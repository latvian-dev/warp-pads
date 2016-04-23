package latmod.xpt.item;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.item.*;
import latmod.xpt.XPT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemLinkCard extends ItemLM
{
	public static final String NBT_TAG = "Coords";
	
	public ItemLinkCard(String s)
	{
		super(s);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTransport);
	}
	
	@Override
	public LMMod getMod()
	{ return XPT.mod; }
	
	@Override
	public void loadRecipes()
	{
		XPT.mod.recipes.addShapelessRecipe(new ItemStack(this), ODItems.DIAMOND, ODItems.EMERALD, Items.paper, Items.ender_pearl);
	}
	
	public static boolean hasData(ItemStack is)
	{ return is.hasTagCompound() && is.stackTagCompound.hasKey(NBT_TAG); }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass)
	{ return hasData(is); }
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote && ep.isSneaking() && hasData(is))
		{
			is.stackTagCompound.removeTag(NBT_TAG);
			if(is.stackTagCompound.hasNoTags()) is.setTagCompound(null);
		}
		
		return is;
	}
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer ep, List l, boolean b)
	{
		if(hasData(is))
		{
			int[] coords = is.stackTagCompound.getIntArray(NBT_TAG);
			StringBuilder sb = new StringBuilder();
			sb.append("Linked to: ");
			sb.append(coords[0]);
			sb.append(", ");
			sb.append(coords[1]);
			sb.append(", ");
			sb.append(coords[2]);
			sb.append(" @ ");
			sb.append(LMDimUtils.getDimName(coords[3]));
			l.add(sb.toString());
		}
	}
}
