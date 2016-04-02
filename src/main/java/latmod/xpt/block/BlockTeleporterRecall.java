package latmod.xpt.block;

import ftb.lib.api.item.ODItems;
import latmod.xpt.*;
import latmod.xpt.item.ItemLinkCard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class BlockTeleporterRecall extends BlockTeleporterBase
{
	public BlockTeleporterRecall(String s)
	{
		super(s);
	}
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addRecipe(new ItemStack(this), "GIG", "ITI", 'T', XPTItems.teleporter, 'I', ODItems.GOLD, 'G', ODItems.GLOWSTONE);
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		ItemStack is = ep.getHeldItem();
		if(w.isRemote || is == null) return true;
		
		if(is.getItem() == XPTItems.mirror)
		{
			if(XPTConfig.levels_for_recall.getAsInt() == -1)
			{
				XPTChatMessages.RECALL_DISABLED.print(ep);
				return true;
			}
			
			int prevLinkedX = 0;
			int prevLinkedY = 0;
			int prevLinkedZ = 0;
			int prevLinkedDim = ep.dimension;
			
			if(ItemLinkCard.hasData(is))
			{
				int[] pos = is.getTagCompound().getIntArray(ItemLinkCard.NBT_TAG);
				
				prevLinkedX = pos[0];
				prevLinkedY = pos[1];
				prevLinkedZ = pos[2];
				prevLinkedDim = pos[3];
			}
			
			if(prevLinkedX != x || prevLinkedY != y || prevLinkedZ != z || prevLinkedDim != w.provider.dimensionId)
			{
				int levels = XPTConfig.only_linking_uses_xp.getAsBoolean() ? XPTConfig.levels_for_recall.getAsInt() : 0;
				
				if(!XPTConfig.canConsumeLevels(ep, levels)) XPTChatMessages.NEED_XP_LEVEL_LINK.print(ep, "" + levels);
				else
				{
					XPTConfig.consumeLevels(ep, levels);
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] {x, y, z, w.provider.dimensionId});
					XPTChatMessages.LINK_CREATED.print(ep);
				}
			}
			else XPTChatMessages.ALREADY_LINKED.print(ep);
		}
		
		return true;
	}
}