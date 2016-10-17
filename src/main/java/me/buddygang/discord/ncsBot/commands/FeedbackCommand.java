package me.buddygang.discord.ncsBot.commands;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class FeedbackCommand extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String msg = event.getMessage().getContent();
		if(msg.startsWith("ncs feedback ")) {
			String feedback = msg.substring("ncs feedback ".length());
			List<String> lines = Arrays.asList(event.getAuthorName() + "'s Feedback", feedback);
			Path file = Paths.get("feedback/" + event.getGuild().getName() + "-" + System.currentTimeMillis() + ".txt");
			try {
				Files.write(file, lines, Charset.forName("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			event.getChannel().sendMessage("You Feedback has been recorded, if your feedback is an error please join the NCSBot Guild: https://discord.gg/U3jUtRe");
		}
	}
	
}
