package latmod.xpt;

import ftb.lib.api.LangKey;
import net.minecraft.command.ICommandSender;

public enum XPTChatMessages
{
	INVALID_BLOCK("invalid_block"),
	ALREADY_LINKED("already_linked"),
	LINK_BROKEN("link_broken"),
	LINK_CREATED("link_created"),
	NEED_XP_LEVEL_TP("need_xp_level_tp"),
	NEED_XP_LEVEL_LINK("need_xp_level_link"),
	RECALL_DISABLED("recall_disabled");
	
	public final LangKey id;
	
	XPTChatMessages(String s)
	{
		id = new LangKey("xpt.lang." + s);
	}
	
	public void print(ICommandSender ics, Object... o)
	{ ics.addChatMessage(id.chatComponent(o)); }
}