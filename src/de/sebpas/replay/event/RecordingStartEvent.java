package de.sebpas.replay.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RecordingStartEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	public RecordingStartEvent(){}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList(){
		return handlers;
	}
}
