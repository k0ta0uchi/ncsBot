package me.buddygang.discord.ncsBot.music;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;

public class MusicCommands extends ListenerAdapter {
	public static final float DEFAULT_VOLUME = 0.045f;

	// Current commands
	// join [name] - Joins a voice channel that has the provided name
	// leave - Leaves the voice channel that the bot is currently in.
	// play - Plays songs from the current queue. Starts playing again if it was
	// previously paused
	// play [url] - Adds a new song to the queue and starts playing if it wasn't
	// playing already
	// pause - Pauses audio playback
	// stop - Completely stops audio playback, skipping the current song.
	// skip - Skips the current song, automatically starting the next
	// nowplaying - Prints information about the currently playing song (title,
	// current time)
	// list - Lists the songs in the queue
	// volume [val] - Sets the volume of the MusicPlayer [0.0 - 1.0]
	// restart - Restarts the current song or restarts the previous song if
	// there is no current song playing.
	// repeat - Makes the player repeat the currently playing song
	// reset - Completely resets the player, fixing all errors and clearing the
	// queue.
	@SuppressWarnings("rawtypes")
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		String message = event.getMessage().getContent();
		AudioManager manager = event.getGuild().getAudioManager();
		MusicPlayer player;
		if (manager.getSendingHandler() == null) {
			player = new MusicPlayer();
			player.setVolume(DEFAULT_VOLUME);
			manager.setSendingHandler(player);
		} else {
			player = (MusicPlayer) manager.getSendingHandler();
		}

		if (message.startsWith("ncs volume ")) {
			float volume = Float.parseFloat(message.substring("ncs volume ".length()));
			volume = Math.min(1F, Math.max(0F, volume));
			player.setVolume(volume);
			event.getChannel().sendMessage("Volume was changed to: " + volume);
		}

		if (message.equalsIgnoreCase("ncs list") || message.equalsIgnoreCase("ncs queue")) {
			List<AudioSource> queue = player.getAudioQueue();
			if (queue.isEmpty()) {
				event.getChannel().sendMessage("The queue is currently empty!");
				return;
			}

			MessageBuilder builder = new MessageBuilder();
			builder.appendString("__Current Queue.  Entries: " + queue.size() + "__\n```");
			for (int i = 0; i < queue.size() && i < 10; i++) {
				AudioInfo info = queue.get(i).getInfo();
				if (info == null)
					builder.appendString("*Could not get info for this song.*");
				else {
					AudioTimestamp duration = info.getDuration();
					builder.appendString("[");
					if (duration == null)
						builder.appendString("N/A");
					else
						builder.appendString(duration.getTimestamp());
					builder.appendString("] " + info.getTitle() + "\n");
				}
			}

			boolean error = false;
			int totalSeconds = 0;
			for (AudioSource source : queue) {
				AudioInfo info = source.getInfo();
				if (info == null || info.getDuration() == null) {
					error = true;
					continue;
				}
				totalSeconds += info.getDuration().getTotalSeconds();
			}

			builder.appendString(
					"```\nTotal Queue Time Length: " + AudioTimestamp.fromSeconds(totalSeconds).getTimestamp());
			if (error)
				builder.appendString("An error occured calculating total time. Might not be completely valid.");
			event.getChannel().sendMessage(builder.build());
		}
		if (message.equalsIgnoreCase("ncs nowplaying")) {
			if (player.isPlaying()) {
				AudioTimestamp currentTime = player.getCurrentTimestamp();
				AudioInfo info = player.getCurrentAudioSource().getInfo();
				if (info.getError() == null) {
					event.getChannel().sendMessage("**Playing:** " + info.getTitle() + "\n" + "**Time:**    ["
							+ currentTime.getTimestamp() + " / " + info.getDuration().getTimestamp() + "]");
					event.getChannel().sendMessage("```\n" + info.getDescription() + "\n```");
				} else {
					event.getChannel()
							.sendMessage("**Playing:** Info Error. Known source: "
									+ player.getCurrentAudioSource().getSource() + "\n" + "**Time:**    ["
									+ currentTime.getTimestamp() + " / (N/A)]");
					event.getChannel().sendMessage("```\n" + info.getDescription() + "\n```");
				}
			} else {
				event.getChannel().sendMessage("The player is not currently playing anything!");
			}
		}

