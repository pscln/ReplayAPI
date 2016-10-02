package de.sebpas.replay;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.sebpas.replay.command.CommandReplay;
import de.sebpas.replay.filesystem.FileManager;
import de.sebpas.replay.util.PlayingPlayer;
import de.sebpas.replay.util.ReplayStoppedEvent;

public class ReplaySystem extends JavaPlugin{
	private int ranTicks = 0;
	private FileManager fileSystem;
	private Recorder recorder;
	
	private static ReplaySystem instance = null;
	
	/** list of all replayers */
	private List<RePlayer> replayers = new ArrayList<RePlayer>();
	
	/** plugin prefixes */
	private static String prefix = "&8[&3Replay&8]: &r";
	private static String error = "&8[&cReplay&8]: &c";
	
	
	/**
	 * Bukkit methods
	 */
	
	@Override
	public void onEnable() {
		System.out.println("[Replay]: Enabled!");
		this.fileSystem = new FileManager();
		this.recorder = new Recorder(this);
		
		this.getCommand("rplstart").setExecutor(new CommandReplay(this));
		this.getCommand("rplstop").setExecutor(new CommandReplay(this));
		this.getCommand("replay").setExecutor(new CommandReplay(this));

		this.getServer().getPluginManager().registerEvents(new PlayingPlayer(), this);

		instance = this;
	}
	@Override
	public void onDisable() {
		if(recorder.isRecording()){
			this.stop();
		}
		for(RePlayer r : replayers)
			r.stopWithoutTask();
	}
	
	/** returns a new instance of this main class */
	public static ReplaySystem getInstance(){
		return instance;
	}
	
	public void start(){
		this.ranTicks = 0;
		this.recorder.recorde();
		this.fileSystem.reset();
	}
	
	public void stop(){
		this.ranTicks = 0;
		this.recorder.stop();
		this.fileSystem.save();
	}
	
	/** returns the amount of recorded ticks */
	public int getHandledTicks(){
		return this.ranTicks;
	}
	
	/** sends a message to all players and the console */
	public static void sendBroadcast(String msg){
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
	}
	
	/** sends an error message to all players and the console */
	public static void sendBroadcastError(String msg){
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', error + msg));
	}
	public String getPrefix(){
		return prefix;
	}
	public String getErrorPrefix(){
		return error;
	}
	
	/** the tick counter */
	public void addTick(){
		++ ranTicks;
	}
	
	/** returns the filemanager */
	public FileManager getFileManager(){
		return fileSystem;
	}
	
	/** returns the recording thread */
	public Recorder getRecorder(){
		return this.recorder;
	}
	/** returns true if the player is already watching a replay */
	public boolean isAlreadyInReplay(Player p){
		for(RePlayer r : replayers)
			if(r.getPlayers().containsKey(p))
				return true;
		return false;
	}
	
	public void addPlayer(RePlayer p){
		this.replayers.add(p);
	}
	
	public void onPlayerStopped(RePlayer p){
		this.replayers.remove(p);
		synchronized (p) {
			this.getServer().getPluginManager().callEvent(new ReplayStoppedEvent(p));
		}
	}
	public RePlayer getPlayersRePlayer(Player p){
		for(RePlayer r : replayers){
			if(r.getPlayers().containsKey(p))
				return r;
		}
		return null;
	}
	public RePlayer getPlayersRePlayer(HumanEntity p){
		for(RePlayer r : replayers){
			for(Player t : r.getPlayers().keySet())
				if(t.getName().equals(p.getName()))
					return r;
		}
		return null;
	}
}
