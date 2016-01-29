package latmod.xpt.items;

import ftb.lib.*;
import ftb.lib.api.item.ODItems;
import latmod.xpt.XPT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;

import java.util.List;

public class ItemLinkCard extends ItemXPT
{
	public static final String NBT_TAG = "Coords";
	
	public ItemLinkCard(String s)
	{
		super(s);
		setMaxStackSize(1);
	}
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addShapelessRecipe(new ItemStack(this), ODItems.DIAMOND, ODItems.EMERALD, Items.paper, Items.ender_pearl);
	}
	
	public static boolean hasData(ItemStack is)
	{ return is.hasTagCompound() && is.getTagCompound().hasKey(NBT_TAG); }
	
	public static BlockDimPos getLink(ItemStack is)
	{
		return hasData(is) ? new BlockDimPos(is.getTagCompound().getIntArray(NBT_TAG)) : null;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is)
	{ return hasData(is); }
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote && ep.isSneaking() && hasData(is))
		{
			is.getTagCompound().removeTag(NBT_TAG);
			if(is.getTagCompound().hasNoTags()) is.setTagCompound(null);
		}
		
		return is;
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer ep, List<String> l, boolean b)
	{
		BlockDimPos link = getLink(is);
		
		if(link != null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Linked to: ");
			sb.append(link.x);
			sb.append(", ");
			sb.append(link.y);
			sb.append(", ");
			sb.append(link.z);
			sb.append(" @ ");
			sb.append(LMDimUtils.getDimName(link.dim));
			l.add(sb.toString());
		}
	}
}
