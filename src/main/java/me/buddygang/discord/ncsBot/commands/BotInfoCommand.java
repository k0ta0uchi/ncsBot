package me.buddygang.discord.ncsBot.commands;

import me.buddygang.discord.ncsBot.NCSBot;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class BotInfoCommand extends ListenerAdapter {
	
	public void onMessageReceived(MessageReceivedEvent event) {
		String msg = event.getMessage().getContent();
		User author = event.getAuthor();
		
		//Add Command
		if(msg.equalsIgnoreCase("ncs bot {Debug:true}") || msg.equalsIgnoreCase("ncs bot {Debug=true}")) {
			long time = (System.currentTimeMillis() - NCSBot.startTime);
			System.out.printf("StartTime: %s, Time: %s", NCSBot.startTime, time);
			long days = time / 86400000;
			long hours = time / 216000000;
			long mins = time / 3600000;
			long secs = time / 60000;
			if(event.isPrivate()) {
				author.getPrivateChannel().sendMessage("Commands are `Server Only`!");
			} else {
				event.getChannel().sendMessage("**Bot Info** `Debug Mode`\n"
						                     + "```json\n"
						                     + "{\n"
						                     + "    \"botName\":\"NoCopyrightSounds\",\n"
						                     + "    \"version\":\"0.0.1a-alpha\",\n"
						                     + "    \"devloper\":\"BuddyGang\",\n"
						                     + "    \"helpCommand\":\"ncs help\",\n"
						                     + "    \"github\":\"https://github.com/JoeZwet/ncsBot\",\n"
						                     + "    \"uptime\":{\n"
						                     + "        \"days\":\"" + days + "\",\n"
						                     + "        \"hours\":\"" + hours + "\",\n"
						                     + "        \"minutes\":\"" + mins + "\",\n"
						                     + "        \"seconds\":\"" + secs + "\"\n"
						                     + "    },\n"
						                     + "    \"development\":{\n"
						                     + "        \"language\":\"Java\",\n"
						                     + "        \"libraries\":[\n  "
						                     + "            {\n"
						                     + "                \"type\":\"text\",\n"
						                     + "                \"name\":\"JDA\",\n"
						                     + "                \"version\":\"2.2.1\",\n"
						                     + "                \"developer\":\"DV8FromTheWorld\",\n"
						                     + "                \"github\":\"https://github.com/DV8FromTheWorld/JDA\"\n"
						                     + "            },\n"
						                     + "            {\n"
						                     + "                \"type\":\"audio\",\n"
						                     + "                \"name\":\"JDA-Player\",\n"
						                     + "                \"version\":\"0.2.3\",\n"
						                     + "                \"build\":\"24\",\n"
						                     + "                \"developer\":\"DV8FromTheWorld\",\n"
						                     + "                \"github\":\"https://github.com/DV8FromTheWorld/JDA-Player\"\n"
						                     + "            }\n"
						                     + "        ]\n"
						                     + "    }\n"
						                     + "}\n"
						                     + "```");
			}
		}
		if(msg.equalsIgnoreCase("ncs bot {Debug:false}") || msg.equalsIgnoreCase("ncs bot {Debug=false}") || msg.equalsIgnoreCase("ncs bot")) {
			long time = (System.nanoTime() - NCSBot.startTime)  / 1000000;
			long days = time / 86400000;
			long hours = time / 216000000;
			long mins = time / 3600000;
			long secs = time / 60000;
			if(event.isPrivate()) {
				author.getPrivateChannel().sendMessage("Commands are `Server Only`!");
			} else {
				event.getChannel().sendMessage("**Bot Info**\n"
						                     + "```\n"
						                     + "Name: NoCopyrightSounds\n"
						                     + "Version: 0.0.1a-alpha\n"
						                     + "Developer: BuddyGang\n"
						                     + "Help Command: ncs help\n"
						                     + "Uptime: " + days + " days, " + hours + " hours, " + mins + " minutes, " + secs + " seconds\n"
						                     + "Libraries: JDA (text), JDA-Player (audio)\n"
						                     + "```");
			}
		}
	}

}