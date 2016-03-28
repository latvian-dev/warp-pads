package latmod.xpt.item;

import ftb.lib.*;
import ftb.lib.api.item.*;
import latmod.xpt.XPT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;

import java.util.List;

public class ItemLinkCard extends ItemLM
{
	public static final String NBT_TAG = "Coords";
	
	public ItemLinkCard(String s)
	{
		super(s);
		setCreativeTab(CreativeTabs.tabTransport);
		setMaxStackSize(1);
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addShapelessRecipe(new ItemStack(this), ODItems.DIAMOND, ODItems.EMERALD, Items.paper, Items.ender_pearl);
	}
	
	public static boolean hasData(ItemStack is)
	{ return is.hasTagCompound() && is.getTagCompound().hasKey(NBT_TAG); }
	
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
		if(hasData(is))
		{
			int[] coords = is.getTagCompound().getIntArray(NBT_TAG);
			l.add("Linked to: " + coords[0] + ", " + coords[1] + ", " + coords[2] + " @ " + LMDimUtils.getDimName(coords[3]));
		}
	}
}
