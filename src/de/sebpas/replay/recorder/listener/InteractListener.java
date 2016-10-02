package de.sebpas.replay.recorder.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.sebpas.replay.ReplaySystem;

public class InteractListener implements Listener{
	private ReplaySystem plugin;
	public InteractListener(ReplaySystem plugin){
		this.plugin = plugin;
	}
	@EventHandler
	public void onAttack(PlayerInteractEvent e){
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
			plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getPlayer().getUniqueId() + ";" + e.getPlayer().getName() + ";swing");
	}
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(e.getEntityType() != EntityType.PLAYER)
			return;
		Player p = (Player) e.getEntity();
		if(ReplaySystem.getInstance().isAlreadyInReplay(p)){
			e.setCancelled(true);
			return;
		}
		plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getEntity().getUniqueId() + ";" + e.getEntity().getName() + ";dmg");
	}
}
