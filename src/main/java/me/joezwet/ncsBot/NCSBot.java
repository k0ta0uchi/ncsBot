package me.joezwet.ncsBot;

import javax.security.auth.login.LoginException;

import me.joezwet.ncsBot.audio.AudioCommandHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class NCSBot {
	
	public static JDA api;

	public static void main(String[] args) {
		try {
			api = new JDABuilder(AccountType.BOT).setToken(NCSBot.getToken()).addEventListener(new AudioCommandHandler()).buildBlocking();
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

	public static String getToken() {
		String path = ".." + File.separator + "config" + File.separator + "bot.json";
		System.out.println(path);
		try (Stream<String> stream = Files.lines(Paths.get(path)); ) {
			StringBuilder sb = new StringBuilder();
			stream.forEach(sb::append);
			JsonObject jsonObj = (JsonObject) new Gson().fromJson(sb.toString(), JsonObject.class);
			
			return jsonObj.get("botToken").getAsString();
		} catch (IOException e) {
			System.out.println(e);
		}
		return null;
  	}
}
