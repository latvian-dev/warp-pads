package com.latmod.xpt.item;

import com.feed_the_beast.ftbl.api.item.ItemLM;
import com.feed_the_beast.ftbl.api.item.ODItems;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMMod;
import com.latmod.xpt.XPT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemLinkCard extends ItemLM
{
    public static final String NBT_TAG = "Coords";

    public ItemLinkCard()
    {
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setMaxStackSize(1);
    }

    public static boolean hasData(ItemStack is)
    {
        return is.hasTagCompound() && is.getTagCompound().hasKey(NBT_TAG);
    }

    @Override
    public LMMod getMod()
    {
        return XPT.mod;
    }

    @Override
    public void loadRecipes()
    {
        XPT.mod.recipes.addShapelessRecipe(new ItemStack(this), ODItems.DIAMOND, ODItems.EMERALD, Items.PAPER, Items.ENDER_PEARL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack is)
    {
        return hasData(is);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack is, World w, EntityPlayer ep, EnumHand hand)
    {
        if(!w.isRemote && ep.isSneaking() && hasData(is))
        {
            is.getTagCompound().removeTag(NBT_TAG);
            if(is.getTagCompound().hasNoTags())
            {
                is.setTagCompound(null);
            }
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