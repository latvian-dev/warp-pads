package latmod.xpt;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import latmod.lib.IntMap;
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
import net.minecraftforge.common.util.FakePlayer;

public class TileTeleporter extends TileEntity // TileLM // BlockXPT
{
	public EntityPos linked = null;
	public int cooldown = 0;
	public String name = "";
	private boolean created = false;
	
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		if(tag.hasKey("Link") && tag.hasKey("Timer"))
		{
			int[] link = tag.getIntArray("Link");
			cooldown = (tag.func_150299_b("Timer") == LMNBTUtils.INT) ? tag.getInteger("Timer") : tag.getIntArray("Timer")[0];
			
			if(link.length >= 4)
			{
				linked = new EntityPos();
				linked.setPos(link[0], link[1], link[2], link[3]);
			}
			else linked = null;
		}
		cooldown = tag.getInteger("Cooldown");
		name = tag.getString("Name");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setIntArray("Link", (linked != null) ? new int[] {linked.intX(), linked.intY(), linked.intZ(), linked.dim} : new int[0]);
		tag.setInteger("Timer", cooldown);
		tag.setString("Name", name);
	}
	
	public final Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		IntMap data = new IntMap();
		
		data.put(0, cooldown);
		
		if(linked != null)
		{
			data.put(1, linked.intX());
			data.put(2, linked.intY());
			data.put(3, linked.intZ());
			data.put(4, linked.dim);
		}
		
		tag.setIntArray("D", data.toIntArray());
		tag.setString("N", name);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		NBTTagCompound tag = pkt.func_148857_g();
		IntMap data = IntMap.fromIntArrayS(tag.getIntArray("D"));
		
		cooldown = data.get(0);
		
		if(data.keys.contains(1))
		{
			linked = new EntityPos();
			linked.setPos(data.get(1), data.get(2), data.get(3), data.get(4));
		}
		else linked = null;
		
		name = tag.getString("N");
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	
	public int getDim()
	{ return (worldObj == null) ? 0 : worldObj.provider.dimensionId; }
	
	public int getType()
	{
		if(worldObj != null && linked != null) return (linked.dim == getDim()) ? 1 : 2;
		return 0;
	}
	
	public void markDirty()
	{ worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); }
	
	public boolean isServer()
	{ return !worldObj.isRemote; }
	
	public void updateEntity()
	{
		if(cooldown < 0) cooldown = 0;
		
		if(cooldown > 0)
		{
			cooldown--;
			
			if(cooldown == 0 && isServer()) markDirty();
		}
		
		if(!created && isServer())
		{
			created = true;
			markDirty();
		}
	}
	
	public void onRightClick(EntityPlayer ep, ItemStack is)
	{
		if(worldObj.isRemote || is == null) return;
		
		if(is.getItem() == Items.name_tag)
		{
			if(!is.hasDisplayName()) return;
			
			name = is.getDisplayName();
			
			if(!ep.capabilities.isCreativeMode) is.stackSize--;
			
			markDirty();
			
			return;
		}
		else if(is.getItem() == XPT.link_card)
		{
			if(ItemLinkCard.hasData(is))
			{
				XPTChatMessages msg = XPTChatMessages.INVALID_BLOCK;
				
				int[] pos = is.getTagCompound().getIntArray(ItemLinkCard.NBT_TAG);
				
				TileTeleporter t = getTileXPT(pos[0], pos[1], pos[2], pos[3]);
				
				if(t != null)
				{
					boolean crossdim = pos[3] != getDim();
					int levels = XPTConfig.only_linking_uses_xp.get() ? getLevels(t, crossdim) : 0;
					
					if(!XPTConfig.canConsumeLevels(ep, levels))
					{
						XPTChatMessages.NEED_XP_LEVEL_LINK.print(ep, "" + levels);
						return;
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
			else if(yCoord > 0)
			{
				if(is.stackSize > 1)
				{
					if(!ep.capabilities.isCreativeMode) is.stackSize--;
					
					ItemStack is1 = new ItemStack(XPT.link_card);
					is1.setTagCompound(new NBTTagCompound());
					is1.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] {xCoord, yCoord, zCoord, worldObj.provider.dimensionId});
					
					if(ep.inventory.addItemStackToInventory(is1)) ep.openContainer.detectAndSendChanges();
					else worldObj.spawnEntityInWorld(new EntityItem(worldObj, ep.posX, ep.posY, ep.posZ, is1));
				}
				else
				{
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new int[] {xCoord, yCoord, zCoord, worldObj.provider.dimensionId});
				}
			}
		}
	}
	
	public ChunkCoordinates getPos()
	{ return new ChunkCoordinates(xCoord, yCoord, zCoord); }
	
	public XPTChatMessages createLink(TileTeleporter t, boolean updateLink)
	{
		if(t == null || !isServer()) return XPTChatMessages.INVALID_BLOCK;
		if(linked != null && linked.equalsIntPos(t.linked)) return XPTChatMessages.ALREADY_LINKED;
		if(t.xCoord == xCoord && t.yCoord == yCoord && t.zCoord == zCoord && t.getDim() == getDim())
			return XPTChatMessages.ALREADY_LINKED;
		
		TileTeleporter t0 = getLinkedTile();
		if(t0 != null)
		{
			t0.linked = null;
			t0.markDirty();
		}
		
		linked = new EntityPos(t.xCoord, t.yCoord, t.zCoord, t.getDim());
		
		if(updateLink)
		{
			t = getLinkedTile();
			if(t != null) t.createLink(this, false);
		}
		
		markDirty();
		return XPTChatMessages.LINK_CREATED;
	}
	
	public static TileTeleporter getTileXPT(int x, int y, int z, int dim)
	{
		if(y < 0 || y > 256) return null;
		
		World w = LMDimUtils.getWorld(dim);
		
		if(w != null)
		{
			TileEntity te = w.getTileEntity(x, y, z);
			
			if(te != null && te instanceof TileTeleporter) return (TileTeleporter) te;
		}
		
		return null;
	}
	
	public TileTeleporter getLinkedTile()
	{
		if(linked == null) return null;
		return getTileXPT(linked.intX(), linked.intY(), linked.intZ(), linked.dim);
	}
	
	public void onPlayerCollided(EntityPlayerMP ep)
	{
		if(linked != null && isServer() && cooldown <= 0 && ep.isSneaking() && !(ep instanceof FakePlayer))
		{
			ep.setSneaking(false);
			
			TileTeleporter t = getLinkedTile();
			if(t != null && (t.linked == null || equals(t.getLinkedTile())))
			{
				if(t.linked == null) t.createLink(this, false);
				
				boolean crossdim = linked.dim != getDim();
				int levels = XPTConfig.only_linking_uses_xp.get() ? 0 : getLevels(t, crossdim);
				
				if(!XPTConfig.canConsumeLevels(ep, levels))
				{
					XPTChatMessages.NEED_XP_LEVEL_TP.print(ep, "" + levels);
					return;
				}
				
				worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 1.5D, zCoord + 0.5D, "mob.endermen.portal", 1F, 1F);
				
				if(LMDimUtils.teleportPlayer(ep, linked.center()))
				{
					XPTConfig.consumeLevels(ep, levels);
					cooldown = t.cooldown = XPTConfig.cooldownTicks();
					
					markDirty();
					t.markDirty();
					
					t.worldObj.playSoundEffect(linked.intX() + 0.5D, linked.intY() + 1.5D, linked.intZ() + 0.5D, "mob.endermen.portal", 1F, 1F);
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
		double dist = crossdim ? 0D : Math.sqrt(getDistanceFrom(t.xCoord + 0.5D, t.yCoord + 0.5D, t.zCoord + 0.5D));
		return (XPTConfig.levels_for_1000_blocks.get() > 0) ? MathHelper.ceiling_double_int(XPTConfig.levels_for_1000_blocks.get() * dist / 1000D) : 0;
	}
	
	public void onPlacedBy(EntityPlayer el, ItemStack is)
	{
		if(is.hasDisplayName()) name = is.getDisplayName();
		markDirty();
	}
	
	public boolean equals(Object o)
	{
		if(o == null || o.getClass() != TileTeleporter.class) return false;
		TileTeleporter t = (TileTeleporter) o;
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
	{
		double d = 0.5D;
		return AxisAlignedBB.getBoundingBox(xCoord - d, yCoord, zCoord - d, xCoord + 1D + d, yCoord + 2D, zCoord + 1D + d);
	}
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{ return 64D; }
	
	public void onBroken()
	{
		if(XPTConfig.unlink_broken.get()) createLink(null, true);
	}
}