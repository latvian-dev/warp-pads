package latmod.xpt.block;

import ftb.lib.*;
import ftb.lib.api.LangKey;
import ftb.lib.api.tile.TileLM;
import latmod.lib.*;
import latmod.xpt.*;
import latmod.xpt.item.ItemLinkCard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.*;

public class TileTeleporter extends TileLM
{
	public BlockDimPos linked = null;
	public int cooldown = 0;
	public int pcooldown = 0;
	public String name = "";
	
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		
		BlockDimPos link = new BlockDimPos(tag.getIntArray("Link"));
		if(link.isValid()) linked = link;
		else linked = null;
		pcooldown = cooldown = tag.getInteger("Cooldown");
		
		if(tag.hasKey("Name")) name = tag.getString("Name");
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		tag.setIntArray("Link", (linked != null) ? linked.toIntArray() : new int[0]);
		tag.setInteger("Cooldown", cooldown);
	}
	
	public void readTileClientData(NBTTagCompound tag)
	{
		IntMap data = new IntMap();
		data.fromArray(tag.getIntArray("D"));
		
		pcooldown = cooldown = Converter.nonNull(data.get(0));
		
		if(data.containsKey(1))
		{
			linked = new BlockDimPos(data.get(1), data.get(2), data.get(3), DimensionType.getById(data.get(4)));
		}
		else linked = null;
		
		name = tag.getString("N");
	}
	
	public void writeTileClientData(NBTTagCompound tag)
	{
		IntMap data = new IntMap();
		
		data.put(0, cooldown);
		
		if(linked != null)
		{
			data.put(1, linked.x);
			data.put(2, linked.y);
			data.put(3, linked.z);
			data.put(4, linked.dim.getId());
		}
		
		tag.setIntArray("D", data.toArray());
		if(!name.isEmpty()) tag.setString("N", name);
	}
	
	public int getType()
	{
		if(worldObj != null && linked != null) return (linked.dim == getDimension()) ? 1 : 2;
		return 0;
	}
	
	public void onUpdate()
	{
		pcooldown = cooldown;
		if(cooldown < 0) cooldown = 0;
		
		if(cooldown > 0)
		{
			cooldown--;
			if(cooldown == 0 && getSide().isServer()) markDirty();
		}
	}
	
	public boolean onRightClick(EntityPlayer ep, ItemStack is, EnumFacing side, EnumHand hand, float x, float y, float z)
	{
		if(worldObj.isRemote || is == null) return true;
		
		if(is.getItem() == Items.name_tag)
		{
			if(!is.hasDisplayName()) return true;
			
			name = is.getDisplayName();
			
			if(!ep.capabilities.isCreativeMode) is.stackSize--;
			
			markDirty();
			
			return true;
		}
		else if(is.getItem() == XPTItems.link_card)
		{
			if(ItemLinkCard.hasData(is))
			{
				LangKey msg = XPTLang.invalid_block;
				
				BlockDimPos pos = new BlockDimPos(is.getTagCompound().getIntArray(ItemLinkCard.NBT_TAG));
				TileTeleporter t = getTileXPT(pos);
				
				if(t != null)
				{
					boolean crossdim = pos.dim != getDimension();
					int levels = XPTConfig.only_linking_uses_xp.getAsBoolean() ? getLevels(pos.x, pos.y, pos.z, crossdim) : 0;
					
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
			else if(getPos().getY() > 0)
			{
				if(is.stackSize > 1)
				{
					if(!ep.capabilities.isCreativeMode) is.stackSize--;
					
					ItemStack is1 = new ItemStack(XPTItems.link_card);
					is1.setTagCompound(new NBTTagCompound());
					is1.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new BlockDimPos(getPos(), getDimension()).toIntArray());
					
					if(ep.inventory.addItemStackToInventory(is1)) ep.openContainer.detectAndSendChanges();
					else worldObj.spawnEntityInWorld(new EntityItem(worldObj, ep.posX, ep.posY, ep.posZ, is1));
				}
				else
				{
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, new BlockDimPos(getPos(), getDimension()).toIntArray());
				}
			}
		}
		
		return true;
	}
	
	public LangKey createLink(TileTeleporter t, boolean updateLink)
	{
		if(t == null || getSide().isClient()) return XPTLang.invalid_block;
		if(linked != null && linked.equalsPos(t.linked)) return XPTLang.already_linked;
		if(t.equals(this)) return XPTLang.already_linked;
		
		TileTeleporter t0 = getLinkedTile();
		if(t0 != null)
		{
			t0.linked = null;
			t0.markDirty();
		}
		
		linked = new BlockDimPos(t.getPos(), t.getDimension());
		
		if(updateLink)
		{
			t = getLinkedTile();
			if(t != null) t.createLink(this, false);
		}
		
		markDirty();
		return XPTLang.link_created;
	}
	
	public static TileTeleporter getTileXPT(BlockDimPos pos)
	{
		if(pos == null || pos.y < 0 || pos.y > 256) return null;
		
		World w = LMDimUtils.getWorld(pos.dim);
		
		if(w != null)
		{
			TileEntity te = w.getTileEntity(pos.toBlockPos());
			
			if(te != null && te instanceof TileTeleporter) return (TileTeleporter) te;
		}
		
		return null;
	}
	
	public TileTeleporter getLinkedTile()
	{ return getTileXPT(linked); }
	
	public void onPlayerCollided(EntityPlayerMP ep)
	{
		if(linked != null && getSide().isServer() && cooldown <= 0 && ep.isSneaking() && !(ep instanceof FakePlayer))
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
				
				worldObj.playSound(ep, getPos().getX() + 0.5D, getPos().getY() + 1.5D, getPos().getZ() + 0.5D, SoundEvents.entity_endermen_teleport, SoundCategory.BLOCKS, 1F, 1F);
				
				if(LMDimUtils.teleportPlayer(ep, linked))
				{
					XPTConfig.consumeLevels(ep, levels);
					cooldown = XPTConfig.cooldownTicks();
					markDirty();
					
					if(t != null)
					{
						t.cooldown = cooldown;
						t.markDirty();
					}
					ep.worldObj.playSound(ep, linked.x + 0.5D, linked.y + 1.5D, linked.z + 0.5D, SoundEvents.entity_endermen_teleport, SoundCategory.BLOCKS, 1F, 1F);
				}
			}
		}
	}
	
	private int getLevels(int x, int y, int z, boolean crossdim)
	{
		if(crossdim) return XPTConfig.levels_for_crossdim.getAsInt();
		double dist = Math.sqrt(getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D));
		return Math.max(0, ((XPTConfig.levels_for_1000_blocks.getAsInt() > 0) ? MathHelper.ceiling_double_int(XPTConfig.levels_for_1000_blocks.getAsInt() * dist / 1000D) : 0) - 1);
	}
	
	public void onPlacedBy(EntityPlayer ep, ItemStack is, IBlockState state)
	{
		super.onPlacedBy(ep, is, state);
		if(is.hasDisplayName()) name = is.getDisplayName();
		markDirty();
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		double d = 0.5D;
		return new AxisAlignedBB(getPos().getX() - d, getPos().getY(), getPos().getZ() - d, getPos().getX() + 1D + d, getPos().getY() + 2D, getPos().getZ() + 1D + d);
	}
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{ return 64D; }
	
	public void onBroken(IBlockState state)
	{
		super.onBroken(state);
	}
}