		// Start an audio connection with a VoiceChannel
		if (message.startsWith("ncs join ")) {
			// Separates the name of the channel so that we can search for it
			String chanName = message.substring(9);

			// Scans through the VoiceChannels in this Guild, looking for one
			// with a case-insensitive matching name.
			VoiceChannel channel = event.getGuild().getVoiceChannels().stream()
					.filter(vChan -> vChan.getName().equalsIgnoreCase(chanName)).findFirst().orElse(null); // If
																											// there
																											// isn't
																											// a
																											// matching
																											// name,
																											// return
																											// null.
			if (channel == null) {
				event.getChannel()
						.sendMessage("There isn't a VoiceChannel in this Guild with the name: '" + chanName + "'");
				return;
			}
			manager.openAudioConnection(channel);
		}
		// Disconnect the audio connection with the VoiceChannel.
		if (message.equalsIgnoreCase("ncs leave"))
			manager.closeAudioConnection();

		if (message.equalsIgnoreCase("ncs skip")) {
			player.skipToNext();
			event.getChannel().sendMessage("Skipped the current song.");
		}

		if (message.equalsIgnoreCase("ncs repeat")) {
			if (player.isRepeat()) {
				player.setRepeat(false);
				event.getChannel().sendMessage("The player has been set to **not** repeat.");
			} else {
				player.setRepeat(true);
				event.getChannel().sendMessage("The player been set to repeat.");
			}
		}

		if (message.equalsIgnoreCase("ncs shuffle")) {
			if (player.isShuffle()) {
				player.setShuffle(false);
				event.getChannel().sendMessage("The player has been set to **not** shuffle.");
			} else {
				player.setShuffle(true);
				event.getChannel().sendMessage("The player been set to shuffle.");
			}
		}

		if (message.equalsIgnoreCase("ncs reset")) {
			if (event.getAuthor().getId().equals("97172171259904000")) {
				player.stop();
				player = new MusicPlayer();
				player.setVolume(DEFAULT_VOLUME);
				manager.setSendingHandler(player);
				event.getChannel().sendMessage("Music player has been completely reset.");
			} else {
				event.getChannel().sendMessage("Only my developer can do that");
			}
		}

