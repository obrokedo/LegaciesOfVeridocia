package mb.fc.game.manager;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;

import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

public class SoundManager extends Manager
{
	private Music playingMusic;
	private float playingVolume;
	private String playingMusicName;
	private float playingMusicPosition;
	private float musicPosition = 0f;

	public void update(int delta)
	{
		/*
		if (playingMusic != null && playingMusic.playing())
		{
			musicPosition = musicPosition + delta / 1000.0f;
		}
		*/
	}

	@Override
	public void initialize() {

	}

	public void playSoundByName(String name, float volume)
	{
		if (name == null)
			return;
		Sound sound = stateInfo.getResourceManager().getSoundByName(name);
		sound.play(1f, volume);
	}

	public void playMusicByName(String name, float volume, float position)
	{
		Music playingMusic = stateInfo.getResourceManager().getMusicByName(name);
		musicPosition = 0;
		playingMusic.stop();
		playingMusic.setPosition(position);
		playingMusic.loop(1, 0);
		playingMusic.fade(2000, volume, false);
		this.playingMusicName = name;
		this.playingVolume = volume;
		this.playingMusic = playingMusic;
	}

	public void pauseMusic()
	{
		if (playingMusic != null)
		{
			System.out.println(playingMusic.getPosition());
			this.playingMusicPosition = this.playingMusic.getPosition();
			playingMusic.stop();
		}
	}

	public void resumeMusic()
	{
		if (playingMusic != null)
		{
			playMusicByName(playingMusicName, playingVolume, playingMusicPosition);
		}
	}

	public void stopMusic()
	{
		if (playingMusic != null)
		{
			playingMusic.stop();
			playingMusic = null;
		}
	}

	public void fadeMusic(int duration)
	{
		if (playingMusic != null)
		{
			playingMusic.fade(duration, 0f, true);
		}
	}

	@Override
	public void recieveMessage(Message message)
	{
		switch (message.getMessageType())
		{
			case Message.MESSAGE_SOUND_EFFECT:
				AudioMessage am = (AudioMessage) message;
				playSoundByName(am.getAudio(), am.getVolume());
				break;
			case Message.MESSAGE_PAUSE_MUSIC:
				pauseMusic();
				break;
			case Message.MESSAGE_RESUME_MUSIC:
				resumeMusic();
				break;
			case Message.MESSAGE_PLAY_MUSIC:
				am = (AudioMessage) message;
				playMusicByName(am.getAudio(), am.getVolume(), am.getPosition());
				break;
			case Message.MESSAGE_FADE_MUSIC:
				IntMessage im = (IntMessage) message;
				fadeMusic(im.getValue());
				break;
		}
	}
}
