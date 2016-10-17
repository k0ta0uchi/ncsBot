package me.buddygang.discord.ncsBot.commands;

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class InviteCommand extends ListenerAdapter {
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String msg = event.getMessage().getContent();
		
		if(msg.equalsIgnoreCase("ncs invite")) {
			event.getChannel().sendMessage("To add me to your guild, please goto this link: https://discordapp.com/oauth2/authorize?permissions=338750480&client_id=230029198964555778&scope=bot");
		}
	}
}
