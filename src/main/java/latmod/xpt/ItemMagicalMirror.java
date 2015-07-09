package latmod.xpt;

import java.util.List;

import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;

public class ItemMagicalMirror extends ItemLinkCard
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
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP && ep.isSneaking() && hasData(is))
		{
			if(XPTConfig.levels_for_recall <= 0)
			{
				XPTChatMessages.RECALL_DISABLED.print(ep);
				return is;
			}
			
			int levels = XPTConfig.levels_for_recall;
			
			if(!XPTConfig.canConsumeLevels(ep, levels))
			{
				XPTChatMessages.getNeedLevel(true).print(ep, "" + levels);
				return is;
			}
			
			int[] coords = is.stackTagCompound.getIntArray(NBT_TAG);
			TileTeleporter t = TileTeleporter.getTileXPT(coords[0], coords[1], coords[2], coords[3]);
			
			if(t != null && t.cooldown <= 0)
			{
				XPTConfig.consumeLevels(ep, levels);
				t.cooldown = t.maxCooldown = XPTConfig.cooldown_seconds * 100;
				t.markDirty();
				XPTChatMessages.TELEPORTED_TO.print(ep);
				Teleporter.teleportPlayer((EntityPlayerMP)ep, coords[0] + 0.5D, coords[1] + 1.5D, coords[2] + 0.5D, coords[3]);
			}
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
			World w = DimensionManager.getWorld(coords[3]);
			if(w != null) sb.append(w.provider.getDimensionName());
			else sb.append("DIM" + coords[3]);
			l.add(sb.toString());
		}
	}
}
