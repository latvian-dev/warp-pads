package latmod.xpt.items;

import ftb.lib.LMDimUtils;
import ftb.lib.api.item.ODItems;
import latmod.xpt.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ItemMagicalMirror extends ItemLinkCard // ItemBow
{
	public ItemMagicalMirror(String s)
	{
		super(s);
	}
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addRecipe(new ItemStack(this), "GGL", "GSG", "LGG", 'L', XPTItems.link_card, 'G', ODItems.GLASS_PANE_ANY, 'S', Items.nether_star);
	}
	
	public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer ep, int p_77615_4_)
	{
	}
	
	public ItemStack onEaten(ItemStack is, World w, EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP)
		{
			if(XPTConfig.levels_for_recall.get() == -1)
			{
				XPTChatMessages.RECALL_DISABLED.print(ep);
				return is;
			}
			else if(!hasData(is))
			{
				XPTChatMessages.LINK_BROKEN.print(ep);
				return is;
			}
			
			int levels = XPTConfig.only_linking_uses_xp.get() ? 0 : XPTConfig.levels_for_recall.get();
			
			if(!XPTConfig.canConsumeLevels(ep, levels))
			{
				XPTChatMessages.NEED_XP_LEVEL_TP.print(ep, Integer.toString(levels));
				return is;
			}
			
			int[] coords = is.getTagCompound().getIntArray(NBT_TAG);
			
			World w1 = DimensionManager.getWorld(coords[3]);
			
			if(w1.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock() == XPTItems.teleporter_recall)
			{
				XPTConfig.consumeLevels(ep, levels);
				w.playSoundEffect(ep.posX, ep.posY + 1.5D, ep.posZ, "mob.endermen.portal", 1F, 1F);
				LMDimUtils.teleportPlayer((EntityPlayerMP) ep, coords[0] + 0.5D, coords[1] + 0.2D, coords[2] + 0.5D, coords[3]);
				w.playSoundEffect(coords[0] + 0.5D, coords[1] + 1.5D, coords[2] + 0.5D, "mob.endermen.portal", 1F, 1F);
			}
			else XPTChatMessages.LINK_BROKEN.print(ep);
		}
		
		return is;
	}
	
	public int getMaxItemUseDuration(ItemStack is)
	{ return 28; }
	
	public EnumAction getItemUseAction(ItemStack p_77661_1_)
	{ return EnumAction.BOW; }
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{
		ep.setItemInUse(is, getMaxItemUseDuration(is));
		return is;
	}
}
