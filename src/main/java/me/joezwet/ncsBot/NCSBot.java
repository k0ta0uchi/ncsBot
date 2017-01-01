package me.joezwet.ncsBot;

import javax.security.auth.login.LoginException;

import me.joezwet.ncsBot.audio.AudioCommandHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class NCSBot {
	
	public static JDA api;

	public static void main(String[] args) {
		try {
			api = new JDABuilder(AccountType.BOT).setToken("BOT_TOKEN").addListener(new AudioCommandHandler()).buildBlocking();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RateLimitedException e) {
			e.printStackTrace();
		}
	}
}
