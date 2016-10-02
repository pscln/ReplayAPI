package de.sebpas.replay.util;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.sebpas.replay.RePlayer;

public class ReplayStoppedEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private RePlayer replayer;
	
	public ReplayStoppedEvent(RePlayer replayer){
		this.replayer = replayer;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList(){
		return handlers;
	}
	public RePlayer getRePlayer(){
		return replayer;
	}
	
}
