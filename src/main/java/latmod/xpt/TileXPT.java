package latmod.xpt;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.*;

public class TileXPT extends TileEntity // TileLM
{
	public int linkedX, linkedY, linkedZ, linkedDim, cooldown, maxCooldown;
	public String name = "";
	private boolean created = false;
	
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		linkedX = tag.getInteger("LinkX");
		linkedY = tag.getInteger("LinkY");
		linkedZ = tag.getInteger("LinkZ");
		linkedDim = tag.getInteger("LinkDim");
		cooldown = tag.getInteger("Cooldown");
		maxCooldown = tag.getInteger("MaxCooldown");
		name = tag.getString("Name");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("LinkX", linkedX);
		tag.setInteger("LinkY", linkedY);
		tag.setInteger("LinkZ", linkedZ);
		tag.setInteger("LinkDim", linkedDim);
		tag.setInteger("Cooldown", cooldown);
		tag.setString("Name", name);
		if(maxCooldown > 0) tag.setInteger("MaxCooldown", maxCooldown);
	}

	public int getIconID()
	{
		if(worldObj != null && linkedY > 0)
			return (linkedDim == worldObj.provider.dimensionId) ? 1 : 2;
		return 0;
	}
	
	public void markDirty()
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void updateEntity()
	{
		if(cooldown > 0)
		{
			cooldown--;
			
			if(!worldObj.isRemote && cooldown == 0)
				markDirty();
		}
		
		if(!worldObj.isRemote)
		{
			if(linkedY > 0 && (!created || worldObj.getTotalWorldTime() % 100L == 13L))
			{
				TileXPT t = getLinkedTile();
				
				if(t == null || !t.getLinkedTile().equals(this))
				{ linkedY = 0; markDirty(); }
			}
			
			if(!created)
			{
				created = true;
				markDirty();
			}
		}
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
	
	public void onRightClick(EntityPlayer ep, ItemStack is)
	{
		if(worldObj.isRemote || is == null) return;
		
		if(is.getItem() == Items.name_tag)
		{
			if(!is.hasDisplayName()) return;
			
			name = is.getDisplayName();
			is.stackSize--;
			markDirty();
			return;
		}
		
		if(is.getItem() != XPT.item) return;
		
		if(is.hasTagCompound() && is.stackTagCompound.hasKey("Coords"))
		{
			int[] pos = is.stackTagCompound.getIntArray("Coords");
			linkedX = pos[0];
			linkedY = pos[1];
			linkedZ = pos[2];
			linkedDim = pos[3];
			
			TileXPT t = getLinkedTile();
			
			if(t != null && !t.equals(this))
			{
				t.linkedX = xCoord;
				t.linkedY = yCoord;
				t.linkedZ = zCoord;
				t.linkedDim = worldObj.provider.dimensionId;
				cooldown = maxCooldown = t.cooldown = t.maxCooldown = 0;
				markDirty();
				t.markDirty();
				is.stackSize--;
			}
			else linkedY = 0;
		}
		else if(yCoord > 0)
		{
			if(!is.hasTagCompound()) is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setIntArray("Coords", new int[] { xCoord, yCoord, zCoord, worldObj.provider.dimensionId });
		}
	}
	
	public TileXPT getLinkedTile()
	{
		if(DimensionManager.isDimensionRegistered(linkedDim))
		{
			World w = MinecraftServer.getServer().worldServerForDimension(linkedDim);
			TileEntity te = w.getTileEntity(linkedX, linkedY, linkedZ);
			
			if(te != null && !te.isInvalid() && te instanceof TileXPT)
				return (TileXPT)te;
		}
		
		return null;
	}
	
	public void onPlayerCollided(EntityPlayer ep)
	{
		if(!worldObj.isRemote && cooldown <= 0 && ep.isSneaking() && ep instanceof EntityPlayerMP && !(ep instanceof FakePlayer))
		{
			ep.setSneaking(false);
			
			TileXPT t = getLinkedTile();
			
			if(t != null && t.getLinkedTile().equals(this) && teleportPlayer((EntityPlayerMP)ep, linkedX + 0.5D, linkedY + 1D, linkedZ + 0.5D, linkedDim))
			{
				cooldown = maxCooldown = XPTConfig.cooldown_seconds * 20;
				t.cooldown = t.maxCooldown = cooldown;
				ep.addChatMessage(new ChatComponentText("Used teleport '" + (name.isEmpty() ? (worldObj.provider.getDimensionName() + ": " + xCoord + ", " + yCoord + ", " + zCoord) : name) + "'"));
				markDirty();
				t.markDirty();
			}
		}
	}
	
	public void onPlacedBy(EntityPlayer el, ItemStack is)
	{
		if(is.hasDisplayName())
			name = is.getDisplayName();
		markDirty();
	}
	
	public boolean equals(Object o)
	{
		if(o.getClass() != TileXPT.class) return false;
		TileXPT t = (TileXPT)o;
		if(t.xCoord != xCoord) return false; if(t.yCoord != yCoord) return false; if(t.zCoord != zCoord) return false;
		if(t.worldObj != null && worldObj != null && t.worldObj.provider.dimensionId != worldObj.provider.dimensionId) return false;
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{ double d = 0.5D; return AxisAlignedBB.getBoundingBox(xCoord - d, yCoord, zCoord - d, xCoord + 1D + d, yCoord + 2D, zCoord + 1D + d); }
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{ return 64D; }
	
	/** @author CoFH */
	public static boolean teleportPlayer(EntityPlayerMP ep, double x, double y, double z, int dim)
	{
		if(ep.worldObj.isRemote || ep.isDead || !DimensionManager.isDimensionRegistered(dim)) return false;
		
		if(dim != ep.dimension)
		{
			MinecraftServer ms = MinecraftServer.getServer();
			
			int dim0 = ep.dimension;
			WorldServer w0 = ms.worldServerForDimension(ep.dimension);
			ep.dimension = dim;
			WorldServer w1 = ms.worldServerForDimension(ep.dimension);
			ep.playerNetServerHandler.sendPacket(new S07PacketRespawn(ep.dimension, ep.worldObj.difficultySetting, ep.worldObj.getWorldInfo().getTerrainType(), ep.theItemInWorldManager.getGameType()));
			w0.removePlayerEntityDangerously(ep);
			if(ep.riddenByEntity != null) ep.riddenByEntity.mountEntity(null);
			if(ep.ridingEntity != null) ep.mountEntity(null);
			
			ep.isDead = false;
			
			double d = w0.provider.getMovementFactor() / w1.provider.getMovementFactor();
			double md = 29999872D;
			
			w0.theProfiler.startSection("placing");
			
			if(ep.isEntityAlive())
			{
				ep.setLocationAndAngles(MathHelper.clamp_double(ep.posX * d, -md, md), ep.posY, MathHelper.clamp_double(ep.posZ * d, -md, md), ep.rotationYaw, ep.rotationPitch);
				w1.spawnEntityInWorld(ep);
				w1.updateEntityWithOptionalForce(ep, false);
			}
			
			w0.theProfiler.endSection();
			ep.setWorld(w1);
			
			ms.getConfigurationManager().func_72375_a(ep, w0);
			ep.playerNetServerHandler.setPlayerLocation(ep.posX, ep.posY, ep.posZ, ep.rotationYaw, ep.rotationPitch);
			ep.theItemInWorldManager.setWorld(w1);
			ms.getConfigurationManager().updateTimeAndWeatherForPlayer(ep, w1);
			ms.getConfigurationManager().syncPlayerInventory(ep);
			
			for(Object o : ep.getActivePotionEffects())
				ep.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(ep.getEntityId(), (PotionEffect)o));
			
			FMLCommonHandler.instance().firePlayerChangedDimensionEvent(ep, dim0, dim);
		}
		
		ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.rotationYaw, ep.rotationPitch);
		return true;
	}
}