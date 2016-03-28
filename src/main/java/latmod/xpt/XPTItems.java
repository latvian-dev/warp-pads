package latmod.xpt;

import latmod.xpt.block.BlockTeleporter;
import latmod.xpt.item.ItemLinkCard;

/**
 * Created by LatvianModder on 29.01.2016.
 */
public class XPTItems
{
	public static final BlockTeleporter teleporter = new BlockTeleporter("teleporter");
	public static final ItemLinkCard link_card = new ItemLinkCard("link_card");
	
	public static void init()
	{
		XPT.mod.addItem(teleporter);
		XPT.mod.addItem(link_card);
	}
}
