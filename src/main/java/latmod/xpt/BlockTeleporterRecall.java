package latmod.xpt;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.*;

public class BlockTeleporterRecall extends Block
{
	public BlockTeleporterRecall()
	{
		super(Material.wood);
		isBlockContainer = true;
		setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
	}
	
	public void loadRecipes()
	{
		if(XPTConfig.levels_for_recall > 0)
		GameRegistry.addRecipe(new ShapedOreRecipe(this, "IEI", "IPI",
				'E', "blockEmerald",
				'I', "ingotGold",
				'P', Items.ender_eye));
	}
	
	public void setBlockBoundsForItemRender() { }
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) { }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		if(XPTConfig.soft_blocks) return null;
		setBlockBoundsBasedOnState(w, x, y, z);
		return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int s, float x1, float y1, float z1)
	{
		ItemStack is = ep.getHeldItem();
		if(w.isRemote || is == null) return true;
		
		if(is.getItem() == XPT.mirror)
		{
			if(XPTConfig.levels_for_recall == -1)
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
				int levels = XPTConfig.only_linking_uses_xp ? XPTConfig.levels_for_recall : 0;
				
				if(!XPTConfig.canConsumeLevels(ep, levels))
					XPTChatMessages.getNeedLevel(false).print(ep, "" + levels);
				else
				{
					XPTConfig.consumeLevels(ep, levels);
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] { x, y, z, w.provider.dimensionId });
					XPTChatMessages.LINK_CREATED.print(ep);
				}
			}
			else XPTChatMessages.CANT_CREATE_A_LINK.print(ep);
		}
		
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{ blockIcon = ir.registerIcon("xpt:teleporter_recall"); }
	
	public boolean isOpaqueCube()
	{ return false; }
	
	public boolean renderAsNormalBlock()
	{ return false; }
}