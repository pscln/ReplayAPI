package de.sebpas.replay.util;

import de.sebpas.replay.ReplaySystem;
import de.sebpas.replay.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryUtilities {
	private static Map<Player, ItemStack[]> armors = new HashMap<Player, ItemStack[]>();
	
	private InventoryUtilities() {}
	/** opens the teleporter gui which allows player p to teleport to npcs */
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
	/** saves player p's armor contents */
	public static void saveArmor(Player p, ReplaySystem plugin){
		boolean changed = false;
		if(armors.containsKey(p)){
			ItemStack[] old = armors.get(p);
			ItemStack[] now = p.getInventory().getArmorContents();
			for(int i = 0; i < old.length; i++){
				if(old[i] != now[i]){
					old[i] = now[i];
					changed = true;
				}
			}
		}else{
			armors.put(p, p.getInventory().getArmorContents());
			changed = true;
		}
		if(changed){
			plugin.getRecorder().addString(plugin.getHandledTicks() + ";" + p.getUniqueId() + ";" + p.getName() + ";armr:" + armorToString(p.getInventory().getArmorContents()));
		}
	}
	private static String armorToString(ItemStack[] items){
		String rtn = "";
		for(int i = 0; i < items.length; i++){
			rtn += (rtn != "" ?  "," : "") + items[i].getType();
		}
		return rtn;
	}
}
