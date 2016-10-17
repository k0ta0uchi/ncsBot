package me.buddygang.discord.ncsBot.commands;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.PermissionUtil;

public class UpdateCommand extends ListenerAdapter {
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String msg = event.getMessage().getContent();
		
		if(msg.startsWith("ncs update ")) {
			if (event.getAuthor().getId().equals("97172171259904000")) {

				String alert = msg.substring("ncs update ".length());
				JDA api = event.getJDA();
				event.getChannel().sendMessage("[**Update**]```" + alert + "```");
				for (int i = 0; i < api.getGuilds().size(); i++) {
					Guild guild = api.getGuilds().get(i);
					if (guild.getId() == "110373943822540800") {
					} else {
						if(PermissionUtil.canTalk(guild.getPublicChannel())) {
							guild.getPublicChannel().sendMessage("[**Update**]```" + alert + "```");
						   } else {
							   System.out.printf("I don't have the Permission: MESSAGE_WRITE, In the guild: %s\n", guild.getName());
						   }
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
