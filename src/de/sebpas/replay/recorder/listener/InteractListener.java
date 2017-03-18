package de.sebpas.replay.recorder.listener;

import de.sebpas.replay.ReplaySystem;
import de.sebpas.replay.util.InventoryUtilities;
import net.minecraft.server.v1_8_R3.ItemArmor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class InteractListener implements Listener{
	private ReplaySystem plugin;
	
	public InteractListener(ReplaySystem plugin){
		this.plugin = plugin;
	}
	@EventHandler
	public void onAttack(PlayerInteractEvent e){
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
			plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getPlayer().getUniqueId() + ";" + e.getPlayer().getName() + ";swing");
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getItem() != null){
				if(CraftItemStack.asNMSCopy(e.getItem()).getItem() instanceof ItemArmor){
					InventoryUtilities.saveArmor(e.getPlayer(), plugin);
				}
			}
		}
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
	@EventHandler
	public void onItemInHandChanged(PlayerItemHeldEvent e){
		plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getPlayer().getUniqueId() + ";" + e.getPlayer().getName() + ";itmhnd:" + e.getPlayer().getItemInHand().getType());
	}
	@EventHandler
	public void onArmorChanged(InventoryClickEvent e){
		if(e.getWhoClicked() instanceof Player){
			InventoryUtilities.saveArmor((Player) e.getWhoClicked(), plugin);
		}
	}
	@EventHandler
	public void onChatMsg(AsyncPlayerChatEvent e){
		plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + e.getPlayer().getUniqueId() + ";" + e
                .getPlayer().getName() + ";cht;" + e.getMessage().replace('§', '&'));
	}
}
