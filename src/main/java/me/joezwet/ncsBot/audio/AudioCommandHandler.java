package me.joezwet.ncsBot.audio;

import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;

public class AudioCommandHandler extends ListenerAdapter {

	private AudioPlayerManager playerManager;
	private Map<Long, GuildMusicManager> musicManagers;

	private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		long guildId = Long.parseLong(guild.getId());
		GuildMusicManager musicManager = musicManagers.get(guildId);

		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager);
			musicManagers.put(guildId, musicManager);
		}

		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

		return musicManager;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String msg = event.getMessage().getContent();
		Guild guild = event.getGuild();
		if (guild != null) {
			if (!guild.getId().equals("225504501287747584")) {
				if(msg.startsWith("ncs")) {
					event.getChannel().sendMessage("All Commands are disabled for testing!").queue();
				}
			} else {
				if (msg.startsWith("ncs play ")) {
					loadAndPlay(event.getTextChannel(), msg.substring("ncs play ".length()));
				}

				if (msg.startsWith("ncs join ")) {
					joinVoiceChannel(event.getTextChannel());
				}
				if (msg.startsWith("ncs idJoin ")) {
					joinVoiceChannel(event.getTextChannel(), msg.substring("ncs idJoin ".length()));
				}
			}
		}

		super.onMessageReceived(event);
	}

	private void loadAndPlay(final TextChannel channel, final String trackUrl) {
		final GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			public void trackLoaded(AudioTrack track) {
				channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

				play(channel.getGuild(), musicManager, track, channel, channel.getGuild().getAudioManager());
			}

			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();

				if (firstTrack == null) {
					firstTrack = playlist.getTracks().get(0);
				}

				channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist "
						+ playlist.getName() + ")").queue();

				play(channel.getGuild(), musicManager, firstTrack, channel, channel.getGuild().getAudioManager());
			}

			public void noMatches() {
				channel.sendMessage("Nothing found by " + trackUrl).queue();
			}

			public void loadFailed(FriendlyException exception) {
				channel.sendMessage("Could not play: " + exception.getMessage()).queue();
			}
		});
	}

	private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, TextChannel channel,
			AudioManager audioManager) {
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			channel.sendMessage("You need to add me to a voice channel first!").queue();
			return;
		}
		if (audioManager.isConnected()) {
			musicManager.scheduler.queue(track);
		}
	}

	private void skipTrack(TextChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.scheduler.nextTrack();

		channel.sendMessage("Skipped to next track.").queue();
	}

	private void joinVoiceChannel(TextChannel channel) {
		Guild guild = channel.getGuild();
		AudioManager am = guild.getAudioManager();
		try {
			am.openAudioConnection(guild.getVoiceChannelsByName("Music", true).get(0));
		} catch(Exception e) {
			channel.sendMessage("You need to create a voice channel named `Music`!").queue();
		}
			
	}
	private void joinVoiceChannel(TextChannel channel, String id) {
		Guild guild = channel.getGuild();
		AudioManager am = guild.getAudioManager();
		try {
			am.openAudioConnection(guild.getVoiceChannelById(id));
		} catch(Exception e) {
			channel.sendMessage("ID not found!").queue();
		}
			
	}

	private void leaveVoiceChannel(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			channel.sendMessage("Leaving Voice Channel!").queue();
			guild.getAudioManager().closeAudioConnection();
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

}
