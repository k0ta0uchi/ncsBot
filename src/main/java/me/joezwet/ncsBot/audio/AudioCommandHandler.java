package me.joezwet.ncsBot.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
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
	private List<AudioTrack> trackList;

	public AudioCommandHandler() {
		this.musicManagers = new HashMap<>();
		this.playerManager = new DefaultAudioPlayerManager();
		playerManager.registerSourceManager(new YoutubeAudioSourceManager());
	}

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
			if (msg.startsWith("ncs help")) {
				help(event.getTextChannel());
			}
			if (msg.startsWith("ncs bot")) {
				bot(event.getTextChannel());
			}
			if (msg.startsWith("ncs play ")) {
				String category = msg.substring("ncs play ".length());
				if(category == null){
					restart(event.getTextChannel());
				} else switch(category) {
					case "all":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2GpgnIh0AiYKh7o7HnYAej-5ph");
						break;
					case "electronic":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2GpgnZOm5rCopMAOYhZCPoUyO5");
						break;
					case "indie-dance":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2GpglkzuspoGv-mu7B2ce9_0Fn");
						break;
					case "hardstyle":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2GpgnXJ2owag81mqSFklL83-d5");
						break;
					case "trap":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2Gpgm0WF6DEGmb7ab4qHAGlPzg");
						break;
					case "drumstep":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2GpglTnOLbhyrHAVaWsCIEX53Y");
						break;
					case "melodic-dubstep":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2Gpgm57nFVNM7qYZ9u64U9Q-Bf");
						break;
					case "dubstep":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2Gpglq-J-Hv0p-y0wk3lQk570u");
						break;
					case "house":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2GpgmsW46rJyudVFlY6IYjFBIK");
						break;
					case "drum&base":
						loadAndPlay(event.getTextChannel(), "PLRBp0Fe2GpgnzYdHtTCoBYPyIJG9_opMn");
						break;
					default:
						break;
				}
			}
			if (msg.startsWith("ncs skip")) {
				skipTrack(event.getTextChannel());
			}
			if (msg.startsWith("ncs nowplaying")) {
				nowPlaying(event.getTextChannel());
			}
			if (msg.startsWith("ncs list")) {
				listTracks(event.getTextChannel());
			}
			if (msg.startsWith("ncs volume ")) {
				changeVolume(event.getTextChannel(),  msg.substring("ncs volume ".length()));
			}
			if (msg.startsWith("ncs restart")) {
				restart(event.getTextChannel());
			}
			if (msg.startsWith("ncs repeat")) {
				repeat(event.getTextChannel());
			}
			if (msg.startsWith("ncs reset")) {
				reset(event.getTextChannel());
			}
			if (msg.startsWith("ncs pause")) {
				pauseTrack(event.getTextChannel());
			}
			if (msg.startsWith("ncs resume")) {
				resumeTrack(event.getTextChannel());
			}
			if (msg.startsWith("ncs stop")) {
				stopTrack(event.getTextChannel());
			}
			if (msg.startsWith("ncs join")) {
				joinVoiceChannel(event.getTextChannel());
			}
			if (msg.startsWith("ncs idJoin ")) {
				joinVoiceChannel(event.getTextChannel(), msg.substring("ncs idJoin ".length()));
			}
			if (msg.startsWith("ncs leave")) {
				leaveVoiceChannel(event.getTextChannel());
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

				musicManager.player.destroy();
				musicManager.scheduler.queue.clear();
				GuildMusicManager _musicManager = getGuildAudioPlayer(channel.getGuild());

				channel.sendMessage("Loaded playlist: " + playlist.getName()).queue();
				channel.sendMessage("Playing: " + firstTrack.getInfo().title).queue();

				playList(channel.getGuild(), _musicManager, playlist, channel, channel.getGuild().getAudioManager());
			}

			public void noMatches() {
				channel.sendMessage("Nothing found by " + trackUrl).queue();
			}

			public void loadFailed(FriendlyException exception) {
				channel.sendMessage("Could not play: " + exception.getMessage()).queue();
			}
		});
	}

	private void help(TextChannel channel) {
		String helpText = "NOTE: All commands are prefixed with ncs. e.g. ncs help\n\n"
						+ "Command - Description\n"
						+ "help - Sends you a direct message containing all commands.\n"
						+ "bot - Basic bot infomation.\n"
						+ "play {type} - Plays music based off of {type}."
						+ "join {channel} - Joins a voice channel."
						+ "leave - Leaves the joined voice channel.\n"
						+ "pause - Pauses audio playback.\n"
						+ "stop - Completely stops audio playback, skipping the current song.\n"
						+ "skip - Skips the current song, automatically starting the next.\n"
						+ "nowplaying - Prints information about the currently playing song.\n"
						+ "list - Lists the songs in the queue.\n"
						+ "volume[0-100] - Sets the volume of the player. 100 being 100%, 0 being 0%\n"
						+ "restart - Restarts the current song or restarts the previous song if there is no current song playing.\n"
						+ "repeat - Makes the player repeat the currently playing song.\n"
						+ "reset - Completely resets the player, fixing all errors and clearing the + queue.\n"
						+ "feedback {message} - Sends feedback to the developers.\n"
						+ "invite - Sends invite link, NOTE: All perms are REQUIRED, if not all given + the bot will not work properly.\n"
						+ "ping - The Bots Ping.\n\n\n"
						+ "NOTE: These types are played with ncs play {type}.\n\n"
						+ "Type - Playlist\n"
						+ "all - NCS: All Releases\n"
						+ "electronic - NCS: Electronic\n"
						+ "indie-dance - NCS: Indie Dance\n"
						+ "hardstyle - NCS: Hardstyle\n"
						+ "trap - NCS: Trap\n"
						+ "drumstep - NCS: Drumstep\n"
						+ "melodic-dubstep - NCS: Melodic Dubstep\n"
						+ "dubstep - NCS: Dubstep\n"
						+ "house - NCS: House\n"
						+ "drum&base - NCS: Drum & Base\n";
						channel.sendMessage(helpText).queue();
	}

	private void bot(TextChannel channel) {
		channel.sendMessage("A DiscordApp bot that plays NCS music in voice channels.").queue();
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

	private void playList(Guild guild, GuildMusicManager musicManager, AudioPlaylist playlist, TextChannel channel,
			AudioManager audioManager) {
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			channel.sendMessage("You need to add me to a voice channel first!").queue();
			return;
		}
		this.trackList = new ArrayList<AudioTrack>();
		if (audioManager.isConnected()) {
			this.trackList = new ArrayList<AudioTrack>();
			for(AudioTrack track: playlist.getTracks()) {
				this.trackList.add(track);
				musicManager.scheduler.queue(track);
			}
		}
	}

	private void skipTrack(TextChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.scheduler.nextTrack();
		AudioTrack track = musicManager.player.getPlayingTrack();
		this.trackList.remove(0);

		channel.sendMessage("Skipped to next track: " + track.getInfo().title).queue();
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

	private void pauseTrack(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			long guildId = Long.parseLong(guild.getId());
			GuildMusicManager musicManager = musicManagers.get(guildId);
			musicManager.player.setPaused(true);
			channel.sendMessage("Paused.").queue();
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void resumeTrack(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			long guildId = Long.parseLong(guild.getId());
			GuildMusicManager musicManager = musicManagers.get(guildId);
			if(musicManager.player.isPaused()) {
				AudioTrack track = musicManager.player.getPlayingTrack();
				channel.sendMessage("Resumed track: " + track.getInfo().title).queue();
				musicManager.player.setPaused(false);
			} else {
				channel.sendMessage("Already playing!").queue();
			}
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void stopTrack(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			long guildId = Long.parseLong(guild.getId());
			GuildMusicManager musicManager = musicManagers.get(guildId);
			musicManager.scheduler.nextTrack();
			musicManager.player.stopTrack();
			channel.sendMessage("Stopped.").queue();
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void nowPlaying(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			long guildId = Long.parseLong(guild.getId());
			GuildMusicManager musicManager = musicManagers.get(guildId);
			AudioTrack track = musicManager.player.getPlayingTrack();
			channel.sendMessage("Now playing: " + track.getInfo().title).queue();
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void listTracks(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			if(this.trackList != null) {
				String list = "";
				long guildId = Long.parseLong(guild.getId());
				GuildMusicManager musicManager = musicManagers.get(guildId);
				String playingTitle = musicManager.player.getPlayingTrack().getInfo().title;
				boolean isLead = false;
				int cutnum = 0;
				for(int i = 0; i < this.trackList.size(); i++) {
					AudioTrack track = this.trackList.get(i);
					String title = track.getInfo().title;
					if(isLead) {
						String _list = list + title + " \n";
						if(_list.length() > 1900) {
							list = list + "...and more";
							break;
						} else {
							list = _list;
						}
					} else if(playingTitle == title) {
						list = list + title + " \n";
						isLead = true;
						cutnum = i - 1;
					}
				}
				for(int i = 0; i < cutnum; i++) {
					this.trackList.remove(0);
				}
				channel.sendMessage(list).queue();
			} else {
				channel.sendMessage("No song in queue.").queue();
			}
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void changeVolume(TextChannel channel, String _volume) {
		int volume = Integer.parseInt(_volume);
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			long guildId = Long.parseLong(guild.getId());
			GuildMusicManager musicManager = musicManagers.get(guildId);
			musicManager.player.setVolume(volume);
			channel.sendMessage("Changed volume to: " + volume).queue();
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void restart(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			long guildId = Long.parseLong(guild.getId());
			GuildMusicManager musicManager = musicManagers.get(guildId);
			AudioTrack track = musicManager.player.getPlayingTrack();
			if(track == null)
				track = musicManager.scheduler.lastTrack;
			musicManager.player.playTrack(track.makeClone());
			channel.sendMessage("Restarted track: " + track.getInfo().title).queue();
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void repeat(TextChannel channel) {
		Guild guild = channel.getGuild();
		if (guild.getAudioManager().isConnected()) {
			long guildId = Long.parseLong(guild.getId());
			GuildMusicManager musicManager = musicManagers.get(guildId);
			musicManager.scheduler.setRepeating(!musicManager.scheduler.isRepeating());
			channel.sendMessage("Player was set to: **" + (musicManager.scheduler.isRepeating() ? "repeat" : "not repeat") + "**").queue();
		} else {
			channel.sendMessage("I am not in a voice channel!").queue();
		}
	}

	private void reset(TextChannel channel) {
		Guild guild = channel.getGuild();
		long guildId = Long.parseLong(guild.getId());
		GuildMusicManager musicManager = musicManagers.get(guildId);
		synchronized (musicManagers)
		{
			musicManager.scheduler.queue.clear();
			musicManager.player.destroy();
			guild.getAudioManager().setSendingHandler(null);
			musicManagers.remove(guildId);
		}

		musicManager = getGuildAudioPlayer(guild);
		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
		channel.sendMessage("The player has been completely reset!").queue();
	}
}
