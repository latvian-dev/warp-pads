package latmod.xpt.items;

import ftb.lib.LMMod;
import ftb.lib.api.item.ItemLM;
import latmod.xpt.XPT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.relauncher.*;

/**
 * Created by LatvianModder on 27.01.2016.
 */
public abstract class ItemXPT extends ItemLM
{
	public ItemXPT(String s)
	{
		super(s);
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
	
	public void onPostLoaded()
	{
	}
	
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab()
	{ return XPT.inst.creativeTab; }
}
