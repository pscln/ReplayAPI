package de.sebpas.replay;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.sebpas.replay.event.RecordingStartEvent;
import de.sebpas.replay.event.RecordingStoppedEvent;
import de.sebpas.replay.recorder.listener.InteractListener;
import de.sebpas.replay.recorder.listener.MoveListener;
import de.sebpas.replay.recorder.listener.SpawnDespawnListener;
import de.sebpas.replay.util.InventoryUtilities;

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
		Bukkit.getPluginManager().registerEvents(new SpawnDespawnListener(plugin), plugin);
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 1L, 1L);
	}
	/** starts capturing */
	public void recorde(){
		Bukkit.getPluginManager().callEvent(new RecordingStartEvent());
		this.isRecording = true;
		for(Player p : Bukkit.getOnlinePlayers()){
			addString(plugin.getHandledTicks() + ";" + p.getUniqueId() + ";" + p.getName() + ";moved:" + p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ()+ "," + p.getLocation().getYaw() + "," + p.getLocation().getPitch() + ";"	+ ";");
			InventoryUtilities.saveArmor(p, plugin);
			this.addString(plugin.getHandledTicks() + ";" + p.getUniqueId() + ";" + p.getName() + ";itmhnd:" + p.getItemInHand().getType());
		}
	}
	/** stops capturing */
	public void stop(){
		this.addString(plugin.getHandledTicks() + "");
		Bukkit.getPluginManager().callEvent(new RecordingStoppedEvent());
		this.isRecording = false;
	}
	
	public boolean isRecording(){
		return this.isRecording;
	}
	/** adds a string to the file the replay will be saved in */
	public void addString(String s){
		if(isRecording())
			this.plugin.getFileManager().appendString(s);
	}
}
