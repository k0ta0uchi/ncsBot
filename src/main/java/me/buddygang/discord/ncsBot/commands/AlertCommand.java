package me.buddygang.discord.ncsBot.commands;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class AlertCommand extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String msg = event.getMessage().getContent();

		if (msg.startsWith("ncs alert ")) {
			if (event.getAuthor().getId().equals("97172171259904000")) {

				String alert = msg.substring("ncs alert ".length());
				JDA api = event.getJDA();
				for (int i = 0; i < api.getGuilds().size(); i++) {
					Guild guild = api.getGuilds().get(i);
					if (guild.getId() == "110373943822540800") {

					} else {
						guild.getPublicChannel().sendMessage("[**ALERT FROM DEVELOPER**]```" + alert + "```");
					}
					if (i == api.getGuilds().size() + 1) {
						i = 0;
					}
				}
			} else {
				event.getChannel().sendMessage("Sorry, only my developer can do that");
			}
		}
	}

}
