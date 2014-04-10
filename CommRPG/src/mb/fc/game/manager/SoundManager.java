package mb.fc.game.manager;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;

import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

public class SoundManager extends Manager
{
	public void update(int delta)
	{
		
	}

	@Override
	public void initialize() {
		
	}
	
	public void playSoundByName(String name, float volume)
	{		
		Sound sound = stateInfo.getResourceManager().getSoundByName(name); 
		sound.play(1f, volume);
	}
	
	public void playMusicByName(String name)
	{
		Music playingMusic = stateInfo.getResourceManager().getMusicByName(name);
		playingMusic.loop(1, 0);
		stateInfo.setPlayingMusic(playingMusic);
	}
	

	public void playMusicByName(String name, float volume)
	{
		Music playingMusic = stateInfo.getResourceManager().getMusicByName(name);
		playingMusic.loop(1, volume);
		stateInfo.setPlayingMusic(playingMusic);
	}
	
	public void pauseMusic()
	{
		if (stateInfo.getPlayingMusic() != null)
			stateInfo.getPlayingMusic().pause();
	}
	
	public void resumeMusic()
	{
		if (stateInfo.getPlayingMusic() != null)
			stateInfo.getPlayingMusic().resume();
	}
	
	public void stopMusic()
	{
		if (stateInfo.getPlayingMusic() != null)
		{
			stateInfo.getPlayingMusic().stop();
			stateInfo.setPlayingMusic(null);
		}
	}
	
	public void fadeMusic(int duration)
	{
		if (stateInfo.getPlayingMusic() != null)
			stateInfo.getPlayingMusic().fade(duration, 0f, true);
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
				playMusicByName(am.getAudio(), am.getVolume());
				break;
			case Message.MESSAGE_FADE_MUSIC:
				IntMessage im = (IntMessage) message;
				fadeMusic(im.getValue());
				break;
		}
	}
}
