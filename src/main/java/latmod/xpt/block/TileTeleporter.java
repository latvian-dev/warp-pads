package latmod.xpt.block;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.LangKey;
import ftb.lib.api.tile.TileLM;
import latmod.xpt.*;
import latmod.xpt.item.ItemLinkCard;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class TileTeleporter extends TileLM
{
	public BlockDimPos linked = null;
	public int cooldown = 0;
	public int pcooldown = 0;
	private String name;
	
	@Override
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		linked = tag.hasKey("Link") ? new BlockDimPos(tag.getIntArray("Link")) : null;
		pcooldown = cooldown = tag.getInteger("Cooldown");
		name = tag.hasKey("Name") ? tag.getString("Name") : null;
	}
	
	@Override
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		if(linked != null) tag.setIntArray("Link", linked.toIntArray());
		if(cooldown > 0) tag.setInteger("Cooldown", cooldown);
		if(name != null) tag.setString("Name", name);
	}
	
	@Override
	public void readTileClientData(NBTTagCompound tag)
	{
		linked = tag.hasKey("L") ? new BlockDimPos(tag.getIntArray("L")) : null;
		pcooldown = cooldown = tag.getInteger("C");
		name = tag.hasKey("N") ? tag.getString("N") : null;
	}
	
	@Override
	public void writeTileClientData(NBTTagCompound tag)
	{
		if(linked != null) tag.setIntArray("L", linked.toIntArray());
		if(cooldown > 0) tag.setInteger("C", cooldown);
		if(name != null) tag.setString("N", name);
	}
	
	public int getType()
	{
		if(worldObj != null && linked != null) return (linked.dim == getDimension()) ? 1 : 2;
		return 0;
	}
	
	@Override
	public void onUpdate()
	{
		pcooldown = cooldown;
		if(cooldown < 0) cooldown = 0;
		
		if(cooldown > 0)
		{
			cooldown--;
			
			if(cooldown == 0 && isServer()) markDirty();
		}
	}
	
	@Override
	public boolean onRightClick(EntityPlayer ep, ItemStack is, int side, float x, float y, float z)
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
				LangKey msg = XPTLang.invalid_block;
				
				int[] pos = is.getTagCompound().getIntArray(ItemLinkCard.NBT_TAG);
				
				TileTeleporter t = getTileXPT(pos[0], pos[1], pos[2], pos[3]);
				
				if(t != null)
				{
					boolean crossdim = pos[3] != getDimension();
					int levels = XPTConfig.only_linking_uses_xp.getAsBoolean() ? getLevels(t.xCoord, t.yCoord, t.zCoord, crossdim) : 0;
					
					if(!XPTConfig.canConsumeLevels(ep, levels))
					{
						XPTLang.need_xp_level_link.printChat(ep, Integer.toString(levels));
						return true;
					}
					
					msg = createLink(t, true);
					if(msg == XPTLang.link_created)
					{
						is.stackSize--;
						XPTConfig.consumeLevels(ep, levels);
					}
				}
				
				msg.printChat(ep);
			}
			else if(yCoord > 0)
			{
				if(is.stackSize > 1)
				{
					if(!ep.capabilities.isCreativeMode) is.stackSize--;
					
					ItemStack is1 = new ItemStack(XPTItems.link_card);
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
		
		return true;
	}
	
	public LangKey createLink(TileTeleporter t, boolean updateLink)
	{
		if(t == null || !isServer()) return XPTLang.invalid_block;
		if(linked != null && linked.equalsPos(t.linked)) return XPTLang.already_linked;
		if(t.equals(this)) return XPTLang.already_linked;
		
		TileTeleporter t0 = getLinkedTile();
		if(t0 != null)
		{
			t0.linked = null;
			t0.markDirty();
		}
		
		linked = new BlockDimPos(t.xCoord, t.yCoord, t.zCoord, t.getDimension());
		
		if(updateLink)
		{
			t = getLinkedTile();
			if(t != null) t.createLink(this, false);
		}
		
		markDirty();
		return XPTLang.link_created;
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
		return getTileXPT(linked.x, linked.y, linked.z, linked.dim);
	}
	
	public void onPlayerCollided(EntityPlayerMP ep)
	{
		if(linked != null && isServer() && cooldown <= 0 && ep.isSneaking() && !(ep instanceof FakePlayer))
		{
			ep.setSneaking(false);
			
			TileTeleporter t = getLinkedTile();
			if(t == null || (t.linked == null || equals(t.getLinkedTile())))
			{
				if(t != null && t.linked == null) t.createLink(this, false);
				
				boolean crossdim = linked.dim != getDimension();
				int levels = XPTConfig.only_linking_uses_xp.getAsBoolean() ? 0 : getLevels(linked.x, linked.y, linked.z, crossdim);
				
				if(!XPTConfig.canConsumeLevels(ep, levels))
				{
					XPTLang.need_xp_level_tp.printChat(ep, Integer.toString(levels));
					return;
				}
				
				worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 1.5D, zCoord + 0.5D, "mob.endermen.portal", 1F, 1F);
				
				if(LMDimUtils.teleportEntity(ep, linked))
				{
					XPTConfig.consumeLevels(ep, levels);
					cooldown = XPTConfig.cooldownTicks();
					markDirty();
					
					if(t != null)
					{
						t.cooldown = cooldown;
						t.markDirty();
					}
					ep.worldObj.playSoundEffect(linked.x + 0.5D, linked.y + 1.5D, linked.z + 0.5D, "mob.endermen.portal", 1F, 1F);
				}
			}
		}
	}
	
	private int getLevels(int x, int y, int z, boolean crossdim)
	{
		if(crossdim) return XPTConfig.levels_for_crossdim.getAsInt();
		double dist = crossdim ? 0D : Math.sqrt(getDistanceFrom(x + 0.5D, y + 0.5D, z + 0.5D));
		return Math.max(0, ((XPTConfig.levels_for_1000_blocks.getAsInt() > 0) ? MathHelper.ceiling_double_int(XPTConfig.levels_for_1000_blocks.getAsDouble() * dist / 1000D) : 0) - 1);
	}
	
	@Override
	public void onPlacedBy(EntityPlayer el, ItemStack is)
	{
		if(is.hasDisplayName())
		{
			setName(is.getDisplayName());
		}
		markDirty();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		double d = 0.5D;
		return AxisAlignedBB.getBoundingBox(xCoord - d, yCoord, zCoord - d, xCoord + 1D + d, yCoord + 2D, zCoord + 1D + d);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{ return 64D; }
	
	@Override
	public void onBroken()
	{
		super.onBroken();
	}
	
	@Override
	public void setName(String s)
	{
		if(s == null || s.isEmpty()) name = null;
		else name = s;
	}
	
	public String getName()
	{ return name == null ? "" : name; }
}