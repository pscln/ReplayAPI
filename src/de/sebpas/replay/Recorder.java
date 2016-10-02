package de.sebpas.replay;

import org.bukkit.Bukkit;

import de.sebpas.replay.recorder.listener.InteractListener;
import de.sebpas.replay.recorder.listener.MoveListener;

public class Recorder{
	private ReplaySystem plugin;
	private boolean isRecording = false;
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			if(Bukkit.getOnlinePlayers().size() != 0)
				plugin.addTick();
		}
	};
	
	public Recorder(ReplaySystem plugin){
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(new MoveListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new InteractListener(plugin), plugin);
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 1L, 1L);
	}
	public void recorde(){
		this.isRecording = true;
	}
	public void stop(){
		this.isRecording = false;
	}
	
	public boolean isRecording(){
		return this.isRecording;
	}
	public void addString(String s){
		this.plugin.getFileManager().appendString(s);
	}
}
