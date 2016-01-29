package latmod.xpt.blocks;

import ftb.lib.*;
import ftb.lib.api.tile.TileLM;
import latmod.xpt.*;
import latmod.xpt.items.ItemLinkCard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.*;

public class TileTeleporter extends TileLM // TileLM // BlockXPT
{
	private String customName = "";
	public LinkedPos linked = null;
	public int cooldown = 0;
	
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		cooldown = tag.getInteger("Cooldown");
		int[] ai = tag.getIntArray("Link");
		if(ai.length == 0) linked = null;
		else linked = new LinkedPos(ai);
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		tag.setInteger("Cooldown", cooldown);
		if(linked != null) tag.setIntArray("Link", linked.toArray());
	}
	
	public void readTileClientData(NBTTagCompound tag)
	{
		customName = tag.getString("N");
		cooldown = tag.getInteger("C");
		security.readFromNBT(tag, "S");
		int[] ai = tag.getIntArray("L");
		if(ai.length == 0) linked = null;
		else linked = new LinkedPos(ai);
	}
	
	public void writeTileClientData(NBTTagCompound tag)
	{
		if(!customName.isEmpty()) tag.setString("N", customName);
		if(cooldown > 0) tag.setInteger("C", cooldown);
		security.writeToNBT(tag, "S");
		if(linked != null) tag.setIntArray("L", linked.toArray());
	}
	
	public void setName(String s)
	{ customName = s; }
	
	public String getName()
	{ return customName.isEmpty() ? (getPos().getX() + ", " + getPos().getY() + ", " + getPos().getZ() + " @ " + worldObj.provider.getDimensionName()) : customName; }
	
	public int getType()
	{
		if(worldObj != null && linked != null) return (linked.dim == getDimension()) ? 1 : 2;
		return 0;
	}
	
	public void onUpdate()
	{
		if(cooldown < 0) cooldown = 0;
		
		if(cooldown > 0)
		{
			cooldown--;
			
			if(cooldown == 0 && isServer()) markDirty();
		}
	}
	
	public boolean onRightClick(EntityPlayer ep, ItemStack is, EnumFacing side, float x, float y, float z)
	{
		if(worldObj.isRemote || is == null) return true;
		
		if(is.getItem() == Items.name_tag)
		{
			if(!is.hasDisplayName()) return true;
			
			setName(is.getDisplayName());
			
			if(!ep.capabilities.isCreativeMode) is.stackSize--;
			
			markDirty();
			
			return true;
		}
		else if(is.getItem() == XPTItems.link_card)
		{
			if(ItemLinkCard.hasData(is))
			{
				XPTChatMessages msg = XPTChatMessages.INVALID_BLOCK;
				
				int[] pos = is.getTagCompound().getIntArray(ItemLinkCard.NBT_TAG);
				
				TileTeleporter t = getTileXPT(new LinkedPos(pos));
				
				if(t != null)
				{
					boolean crossdim = pos[3] != getDimension();
					int levels = XPTConfig.only_linking_uses_xp.get() ? getLevels(t, crossdim) : 0;
					
					if(!XPTConfig.canConsumeLevels(ep, levels))
					{
						XPTChatMessages.NEED_XP_LEVEL_LINK.print(ep, "" + levels);
						return true;
					}
					
					msg = createLink(t, true);
					if(msg == XPTChatMessages.LINK_CREATED)
					{
						is.stackSize--;
						XPTConfig.consumeLevels(ep, levels);
					}
				}
				
				msg.print(ep);
			}
			else if(pos.getY() > 0)
			{
				if(is.stackSize > 1)
				{
					if(!ep.capabilities.isCreativeMode) is.stackSize--;
					
					ItemStack is1 = new ItemStack(XPTItems.link_card);
					is1.setTagCompound(new NBTTagCompound());
					is1.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] {getPos().getX(), getPos().getY(), getPos().getZ(), worldObj.provider.getDimensionId()});
					
					if(ep.inventory.addItemStackToInventory(is1)) ep.openContainer.detectAndSendChanges();
					else worldObj.spawnEntityInWorld(new EntityItem(worldObj, ep.posX, ep.posY, ep.posZ, is1));
				}
				else
				{
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] {getPos().getX(), getPos().getY(), getPos().getZ(), worldObj.provider.getDimensionId()});
				}
			}
		}
		
		return true;
	}
	
	public XPTChatMessages createLink(TileTeleporter t, boolean updateLink)
	{
		if(t == null || !isServer()) return XPTChatMessages.INVALID_BLOCK;
		if(linked != null && linked.equals(t.linked)) return XPTChatMessages.ALREADY_LINKED;
		if(t.getPos().equals(getPos()) && t.getDimension() == getDimension()) return XPTChatMessages.ALREADY_LINKED;
		
		TileTeleporter t0 = getLinkedTile();
		if(t0 != null)
		{
			t0.linked = null;
			t0.markDirty();
		}
		
		linked = new LinkedPos(t.getPos(), t.getDimension());
		
		if(updateLink)
		{
			t = getLinkedTile();
			if(t != null) t.createLink(this, false);
		}
		
		markDirty();
		return XPTChatMessages.LINK_CREATED;
	}
	
	public static TileTeleporter getTileXPT(LinkedPos link)
	{
		if(link == null || link.pos.getY() < 0 || link.pos.getY() >= 256) return null;
		
		World w = LMDimUtils.getWorld(link.dim);
		
		if(w != null)
		{
			TileEntity te = w.getTileEntity(link.pos);
			
			if(te != null && te instanceof TileTeleporter) return (TileTeleporter) te;
		}
		
		return null;
	}
	
	public TileTeleporter getLinkedTile()
	{ return getTileXPT(linked); }
	
	public void onPlayerCollided(EntityPlayerMP ep)
	{
		FTBLib.printChat(ep, "Collision!");
		if(linked != null && isServer() && cooldown <= 0 && ep.isSneaking() && !(ep instanceof FakePlayer))
		{
			ep.setSneaking(false);
			
			TileTeleporter t = getLinkedTile();
			if(t != null && (t.linked == null || equals(t.getLinkedTile())))
			{
				if(t.linked == null) t.createLink(this, false);
				
				boolean crossdim = linked.dim != getDimension();
				int levels = XPTConfig.only_linking_uses_xp.get() ? 0 : getLevels(t, crossdim);
				
				if(!XPTConfig.canConsumeLevels(ep, levels))
				{
					XPTChatMessages.NEED_XP_LEVEL_TP.print(ep, "" + levels);
					return;
				}
				
				FTBLib.playSoundEffect(worldObj, getPos().up(), "mob.endermen.portal", 1F, 1F);
				
				if(LMDimUtils.teleportPlayer(ep, linked.entityPos()))
				{
					XPTConfig.consumeLevels(ep, levels);
					cooldown = t.cooldown = XPTConfig.cooldownTicks();
					
					markDirty();
					t.markDirty();
					
					FTBLib.playSoundEffect(worldObj, linked.pos.up(), "mob.endermen.portal", 1F, 1F);
				}
			}
			else if(XPTConfig.unlink_broken.get())
			{
				XPTChatMessages.LINK_BROKEN.print(ep);
				if(t != null)
				{
					linked = null;
					markDirty();
				}
			}
		}
	}
	
	private int getLevels(TileTeleporter t, boolean crossdim)
	{
		if(crossdim) return XPTConfig.levels_for_crossdim.get();
		double dist = crossdim ? 0D : Math.sqrt(getDistanceSq(t.getPos().getX() + 0.5D, t.getPos().getY() + 0.5D, t.getPos().getZ() + 0.5D));
		return (XPTConfig.levels_for_1000_blocks.get() > 0) ? MathHelper.ceiling_double_int(XPTConfig.levels_for_1000_blocks.get() * dist / 1000D) : 0;
	}
	
	public void onPlacedBy(EntityPlayer el, ItemStack is)
	{
		if(is.hasDisplayName()) setName(is.getDisplayName());
		markDirty();
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		double d = 0.5D;
		double x = getPos().getX();
		double y = getPos().getY();
		double z = getPos().getZ();
		return new AxisAlignedBB(x - d, y, z - d, x + 1D + d, y + 2D, z + 1D + d);
	}
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{ return 64D; }
	
	public void onBroken(IBlockState state)
	{
		if(XPTConfig.unlink_broken.get()) createLink(null, true);
	}
}