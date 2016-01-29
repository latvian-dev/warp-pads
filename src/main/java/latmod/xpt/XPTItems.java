package latmod.xpt;

import latmod.xpt.block.*;
import latmod.xpt.item.*;

/**
 * Created by LatvianModder on 29.01.2016.
 */
public class XPTItems
{
	public static final BlockTeleporter teleporter = new BlockTeleporter("teleporter");
	public static final BlockTeleporterRecall teleporter_recall = new BlockTeleporterRecall("teleporter_recall");
	public static final ItemLinkCard link_card = new ItemLinkCard("link_card");
	public static final ItemMagicalMirror mirror = new ItemMagicalMirror("mirror");
	
	public static void init()
	{
		XPT.mod.addItem(teleporter);
		XPT.mod.addItem(teleporter_recall);
		XPT.mod.addItem(link_card);
		XPT.mod.addItem(mirror);
	}
}
