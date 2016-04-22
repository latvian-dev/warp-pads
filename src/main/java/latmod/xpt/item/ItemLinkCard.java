package latmod.xpt.item;

import ftb.lib.*;
import ftb.lib.api.item.*;
import latmod.xpt.XPT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;

import java.util.List;

public class ItemLinkCard extends ItemLM
{
	public static final String NBT_TAG = "Coords";
	
	public ItemLinkCard()
	{
		super();
		setCreativeTab(CreativeTabs.tabTransport);
		setMaxStackSize(1);
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
	{ return is.hasTagCompound() && is.getTagCompound().hasKey(NBT_TAG); }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is)
	{ return hasData(is); }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack is, World w, EntityPlayer ep, EnumHand hand)
	{
		if(!w.isRemote && ep.isSneaking() && hasData(is))
		{
			is.getTagCompound().removeTag(NBT_TAG);
			if(is.getTagCompound().hasNoTags()) is.setTagCompound(null);
		}
		
		return new ActionResult<>(EnumActionResult.SUCCESS, is);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer ep, List<String> l, boolean b)
	{
		if(hasData(is))
		{
			l.add("Linked to: " + new BlockDimPos(is.getTagCompound().getIntArray(NBT_TAG)));
		}
	}
}
