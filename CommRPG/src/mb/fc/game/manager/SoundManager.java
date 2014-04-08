package mb.fc.game.manager;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;

import org.newdawn.slick.Music;

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
		stateInfo.getResourceManager().getSoundByName(name).play(1f, volume);
	}
	
	public void playMusicByName(String name)
	{
		Music playingMusic = stateInfo.getResourceManager().getMusicByName(name);
		playingMusic.loop(1, 1);
		stateInfo.setPlayingMusic(playingMusic);
	}
	

	public void playMusicByName(String name, float volume)
	{
		System.out.println("Play music by name " + name + " " + volume);
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
		System.out.println("GOT MESSAGE " + message.getMessageType());
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
				System.out.println("Message play music");
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
