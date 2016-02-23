package latmod.xpt.item;

import ftb.lib.LMMod;
import ftb.lib.api.item.ItemLM;
import latmod.xpt.XPT;

/**
 * Created by LatvianModder on 06.02.2016.
 */
public class ItemXPT extends ItemLM
{
	public ItemXPT(String s)
	{
		super(s);
		setCreativeTab(XPT.tab);
	}
	
	public LMMod getMod()
	{ return XPT.mod; }
}