package de.sebpas.replay.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtilities {
	private ItemUtilities() {}
	
	public static ItemStack createItem(Material mat, int amount, int shortid, String displayname){
		short s = (short) shortid;
		ItemStack i = new ItemStack(mat, amount, s);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(displayname);
		i.setItemMeta(meta);
		return i;
	}
	/**
	 * You need a player head?
	 * @param mat
	 * @param amount
	 * @param shortid
	 * @param name
	 * @param displayname
	 * @return skull of a specified player
	 */
	public static ItemStack createItem(int amount, String name, String displayname){
		ItemStack item = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(displayname);
		meta.setOwner(name);
		item.setItemMeta(meta);
		return item;
	}
}
