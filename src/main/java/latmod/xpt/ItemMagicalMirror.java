package latmod.xpt;

import java.util.List;

import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;

public class ItemMagicalMirror extends ItemLinkCard // ItemBow
{
	public ItemMagicalMirror()
	{
		super();
		setMaxStackSize(1);
	}
	
	public void loadRecipes()
	{
		if(XPTConfig.levels_for_recall > 0)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this), "GGL", "GSG", "LGG",
					'L', XPT.link_card,
					'G', Blocks.glass_pane,
					'S', Items.nether_star));
	}
	
	public int getItemStackLimit(ItemStack is)
	{ return 1; }
	
	public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer ep, int p_77615_4_)
	{
	}
	
	public ItemStack onEaten(ItemStack is, World w, EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP)
		{
			if(XPTConfig.levels_for_recall == -1)
			{
				XPTChatMessages.RECALL_DISABLED.print(ep);
				return is;
			}
			else if(!hasData(is))
			{
				XPTChatMessages.LINK_BROKEN.print(ep);
				return is;
			}
			
			int levels = XPTConfig.only_linking_uses_xp ? 0 : XPTConfig.levels_for_recall;
			
			if(!XPTConfig.canConsumeLevels(ep, levels))
			{
				XPTChatMessages.getNeedLevel(true).print(ep, "" + levels);
				return is;
			}
			
			int[] coords = is.stackTagCompound.getIntArray(NBT_TAG);
			
			World w1 = DimensionManager.getWorld(coords[3]);
			
			if(w1.getBlock(coords[0], coords[1], coords[2]) == XPT.teleporter_recall)
			{
				XPTConfig.consumeLevels(ep, levels);
				XPTChatMessages.TELEPORTED_TO.print(ep);
				w.playSoundEffect(ep.posX, ep.posY + 1.5D, ep.posZ, "mob.endermen.portal", 1F, 1F);
				Teleporter.teleportPlayer((EntityPlayerMP)ep, coords[0] + 0.5D, coords[1] + 0.2D, coords[2] + 0.5D, coords[3]);
				w.playSoundEffect(coords[0] + 0.5D, coords[1] + 1.5D, coords[2] + 0.5D, "mob.endermen.portal", 1F, 1F);
			}
			else XPTChatMessages.LINK_BROKEN.print(ep);
		}
		
		return is;
	}
	
	public int getMaxItemUseDuration(ItemStack is)
	{ return 28; }
	
	public EnumAction getItemUseAction(ItemStack p_77661_1_)
	{ return EnumAction.bow; }
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{ ep.setItemInUse(is, getMaxItemUseDuration(is)); return is; }
	
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
			World w = DimensionManager.getWorld(coords[3]);
			if(w != null) sb.append(w.provider.getDimensionName());
			else sb.append("DIM" + coords[3]);
			l.add(sb.toString());
		}
	}
}
