package me.buddygang.discord.ncsBot.commands;


import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class TestCommand extends ListenerAdapter {
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String msg = event.getMessage().getContent();

		if (msg.startsWith("ncs re")) {
			if (event.getAuthor().getId().equals("")) {
				String alert = msg.substring("ncs test ".length());
				event.getChannel().sendMessage(alert);
				
				
			} else {
				event.getChannel().sendMessage("Sorry, only my developer can do that");
			}
		}
	}

}
