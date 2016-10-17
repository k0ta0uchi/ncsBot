package me.buddygang.discord.ncsBot.music;

import java.io.IOException;
import java.util.Arrays;

import net.dv8tion.jda.audio.AudioConnection;
import net.dv8tion.jda.audio.AudioSendHandler;
import net.dv8tion.jda.player.AbstractMusicPlayer;

public class MusicPlayer extends AbstractMusicPlayer implements AudioSendHandler {

	public static final int PCM_FRAME_SIZE = 4;
	private byte[] buffer = new byte[AudioConnection.OPUS_FRAME_SIZE * PCM_FRAME_SIZE];

	public boolean canProvide() {
		return state.equals(State.PLAYING);
	}

	public byte[] provide20MsAudio() {
		try {
			int amountRead = currentAudioStream.read(buffer, 0, buffer.length);
			if (amountRead > -1) {
				if (amountRead < buffer.length) {
					Arrays.fill(buffer, amountRead, buffer.length - 1, (byte) 0);
				}
				if (volume != 1) {
					short sample;
					for (int i = 0; i < buffer.length; i += 2) {
						sample = (short) ((buffer[i + 1] & 0xff) | (buffer[i] << 8));
						sample = (short) (sample * volume);
						buffer[i + 1] = (byte) (sample & 0xff);
						buffer[i] = (byte) ((sample >> 8) & 0xff);
					}
				}
				return buffer;
			} else {
				sourceFinished();
				return null;
			}
		} catch (IOException e) {
			LOG.debug("A source closed unexpectantly? Oh well I guess...");
			sourceFinished();
		}
		return null;
	}
}
