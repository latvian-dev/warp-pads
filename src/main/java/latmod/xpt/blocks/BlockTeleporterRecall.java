package latmod.xpt.blocks;

import ftb.lib.api.item.ODItems;
import ftb.lib.api.tile.TileLM;
import latmod.xpt.*;
import latmod.xpt.items.ItemLinkCard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class BlockTeleporterRecall extends BlockTeleporterBase
{
	public BlockTeleporterRecall(String s)
	{
		super(s);
		isBlockContainer = false;
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return null; }
	
	public void loadRecipes()
	{
		XPT.mod.recipes.addRecipe(new ItemStack(this), "GIG", "ITI", 'T', XPTItems.teleporter, 'I', ODItems.GOLD, 'G', ODItems.GLOWSTONE);
	}
	
	public boolean onBlockActivated(World w, BlockPos pos0, IBlockState state, EntityPlayer ep, EnumFacing s, float x1, float y1, float z1)
	{
		ItemStack is = ep.getHeldItem();
		if(w.isRemote || is == null) return true;
		
		if(is.getItem() == XPTItems.mirror)
		{
			if(XPTConfig.levels_for_recall.get() == -1)
			{
				XPTChatMessages.RECALL_DISABLED.print(ep);
				return true;
			}
			
			LinkedPos link = ItemLinkCard.getLink(is);
			
			if(link == null || !link.pos.equals(pos0) || link.dim != w.provider.getDimensionId())
			{
				int levels = XPTConfig.only_linking_uses_xp.get() ? XPTConfig.levels_for_recall.get() : 0;
				
				if(!XPTConfig.canConsumeLevels(ep, levels)) XPTChatMessages.NEED_XP_LEVEL_LINK.print(ep, "" + levels);
				else
				{
					XPTConfig.consumeLevels(ep, levels);
					if(!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new LinkedPos(pos0, w.provider.getDimensionId()).toArray());
					XPTChatMessages.LINK_CREATED.print(ep);
				}
			}
			else XPTChatMessages.ALREADY_LINKED.print(ep);
		}
		
		return true;
	}
}