package me.buddygang.discord.ncsBot.events;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class GuildJoinedEvent extends ListenerAdapter {
	
	public void onGuildJoin(GuildJoinEvent event) {
		Guild guild = event.getGuild();
		
		System.out.printf("Joined new Guild: %s, with %s users!\n", guild.getName(), guild.getUsers().size());
		guild.getPublicChannel().sendMessage("Hello, I just joined your guild! Type `ncs help` for a list of commands!");
	}
	
}
