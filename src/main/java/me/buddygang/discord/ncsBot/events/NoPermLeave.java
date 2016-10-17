package me.buddygang.discord.ncsBot.events;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.utils.PermissionUtil;

public class NoPermLeave {
	
	private JDA api;
	
	public void noPermLeave() {
		for(int i = 0; i < api.getGuilds().size(); i++) {
			Guild guild = api.getGuilds().get(i);
			if(PermissionUtil.canTalk(guild.getPublicChannel())) {
				
			} else {
				guild.getManager().leave();
			}
		}
	}

}
