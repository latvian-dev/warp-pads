package latmod.xpt;

/**
 * Created by LatvianModder on 17.01.2016.
 */
public class XPTItems
{
	public static BlockTeleporter teleporter;
	public static BlockTeleporterRecall teleporter_recall;
	public static ItemLinkCard link_card;
	public static ItemMagicalMirror mirror;
	
	public static void init()
	{
		teleporter = new BlockTeleporter("teleporter").register();
		teleporter_recall = new BlockTeleporterRecall("teleporter_recall").register();
		link_card = new ItemLinkCard("link_card").register();
		mirror = new ItemMagicalMirror("mirror").register();
	}
}