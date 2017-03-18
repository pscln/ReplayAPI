package de.sebpas.replay.util;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.sebpas.replay.RePlayer;
import de.sebpas.replay.ReplaySystem;

public class PlayingPlayer implements Listener{
	private double health = 20;
	private int foodLVL = 20;
	private float xp;
	private int lvl;
	private Location loc;
	private ItemStack[] inv;
	
	public PlayingPlayer(Player p){
		this.pause(p);
	}
	public PlayingPlayer(){}
	
	/** saving the player's items */
	
	public void pause(Player p){
		this.health = p.getHealth();
		this.foodLVL = p.getFoodLevel();
		this.xp = p.getExp();
		this.lvl = p.getLevel();
		this.loc = p.getLocation();
		this.inv = p.getInventory().getContents();
		addItems(p);
	}
	/***
	 * param task HAS TO BE true! if false, it wont use a synchronized task crashing the plugin
	 * @param p
	 * @param task
	 */
	public void throwIntoGame(Player p, boolean task){
		p.setHealth(health);
		p.setFoodLevel(foodLVL);
		p.setExp(xp);
		p.setLevel(lvl);
		p.teleport(loc);
		p.getInventory().setContents(inv);
		if(task)
			Bukkit.getScheduler().runTaskLater(ReplaySystem.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					p.setGameMode(GameMode.SURVIVAL);
					p.setFlying(false);
					p.setAllowFlight(false);
				}
			}, 20L);
	}
	private void addItems(Player p){
		p.getInventory().clear();
		p.getInventory().setItem(8, ItemUtilities.createItem(Material.NETHER_STAR, 1, 0, "§3Geschwindigkeit §7" +
                "(1.0x)"));
		p.getInventory().setItem(4, ItemUtilities.createItem(Material.WOOL, 1, (short) 5, "§cPausieren"));
		p.getInventory().setItem(0, ItemUtilities.createItem(Material.COMPASS, 1, 0, "§3Tracker"));
		p.getInventory().setItem(6, ItemUtilities.createItem(1, "MHF_ArrowRight", "§a30 Sekunden §7Vorspulen"));
		p.getInventory().setItem(2, ItemUtilities.createItem(1, "MHF_ArrowLeft", "§c30 Sekunden §7Zurückspulen"));
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemUse(PlayerInteractEvent e){
		if(e.getItem() == null)
			return;
		if(e.getItem().getItemMeta().getDisplayName() == null)
			return;
		Player p = e.getPlayer();
		if(!ReplaySystem.getInstance().isAlreadyInReplay(p))
			return;
		ItemStack item = e.getItem();
		if(item.getType() == Material.COMPASS){
			InventoryUtilities.openTrackerGui(p);
			e.setCancelled(true);
		}
		if(item.getItemMeta().getDisplayName().contains("Vorspulen")){
			RePlayer r = ReplaySystem.getInstance().getPlayersRePlayer(p);
			if(r == null){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getErrorPrefix() + "Du befindest nicht in einem Replay!"));
				return;
			}
			r.setCurrentTick(r.getCurrentTick() + 20 * 30);
		}
		if(item.getItemMeta().getDisplayName().contains("Zurückspulen")){
			RePlayer r = ReplaySystem.getInstance().getPlayersRePlayer(p);
			if(r == null){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getErrorPrefix() + "Du befindest nicht in einem Replay!"));
				return;
			}
			r.setCurrentTick(r.getCurrentTick() - 20 * 30);
		}
		if(item.getItemMeta().getDisplayName().contains("Pausieren")){
			RePlayer r = ReplaySystem.getInstance().getPlayersRePlayer(p);
			if(r == null){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getErrorPrefix() + "Du befindest nicht in einem Replay!"));
				return;
			}
			r.pause();
			p.getInventory().setItem(4, ItemUtilities.createItem(Material.WOOL, 1, (short) 14, "§aFortsetzen"));
		}
		if(item.getItemMeta().getDisplayName().contains("Fortsetzen")){
			RePlayer r = ReplaySystem.getInstance().getPlayersRePlayer(p);
			if(r == null){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getErrorPrefix() + "Du befindest nicht in einem Replay!"));
				return;
			}
			r.continueReplay();
			p.getInventory().setItem(4, ItemUtilities.createItem(Material.WOOL, 1, (short) 5, "§cPausieren"));
		}
		if(item.getItemMeta().getDisplayName().contains("Geschwindigkeit")){
			RePlayer r = ReplaySystem.getInstance().getPlayersRePlayer(p);
			if(r == null){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getErrorPrefix() + "Du befindest nicht in einem Replay!"));
				return;
			}
			double velocity = 1.0D;
			if(item.getItemMeta().getDisplayName().contains("1.0")){
				velocity = 1.5D;
			}
			if(item.getItemMeta().getDisplayName().contains("1.5")){
				velocity = 2.0D;
			}
			if(item.getItemMeta().getDisplayName().contains("2.0")){
				velocity = 4.0D;
			}
			if(item.getItemMeta().getDisplayName().contains("4.0")){
				velocity = 0.5D;
			}
			if(item.getItemMeta().getDisplayName().contains("0.5")){
				velocity = 1.0D;
			}
			r.setVelocity(velocity);
			if(velocity % 1 == 0)
				r.setCurrentTick(Math.floor(r.getCurrentTick()));
			p.getInventory().setItem(8, ItemUtilities.createItem(Material.NETHER_STAR, 1, 0, "§3Geschwindigkeit " +
                    "§7(" + velocity + "x)"));
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInvClick(InventoryClickEvent e){
		if(e.getClickedInventory() == null)
			return;
		if(e.getClickedInventory().getTitle().contains("Tracker")){
			if(e.getCurrentItem() == null)
				return;
			if(e.getCurrentItem().getItemMeta().getDisplayName() == null)
				return;
			String name = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);
			e.setCancelled(true);
			e.getWhoClicked().teleport(ReplaySystem.getInstance().getPlayersRePlayer(e.getWhoClicked()).getNPCByName(name).getLocation());
		}
	}
}