		// Start playing audio with our FilePlayer. If we haven't created and
		// registered a FilePlayer yet, do that.
		if (message.startsWith("ncs play")) {
			// If no URL was provided.
			if (message.equalsIgnoreCase("ncs play")) {
				if (player.isPlaying()) {
					event.getChannel().sendMessage("Player is already playing!");
					return;
				} else if (player.isPaused()) {
					player.play();
					event.getChannel().sendMessage("Playback as been resumed.");
				} else {
					if (player.getAudioQueue().isEmpty())
						event.getChannel()
								.sendMessage("The current audio queue is empty! Add something to the queue first!");
					else {
						player.play();
						event.getChannel().sendMessage("Player has started playing!");
					}
				}
			} else if (message.startsWith("ncs play ")) {
				System.out.printf("Music now playing in: %s\n", event.getGuild().getName());
				String msg = "";
				TextChannel chan = event.getChannel();
				String url = "";
				String type = message.substring("ncs play ".length());
				if (type.equalsIgnoreCase("all")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2GpgnIh0AiYKh7o7HnYAej-5ph";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("electronic")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2GpgnZOm5rCopMAOYhZCPoUyO5";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("indie dance")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2GpglkzuspoGv-mu7B2ce9_0Fn";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("hardstyle")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2GpgnXJ2owag81mqSFklL83-d5";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("trap")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2Gpgm0WF6DEGmb7ab4qHAGlPzg";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("drumstep")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2GpglTnOLbhyrHAVaWsCIEX53Y";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("melodic dubstep")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2Gpgm57nFVNM7qYZ9u64U9Q-Bf";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("dubstep")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2Gpglq-J-Hv0p-y0wk3lQk570u";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("house")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2GpgmsW46rJyudVFlY6IYjFBIK";
					chan.sendMessage("Gathering songs...");
				} else if (type.equalsIgnoreCase("drum&base")) {
					url = "https://www.youtube.com/playlist?list=PLRBp0Fe2GpgnzYdHtTCoBYPyIJG9_opMn";
					chan.sendMessage("Gathering songs...");
				} else {
					event.getChannel().sendMessage(
							"Invalid playlist type, all types can be found at https://github.com/JoeZwet/ncsBot/#music-types");
					return;
				}

				Playlist playlist = Playlist.getPlaylist(url);
				@SuppressWarnings("unchecked")
				List<AudioSource> sources = new LinkedList(playlist.getSources());
				if (sources.size() > 1) {
					event.getChannel().sendMessage("Found a playlist with **" + sources.size() + "** entries.\n"
							+ "Proceeding to gather information and queue sources. This may take some time...");
					final MusicPlayer fPlayer = player;
					Thread thread = new Thread() {
						@Override
						public void run() {
							for (Iterator<AudioSource> it = sources.iterator(); it.hasNext();) {
								AudioSource source = it.next();
								AudioInfo info = source.getInfo();
								List<AudioSource> queue = fPlayer.getAudioQueue();
								if (info.getError() == null) {
									queue.add(source);
									if (fPlayer.isStopped())
										fPlayer.play();
								} else {
									event.getChannel().sendMessage(
											"Error detected, skipping source. Error:\n```" + info.getError() + "```");
									it.remove();
								}
							}
							event.getChannel().sendMessage("Finished queuing provided playlist. Successfully queued **"
									+ sources.size() + "** sources");
						}
					};
					thread.start();
				} else {
					AudioSource source = sources.get(0);
					AudioInfo info = source.getInfo();
					if (info.getError() == null) {
						player.getAudioQueue().add(source);
						msg += "The provided URL has been added the to queue";
						if (player.isStopped()) {
							player.play();
							msg += " and the player has started playing";
						}
						event.getChannel().sendMessage(msg + ".");
					} else {
						event.getChannel().sendMessage("There was an error while loading the provided URL.\n"
								+ "Error: ```" + info.getError() + "```");
					}
				}
			}
		}
		if (message.equalsIgnoreCase("ncs pause")) {
			player.pause();
			event.getChannel().sendMessage("Playback has been paused.");
		}
		if (message.equalsIgnoreCase("ncs stop")) {
			player.stop();
			event.getChannel().sendMessage("Playback has been completely stopped.");
		}
		if (message.equalsIgnoreCase("ncs restart")) {
			if (player.isStopped()) {
				if (player.getPreviousAudioSource() != null) {
					player.reload(true);
					event.getChannel().sendMessage("The previous song has been restarted.");
				} else {
					event.getChannel().sendMessage("The player has never played a song, so it cannot restart a song.");
				}
			} else {
				player.reload(true);
				event.getChannel().sendMessage("The currently playing song has been restarted!");
			}
		}
		if (message.equalsIgnoreCase("ncs audioConnections")) {
			int connections = 0;
			for (int i = 0; i < event.getJDA().getGuilds().size(); i++) {
				event.getJDA().getAudioManager(event.getJDA().getGuilds().get(i)).isConnected();
				if (event.getJDA().getAudioManager(event.getJDA().getGuilds().get(i)).isConnected()) {
					connections++;
				}
			}
			event.getChannel().sendMessage("[**Audio Connections**]\n" + "```xl\n" + connections + "\n" + "```");
		}
	}
}
