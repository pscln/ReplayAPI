package de.sebpas.replay.util;

import de.sebpas.replay.ReplaySystem;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class Reflections {
	
	public void setValue(Object obj, String name, Object value){
		try{
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		}catch(Exception e){
			ReplaySystem.sendBroadcastError(e.getMessage());
			e.printStackTrace();
		}
	}
	public Object getValue(Object obj, String name){
		try{
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		}catch(Exception e){
			ReplaySystem.sendBroadcastError(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public void sendPacket(Packet<?> packet, Player player){
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	public void sendPacket(Packet<?> packet){
		for(Player p : Bukkit.getOnlinePlayers()){
			this.sendPacket(packet, p);
		}
	}
}
