package de.sebpas.replay.recorder.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.sebpas.replay.ReplaySystem;

public class MoveListener implements Listener{
	private ReplaySystem plugin;
	public MoveListener(ReplaySystem plugin){
		this.plugin = plugin;
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getPlayer().getUniqueId() + ";" + e.getPlayer().getName() + ";moved:" + e.getTo().getX() + "," + e.getTo().getY() + "," + e.getTo().getZ()
				+ "," + e.getTo().getYaw() + "," + e.getTo().getPitch() + ";" + (e.getPlayer().isSneaking() ? "sneak" : (e.getPlayer().isSprinting() ? "sprint" : ""))
						+ ";" + (e.getPlayer().isBlocking() ? "block" : ""));
	}
}
