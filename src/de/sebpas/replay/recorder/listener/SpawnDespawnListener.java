package de.sebpas.replay.recorder.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.sebpas.replay.ReplaySystem;

public class SpawnDespawnListener implements Listener{
	private ReplaySystem plugin;
	
	public SpawnDespawnListener(ReplaySystem plugin){
		this.plugin = plugin;
	}
	
	/**
	 * Join and disconnect events
	 * @param e
	 */
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoinEvent(PlayerJoinEvent e){
		this.plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getPlayer().getUniqueId() + ";" + e
                .getPlayer().getName() + ";lggdin;" + e.getJoinMessage().replace('§', '&') + ";"
				+ e.getPlayer().getLocation().getX() + "," + e.getPlayer().getLocation().getY() + "," + e.getPlayer().getLocation().getZ()
				+ "," + e.getPlayer().getLocation().getYaw() + "," + e.getPlayer().getLocation().getPitch());
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLeaveEvent(PlayerQuitEvent e){
		this.plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getPlayer().getUniqueId() + ";" + e
                .getPlayer().getName() + ";lggdout;" + e.getQuitMessage().replace('§', '&'));
	}
	
	/**
	 * death and respawn events
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDieEvent(PlayerDeathEvent e){
		if(e.getEntityType() == EntityType.PLAYER){
			Player p = (Player) e.getEntity();
			this.plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + p.getUniqueId() + ";" + p.getName()
                    + ";died;" + e.getDeathMessage().replace('§', '&'));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onRespawnEvent(PlayerRespawnEvent e){
		Player p = (Player) e.getPlayer();
		this.plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + p.getUniqueId() + ";" + p.getName() + ";rspn;"
				+ e.getRespawnLocation().getX() + "," + e.getRespawnLocation().getY() + "," + e.getRespawnLocation().getZ()
				+ "," + e.getRespawnLocation().getYaw() + "," + e.getRespawnLocation().getPitch());
	}
}
