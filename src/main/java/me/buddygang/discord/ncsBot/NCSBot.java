package me.buddygang.discord.ncsBot;

import javax.security.auth.login.LoginException;

import me.buddygang.discord.ncsBot.commands.BotInfoCommand;
import me.buddygang.discord.ncsBot.commands.FeedbackCommand;
import me.buddygang.discord.ncsBot.commands.HelpCommand;
import me.buddygang.discord.ncsBot.commands.InviteCommand;
import me.buddygang.discord.ncsBot.commands.UpdateCommand;
import me.buddygang.discord.ncsBot.events.GuildJoinedEvent;
import me.buddygang.discord.ncsBot.music.MusicCommands;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

public class NCSBot {

	public static long startTime;

	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		try {

			JDA api = new JDABuilder().setBotToken("MjM3NDUxODE3MTMzNDA4MjU3.CuX5Bg.94p11vtaV8m9_sJWWEMemEgstNc")
					.addListener(new MusicCommands()).addListener(new BotInfoCommand()).addListener(new HelpCommand())
					.addListener(new GuildJoinedEvent()).addListener(new InviteCommand())
					.addListener(new FeedbackCommand())
					// .addListener(new AlertCommand())
					// .addListener(new TestCommand())
					.addListener(new UpdateCommand()).setBulkDeleteSplittingEnabled(false).buildBlocking();
			api.getAccountManager().setGame("on " + api.getGuilds().size() + " Guilds.");
			System.out.printf("Guilds: %s\n", api.getGuilds().size());

		} catch (LoginException e) {
			System.out.println("The provided bot token was incorrect. Please provide valid details.\n");
		} catch (IllegalArgumentException e) {
			System.out.println("The config was not populated. Please enter a bot token.\n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
