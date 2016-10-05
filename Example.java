package examples;

import org.bukkit.entity.Player;

import de.sebpas.replay.RePlayer;
import de.sebpas.replay.ReplaySystem;

public class Example {
	/** start recording */
	public void startRecording(){
		ReplaySystem.getInstance().start();
	}
	/**
	 * stop recording: 
	 */
	public void stopRecording(){
		ReplaySystem.getInstance().stop();
	}
	/**
	 * replay file file to player p and stop it 
	 * @param file
	 * @param p
	 */
	public void replayFile(String file, Player p){
		RePlayer replayer = new RePlayer(file, p);
		replayer.start();
		
		//velocity
		
		replayer.setVelocity(0.25D);
		
		// pause and continue it
		
		replayer.pause();
		
		replayer.continueReplay();
		
		// jump to 60 seconds (20 * 60 ticks)
		
		replayer.setCurrentTick(20 * 60);
		
		// stop
		
		replayer.stop();
	}
	/**
	 * Events: de.sebpas.event (package)
	 * 
	 * - RecordingStartEvent
	 * - RecordingStoppedEvent
	 * - ReplayStartEvent
	 * - ReplayStoppedEvent
	 * http://www.youtube.com/c/thepnlpchannel may be helpful too! (Video called 'ReplayAPI - Tutorial' (in german)
	 */
}
