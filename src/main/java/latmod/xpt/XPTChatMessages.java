package latmod.xpt;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

public enum XPTChatMessages
{
	CANT_CREATE_A_LINK("cant_link"),
	LINK_BROKEN("link_broken"),
	TELEPORTED_TO("tp_to"),
	LINK_CREATED("link_created"),
	NEED_XP_LEVEL_TP("need_xp_level_tp"),
	NEED_FOOD_LEVEL_TP("need_food_level_tp"),
	NEED_XP_LEVEL_LINK("need_xp_level_link"),
	NEED_FOOD_LEVEL_LINK("need_food_level_link"),
	RECALL_DISABLED("recall_disabled");
	
	public final String id;
	
	XPTChatMessages(String s)
	{
		id = s;
	}
	
	public static XPTChatMessages getNeedLevel(boolean tp)
	{
		if(tp)
		{
			if(XPTConfig.use_food_levels > 0)
				return NEED_FOOD_LEVEL_TP;
			return NEED_XP_LEVEL_TP;
		}
		
		if(XPTConfig.use_food_levels > 0)
			return NEED_FOOD_LEVEL_LINK;
		return NEED_XP_LEVEL_LINK;
	}
	
	public void print(ICommandSender ics, Object... o)
	{
		ics.addChatMessage(new ChatComponentTranslation("xpt.lang." + id, o));
	}
}