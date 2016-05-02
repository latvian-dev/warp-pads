package latmod.xpt.block;

import ftb.lib.BlockDimPos;
import ftb.lib.LMDimUtils;
import ftb.lib.api.LangKey;
import ftb.lib.api.tile.TileLM;
import latmod.lib.Converter;
import latmod.lib.IntMap;
import latmod.xpt.XPTConfig;
import latmod.xpt.XPTItems;
import latmod.xpt.XPTLang;
import latmod.xpt.item.ItemLinkCard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileTeleporter extends TileLM
{
	public BlockDimPos linked = null;
	public int cooldown = 0;
	public int pcooldown = 0;
	public String name = "";
	
	@Override
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		
		BlockDimPos link = new BlockDimPos(tag.getIntArray("Link"));
		linked = link.pos.getY() > 0 ? link : null;
		pcooldown = cooldown = tag.getInteger("Cooldown");
		
		if(tag.hasKey("Name")) { name = tag.getString("Name"); }
	}
	
	@Override
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		tag.setIntArray("Link", (linked != null) ? linked.toIntArray() : new int[0]);
		tag.setInteger("Cooldown", cooldown);
	}
	
	@Override
	public void readTileClientData(NBTTagCompound tag)
	{
		IntMap data = new IntMap();
		data.fromArray(tag.getIntArray("D"));
		
		pcooldown = cooldown = Converter.nonNull(data.get(0));
		
		if(data.containsKey(1))
		{
			linked = new BlockDimPos(new BlockPos(data.get(1), data.get(2), data.get(3)), DimensionType.getById(data.get(4)));
		}
		else { linked = null; }
		
		name = tag.getString("N");
	}
	
	@Override
	public void writeTileClientData(NBTTagCompound tag)
	{
		IntMap data = new IntMap();
		
		data.put(0, cooldown);
		
		if(linked != null)
		{
			data.put(1, linked.pos.getX());
			data.put(2, linked.pos.getY());
			data.put(3, linked.pos.getZ());
			data.put(4, linked.dim.getId());
		}
		
		tag.setIntArray("D", data.toArray());
		if(!name.isEmpty()) { tag.setString("N", name); }
	}
	
	@Override
	public void onUpdate()
	{
		pcooldown = cooldown;
		if(cooldown < 0) { cooldown = 0; }
		
		if(cooldown > 0)
		{
			cooldown--;
			if(cooldown == 0 && getSide().isServer()) { markDirty(); }
		}
	}
	
	@Override
	public boolean onRightClick(EntityPlayer ep, ItemStack is, EnumFacing side, EnumHand hand, float x, float y, float z)
	{
		if(worldObj.isRemote) { return true; }
		
		if(is != null && is.getItem() == Items.NAME_TAG)
		{
			if(!is.hasDisplayName()) { return true; }
			
			name = is.getDisplayName();
			
			if(!ep.capabilities.isCreativeMode) { is.stackSize--; }
			
			markDirty();
			
			return true;
		}
		else if(is != null && is.getItem() == XPTItems.link_card)
		{
			if(ItemLinkCard.hasData(is))
			{
				LangKey msg = XPTLang.invalid_block;
				
				BlockDimPos pos = new BlockDimPos(is.getTagCompound().getIntArray(ItemLinkCard.NBT_TAG));
				TileTeleporter t = getTileXPT(pos);
				
				if(t != null)
				{
					boolean crossdim = pos.dim != getDimPos().dim;
					int levels = XPTConfig.only_linking_uses_xp.getAsBoolean() ? getLevels(pos.pos, crossdim) : 0;
					
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
					if(!ep.capabilities.isCreativeMode) { is.stackSize--; }
					
					ItemStack is1 = new ItemStack(XPTItems.link_card);
					is1.setTagCompound(new NBTTagCompound());
					is1.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, getDimPos().toIntArray());
					
					if(ep.inventory.addItemStackToInventory(is1)) { ep.openContainer.detectAndSendChanges(); }
					else { worldObj.spawnEntityInWorld(new EntityItem(worldObj, ep.posX, ep.posY, ep.posZ, is1)); }
				}
				else
				{
					is.setTagCompound(new NBTTagCompound());
					is.getTagCompound().setIntArray(ItemLinkCard.NBT_TAG, getDimPos().toIntArray());
				}
			}
		}
		else
		{
			if(linked != null && getSide().isServer() && cooldown <= 0 && !(ep instanceof FakePlayer))
			{
				TileTeleporter t = getLinkedTile();
				if(t == null || (t.linked == null || equals(t.getLinkedTile())))
				{
					if(t != null && t.linked == null) { t.createLink(this, false); }
					
					boolean crossdim = linked.dim != getDimPos().dim;
					int levels = XPTConfig.only_linking_uses_xp.getAsBoolean() ? 0 : getLevels(linked.pos, crossdim);
					
					if(!XPTConfig.canConsumeLevels(ep, levels))
					{
						XPTLang.need_xp_level_tp.printChat(ep, Integer.toString(levels));
						return true;
					}
					
					playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
					
					cooldown = XPTConfig.cooldownTicks();
					
					if(LMDimUtils.teleportPlayer(ep, linked))
					{
						XPTConfig.consumeLevels(ep, levels);
						markDirty();
						
						if(t != null)
						{
							t.cooldown = cooldown;
							t.markDirty();
						}
						
						playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1F);
					}
				}
			}
		}
		
		return true;
	}
	
	public LangKey createLink(TileTeleporter t, boolean updateLink)
	{
		if(t == null || getSide().isClient()) { return XPTLang.invalid_block; }
		if(linked != null && linked.equalsPos(t.linked)) { return XPTLang.already_linked; }
		if(t.equals(this)) { return XPTLang.already_linked; }
		
		TileTeleporter t0 = getLinkedTile();
		if(t0 != null)
		{
			t0.linked = null;
			t0.markDirty();
		}
		
		linked = t.getDimPos().copy();
		
		if(updateLink)
		{
			t = getLinkedTile();
			if(t != null) { t.createLink(this, false); }
		}
		
		markDirty();
		return XPTLang.link_created;
	}
	
	public static TileTeleporter getTileXPT(BlockDimPos pos)
	{
		if(pos == null || pos.pos.getY() < 0 || pos.pos.getY() >= 256) { return null; }
		
		World w = LMDimUtils.getWorld(pos.dim);
		
		if(w != null)
		{
			TileEntity te = w.getTileEntity(pos.pos);
			
			if(te != null && te instanceof TileTeleporter) { return (TileTeleporter) te; }
		}
		
		return null;
	}
	
	public TileTeleporter getLinkedTile()
	{ return getTileXPT(linked); }
	
	private int getLevels(Vec3i pos, boolean crossdim)
	{
		if(crossdim) { return XPTConfig.levels_for_crossdim.getAsInt(); }
		double dist = Math.sqrt(getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D));
		return Math.max(0, ((XPTConfig.levels_for_1000_blocks.getAsInt() > 0) ? MathHelper.ceiling_double_int(XPTConfig.levels_for_1000_blocks.getAsInt() * dist / 1000D) : 0) - 1);
	}
	
	@Override
	public void onPlacedBy(EntityPlayer ep, ItemStack is, IBlockState state)
	{
		super.onPlacedBy(ep, is, state);
		if(is.hasDisplayName()) { name = is.getDisplayName(); }
		markDirty();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		double d = 0.5D;
		return new AxisAlignedBB(getPos().getX() - d, getPos().getY(), getPos().getZ() - d, getPos().getX() + 1D + d, getPos().getY() + 2D, getPos().getZ() + 1D + d);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{ return 64D; }
	
	@Override
	public void onBroken(IBlockState state)
	{
		super.onBroken(state);
	}
}