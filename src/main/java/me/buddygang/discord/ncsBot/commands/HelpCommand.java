package me.buddygang.discord.ncsBot.commands;

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class HelpCommand extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String msg = event.getMessage().getContent();

		if (msg.equalsIgnoreCase("ncs help")) {
			event.getChannel()
					.sendMessage("All commands can be found at: https://github.com/JoeZwet/ncsBot/#commands \n"
							+ "All music types can be found at: https://github.com/JoeZwet/ncsBot/#music-types");
		}
		if (msg.equalsIgnoreCase("ncs reloadUser")) {
			event.getJDA().getAccountManager().update();
			event.getChannel().sendMessage("Reloaded Bot User.");
		}
	}

}
