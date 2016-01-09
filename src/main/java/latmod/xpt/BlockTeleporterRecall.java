package latmod.xpt;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockTeleporterRecall extends BlockTeleporterBase
{
	public BlockTeleporterRecall()
	{
		super();
		textureName = "teleporter_recall";
	}
	
	public void loadRecipes()
	{
		if(XPTConfig.levels_for_recall.get() > 0)
			GameRegistry.addRecipe(new ShapedOreRecipe(this, "IEI", "IPI", 'E', "blockEmerald", 'I', "ingotGold", 'P', Items.ender_eye));
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		ItemStack is = ep.getHeldItem();
		if(w.isRemote || is == null) return true;
		
		if(is.getItem() == XPT.mirror)
		{
			if(XPTConfig.levels_for_recall.get() == -1)
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
				int levels = XPTConfig.only_linking_uses_xp.get() ? XPTConfig.levels_for_recall.get() : 0;
				
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