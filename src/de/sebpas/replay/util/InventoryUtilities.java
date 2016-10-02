package de.sebpas.replay.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.sebpas.replay.ReplaySystem;
import de.sebpas.replay.npc.NPC;

public class InventoryUtilities {
	private InventoryUtilities() {}
	public static void openTrackerGui(Player p){
		int npcs = ReplaySystem.getInstance().getPlayersRePlayer(p).getNpcs().size();
		if(npcs % 9 != 0)
			while(npcs % 9 != 0)
				++ npcs;
		Inventory e = Bukkit.createInventory(p, npcs, "§cTracker");
		int slot = 0;
		for(NPC n : ReplaySystem.getInstance().getPlayersRePlayer(p).getNpcs()){
			e.setItem(slot, ItemUtilities.createItem(1, n.getName(), "§3" + n.getName()));
			++ slot;
		}
		p.openInventory(e);
	}
}
