package latmod.xpt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.relauncher.*;

public class TileTeleporter extends TileEntity // TileLM // BlockXPT
{
	public int linkedX, linkedY, linkedZ, linkedDim, cooldown, maxCooldown;
	public String name = "";
	private boolean created = false;
	
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		if(tag.hasKey("Link") && tag.hasKey("Timer"))
		{
			int[] link = tag.getIntArray("Link");
			int[] cd = tag.getIntArray("Timer");
			
			linkedX = link[0];
			linkedY = link[1];
			linkedZ = link[2];
			linkedDim = link[3];
			
			cooldown = cd[0];
			maxCooldown = cd[1];
		}
		else
		{
			linkedX = tag.getInteger("LinkX");
			linkedY = tag.getInteger("LinkY");
			linkedZ = tag.getInteger("LinkZ");
			linkedDim = tag.getInteger("LinkDim");
			cooldown = tag.getInteger("Cooldown");
			maxCooldown = tag.getInteger("MaxCooldown");
		}
		
		name = tag.getString("Name");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setIntArray("Link", new int[] { linkedX, linkedY, linkedZ, linkedDim });
		tag.setIntArray("Timer", new int[] { cooldown, maxCooldown });
		tag.setString("Name", name);
	}
	
	public final Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setIntArray("D", new int[] { linkedX, linkedY, linkedZ, linkedDim, cooldown, maxCooldown });
		tag.setString("N", name);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		NBTTagCompound tag = pkt.func_148857_g();
		int[] data = tag.getIntArray("D");
		linkedX = data[0];
		linkedY = data[1];
		linkedZ = data[2];
		linkedDim = data[3];
		cooldown = data[4];
		maxCooldown = data[5];
		name = tag.getString("N");
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	
	public int getDim()
	{ return (worldObj == null) ? 0 : worldObj.provider.dimensionId; }

	public int getType()
	{
		if(worldObj != null && linkedY > 0)
		{
			if(linkedY == 999) return 3;
			return (linkedDim == getDim()) ? 1 : 2;
		}
		return 0;
	}
	
	public void markDirty()
	{ worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); }
	
	public boolean isServer()
	{ return !worldObj.isRemote; }
	
	public void updateEntity()
	{
		if(cooldown < 0)
			cooldown = 0;
		
		if(cooldown > 0)
		{
			cooldown--;
			
			if(cooldown == 0 && isServer())
				markDirty();
		}
		
		if(!created && isServer())
		{ created = true; markDirty(); }
	}
	
	public void onRightClick(EntityPlayer ep, ItemStack is)
	{
		if(worldObj.isRemote || is == null) return;
		
		if(is.getItem() == Items.name_tag)
		{
			if(!is.hasDisplayName()) return;
			
			name = is.getDisplayName();
			
			if(!ep.capabilities.isCreativeMode)
				is.stackSize--;
			
			markDirty();
			
			return;
		}
		
		if(is.getItem() == XPT.link_card)
		{
			if(ItemLinkCard.hasData(is))
			{
				int[] pos = is.getTagCompound().getIntArray(ItemLinkCard.NBT_TAG);
				
				TileTeleporter t = getTileXPT(pos[0], pos[1], pos[2], pos[3]);
				
				if(t != null)
				{
					boolean crossdim = pos[3] != getDim();
					int levels = XPTConfig.only_linking_uses_xp ? getLevels(t, crossdim) : 0;
					
					if(!XPTConfig.canConsumeLevels(ep, levels))
					{
						XPTChatMessages.getNeedLevel(false).print(ep, "" + levels);
						return;
					}
					
					if(createLink(t, true))
					{
						is.stackSize--;
						XPTConfig.consumeLevels(ep, levels);
						XPTChatMessages.LINK_CREATED.print(ep);
					}
					else XPTChatMessages.CANT_CREATE_A_LINK.print(ep);
				}
				else XPTChatMessages.CANT_CREATE_A_LINK.print(ep);
			}
			else if(yCoord > 0)
			{
				if(is.stackSize > 1)
				{
					if(!ep.capabilities.isCreativeMode) is.stackSize--;
					
					ItemStack is1 = new ItemStack(XPT.link_card);
					is1.setTagCompound(new NBTTagCompound());
					is1.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] { xCoord, yCoord, zCoord, worldObj.provider.dimensionId });
					
					if(ep.inventory.addItemStackToInventory(is1))
						ep.openContainer.detectAndSendChanges();
					else
						worldObj.spawnEntityInWorld(new EntityItem(worldObj, ep.posX, ep.posY, ep.posZ, is1));
				}
				else
				{
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] { xCoord, yCoord, zCoord, worldObj.provider.dimensionId });
				}
			}
		}
		else if(is.getItem() == XPT.recall_remote)
		{
			XPTConfig.levels_for_recall = 1;
			
			if(XPTConfig.levels_for_recall == 0)
			{
				XPTChatMessages.RECALL_DISABLED.print(ep);
				return;
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
			
			if(prevLinkedX != xCoord || prevLinkedY != yCoord || prevLinkedZ != zCoord || prevLinkedDim != getDim())
			{
				int levels = XPTConfig.only_linking_uses_xp ? XPTConfig.levels_for_recall : 0;
				
				if(!XPTConfig.canConsumeLevels(ep, levels))
				{
					XPTChatMessages.getNeedLevel(false).print(ep, "" + levels);
					return;
				}
				else
				{
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] { xCoord, yCoord, zCoord, worldObj.provider.dimensionId });
					linkedY = 999;
					markDirty();
				}
			}
			else XPTChatMessages.CANT_CREATE_A_LINK.print(ep);
		}
	}
	
	public boolean createLink(TileTeleporter t, boolean updateLink)
	{
		if(t == null || !isServer()) return false;
		if(t.xCoord == linkedX && t.yCoord == linkedY && t.zCoord == linkedZ && t.getDim() == linkedDim) return false;
		if(t.xCoord == xCoord && t.yCoord == yCoord && t.zCoord == zCoord && t.getDim() == getDim()) return false;
		
		TileTeleporter t0 = getLinkedTile();
		if(t0 != null)
		{
			t0.linkedY = 0;
			t0.markDirty();
		}
		
		linkedX = t.xCoord;
		linkedY = t.yCoord;
		linkedZ = t.zCoord;
		linkedDim = t.getDim();
		
		if(updateLink)
		{
			t = getLinkedTile();
			if(t != null) t.createLink(this, false);
		}
		
		markDirty();
		return true;
	}
	
	public static TileTeleporter getTileXPT(int x, int y, int z, int dim)
	{
		World w = DimensionManager.getWorld(dim);
		
		if(w != null)
		{
			TileEntity te = w.getTileEntity(x, y, z);
			
			if(te != null && te instanceof TileTeleporter)
				return (TileTeleporter)te;
		}
		
		return null;
	}
	
	public TileTeleporter getLinkedTile()
	{ if(linkedY == 0 || linkedY == 999) return null; return getTileXPT(linkedX, linkedY, linkedZ, linkedDim); }
	
	public void onPlayerCollided(EntityPlayerMP ep)
	{
		if(isServer() && cooldown <= 0 && ep.isSneaking() && !(ep instanceof FakePlayer) && linkedY > 0 && linkedY != 999)
		{
			ep.setSneaking(false);
			
			TileTeleporter t = getLinkedTile();
			if(t != null && (t.linkedY <= 0 || equals(t.getLinkedTile())))
			{
				if(t.linkedY <= 0) t.createLink(this, false);
				
				boolean crossdim = linkedDim != getDim();
				int levels = XPTConfig.only_linking_uses_xp ? 0 : getLevels(t, crossdim);
				
				if(!XPTConfig.canConsumeLevels(ep, levels))
				{
					XPTChatMessages.getNeedLevel(true).print(ep, "" + levels);
					return;
				}
				
				//worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 1D, zCoord + 0.5D, "mob.endermen.portal", 1F, 1F);
				
				//if(teleportPlayer((EntityPlayerMP)ep, linkedX + 0.5D, linkedY + 1.2D, linkedZ + 0.5D, linkedDim))
				if(Teleporter.teleportPlayer(ep, linkedX + 0.5D, linkedY + 0.3D, linkedZ + 0.5D, linkedDim))
				{
					XPTConfig.consumeLevels(ep, levels);
					cooldown = maxCooldown = t.cooldown = t.maxCooldown = XPTConfig.cooldown_seconds * 20;
					
					ep.motionY = 01.05D;
					XPTChatMessages.TELEPORTED_TO.print(ep, getName());
					markDirty();
					t.markDirty();
					
					//worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
				}
			}
			else if(XPTConfig.unlink_broken)
			{
				XPTChatMessages.LINK_BROKEN.print(ep);
				if(t != null) { linkedY = 0; markDirty(); }
			}
		}
	}
	
	private int getLevels(TileTeleporter t, boolean crossdim)
	{
		if(crossdim) return XPTConfig.levels_for_crossdim;
		double dist = crossdim ? 0D : Math.sqrt(getDistanceFrom(t.xCoord + 0.5D, t.yCoord + 0.5D, t.zCoord + 0.5D));
		return (XPTConfig.levels_for_1000_blocks > 0) ? MathHelper.ceiling_double_int(XPTConfig.levels_for_1000_blocks * dist / 1000D) : 0;
	}
	
	public void onPlacedBy(EntityPlayer el, ItemStack is)
	{
		if(is.hasDisplayName())
			name = is.getDisplayName();
		markDirty();
	}
	
	public boolean equals(Object o)
	{
		if(o == null || o.getClass() != TileTeleporter.class) return false;
		TileTeleporter t = (TileTeleporter)o;
		if(t.xCoord != xCoord) return false;
		if(t.yCoord != yCoord) return false;
		if(t.zCoord != zCoord) return false;
		if(t.getDim() != getDim()) return false;
		return true;
	}
	
	public String getName()
	{ return name.isEmpty() ? (xCoord + ", " + yCoord + ", " + zCoord + " @ " + worldObj.provider.getDimensionName()) : name; }
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{ double d = 0.5D; return AxisAlignedBB.getBoundingBox(xCoord - d, yCoord, zCoord - d, xCoord + 1D + d, yCoord + 2D, zCoord + 1D + d); }
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{ return 64D; }

	public void onBroken()
	{
		if(XPTConfig.unlink_broken)
			createLink(null, true);
	}
}