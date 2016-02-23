package latmod.xpt;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

public enum XPTChatMessages
{
	INVALID_BLOCK("invalid_block"),
	ALREADY_LINKED("already_linked"),
	LINK_BROKEN("link_broken"),
	LINK_CREATED("link_created"),
	NEED_XP_LEVEL_TP("need_xp_level_tp"),
	NEED_XP_LEVEL_LINK("need_xp_level_link"),
	RECALL_DISABLED("recall_disabled");
	
	public final String id;
	
	XPTChatMessages(String s)
	{
		id = s;
	}
	
	/*public static XPTChatMessages getNeedLevel(boolean tp)
	{
		if(tp) return NEED_XP_LEVEL_TP;
		return NEED_XP_LEVEL_LINK;
	}*/
	
	public void print(ICommandSender ics, Object... o)
	{
		ics.addChatMessage(new ChatComponentTranslation("xpt.lang." + id, o));
	}
}