package de.sebpas.replay.npc;

import com.mojang.authlib.GameProfile;
import de.sebpas.replay.util.Reflections;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NPC extends Reflections{
	private int ID;
	private Location location;
	private GameProfile profile;
	private String name;
	private Player player;
	
	/** movement */
	private boolean isSneaking = false;
	private boolean isSprinting = false;
	private boolean isBlocking = false;
	
	/** items (armor, item in hand etc) */
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggins;
	private ItemStack boots;
	private ItemStack handHeld;
	
	/**
	 * @param name
	 * @param loc
	 */
	public NPC(String uuid, String name, Location loc){
		this.ID = (int) Math.ceil(Math.random() * 1000) + 2000;
		profile = new GameProfile(UUID.fromString(uuid), name);
		this.location = loc;
		this.name = name;
	}
	public NPC(String uuid, String name, Location loc, Player player){
		this.ID = (int) Math.ceil(Math.random() * 1000) + 2000;
		profile = new GameProfile(UUID.fromString(uuid), name);
		this.location = loc;
		this.player = player;
		this.name = name;
	}
	/** spawns this npc */
	public void spawn(){
		this.spawn(location);
	}
	/** spawns this npc at location location */
	public void spawn(Location location){
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
		setValue(packet, "a", ID);
		setValue(packet, "b", profile.getId());
		setValue(packet, "c", (int) MathHelper.floor(location.getX() * 32.0D));
		setValue(packet, "d", (int) MathHelper.floor(location.getY() * 32.0D));
		setValue(packet, "e", (int) MathHelper.floor(location.getZ() * 32.0D));
		
		setValue(packet, "f", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
		setValue(packet, "g", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
		
		setValue(packet, "h", 0);
		
		DataWatcher w = new DataWatcher(null);
		w.a(10, (byte) 127);
		w.a(6, (float) 20);
		setValue(packet, "i", w);
		this.sendTablistPacket();
		if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
	}
	
	/** tp to location loc */
	public void teleport(Location loc) {
	    PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport();
	    setValue(tp, "a", Integer.valueOf(this.ID));
	    setValue(tp, "b", Integer.valueOf((int)(loc.getX() * 32.0D)));
	    setValue(tp, "c", Integer.valueOf((int)(loc.getY() * 32.0D)));
	    setValue(tp, "d", Integer.valueOf((int)(loc.getZ() * 32.0D)));
	    setValue(tp, "e", Byte.valueOf(toAngle(loc.getYaw())));
	    setValue(tp, "f", Byte.valueOf(toAngle(loc.getPitch())));
	    this.location = loc;
		if(player != null)
			sendPacket(tp, player);
		else
			sendPacket(tp);
	}
	
	/** let this npc sneak */
	public void sneak(){
		this.isSneaking = true;
		this.isBlocking = false;
		this.isSprinting = false;
	    DataWatcher w = new DataWatcher(null);
	    w.a(0, (byte) 2);
	    w.a(1, (short) 0);
	    w.a(8, (byte) 0);
	    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.ID, w, true);
	    if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
	}

	/** resets the sprinting, sneaking [...] values */
	public void resetMovement(){
		this.isBlocking = false;
		this.isSneaking = false;
		this.isSprinting = false;
	    DataWatcher w = new DataWatcher(null);
	    w.a(0, (byte) 0);
	    w.a(1, (short) 0);
	    w.a(8, (byte) 0);
	    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.ID, w, true);
	    if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
	}

	/** let this npc sprint */
	public void sprint(){
		this.isSprinting = true;
		this.isBlocking = false;
		this.isSneaking = false;
	    DataWatcher w = new DataWatcher(null);
	    w.a(0, (byte) 8);
	    w.a(1, (short) 0);
	    w.a(8, (byte) 0);
	    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.ID, w, true);
	    if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
	}

	/** blocks with sword, bow or consumables */
	public void block() {
		this.isBlocking = true;
		this.isSneaking = false;
		this.isSprinting = false;
	    DataWatcher w = new DataWatcher(null);
	    w.a(0, (byte) 16);
	    w.a(1, (short) 0);
	    w.a(6, (byte) 0);
	    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.ID, w, true);
	    if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
	}

	/** updates the inventory (esp. armor and item in hand) */
	public void updateItems(ItemStack inHand, ItemStack boots, ItemStack leggins, ItemStack chestplate, ItemStack helmet){
		if(inHand != null)
			this.handHeld = inHand;
		if(boots != null)
			this.boots = boots;
		if(leggins != null)
			this.leggins = leggins;
		if(chestplate != null)
			this.chestplate = chestplate;
		if(helmet != null)
			this.helmet = helmet;
		
	    PacketPlayOutEntityEquipment[] packets = { new PacketPlayOutEntityEquipment(this.ID, 1, CraftItemStack.asNMSCopy(this.helmet)), new PacketPlayOutEntityEquipment(this.ID, 2, CraftItemStack.asNMSCopy(this.chestplate)), new PacketPlayOutEntityEquipment(this.ID, 3, CraftItemStack.asNMSCopy(this.leggins)), new PacketPlayOutEntityEquipment(this.ID, 4, CraftItemStack.asNMSCopy(this.boots)), new PacketPlayOutEntityEquipment(this.ID, 0, CraftItemStack.asNMSCopy(this.handHeld)) };

	    for(int i = 0; i < packets.length; i++)
		    if(player != null)
				sendPacket(packets[i], player);
			else
				sendPacket(packets[i]);
	}
	
	/** sends a damage animation to all players */
	public void damageAnimation(){
	    PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
	    setValue(packet, "a", (int) this.ID);
	    setValue(packet, "b", (int) 1);
	    if(player != null){
	    	sendPacket(packet, player);
	    	player.playSound(this.location, Sound.HURT_FLESH, 1, 2);
	    }
		else{
			sendPacket(packet);
			for(Player p : Bukkit.getOnlinePlayers())
				p.playSound(this.location, Sound.HURT_FLESH, 1, 2);
		}
	}
	
	/** moves the npc with walking animation */
	public void move(double x, double y, double z, float yaw, float pitch){
		if(player != null)
			sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.ID, (byte) toFxdPnt(x), (byte) toFxdPnt(y), (byte) toFxdPnt(z), toAngle(yaw), toAngle(pitch), true), player);
		else
			sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.ID, (byte) toFxdPnt(x), (byte) toFxdPnt(y), (byte) toFxdPnt(z), toAngle(yaw), toAngle(pitch), true));
		this.location.add(toFxdPnt(x) / 32D, toFxdPnt(y) / 32D, toFxdPnt(z) / 32D);
		this.location.setYaw(yaw);
		this.location.setPitch(pitch);
	}
	
	private int toFxdPnt(double value){
		return (int) Math.floor(value * 32.0D);
	}
	
	public Location getLocation(){
		return this.location;
	}
	
	/** changes players head rotation */
	public void look(float yaw, float pitch){
		PacketPlayOutEntityHeadRotation headRot = new PacketPlayOutEntityHeadRotation();
		setValue(headRot, "a", this.ID);
		setValue(headRot, "b", toAngle(yaw));
		
		if(player != null){
			sendPacket(headRot);
			sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.ID, toAngle(yaw), toAngle(pitch), true), player);
		}
		else{
			sendPacket(headRot);
			sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.ID, toAngle(yaw), toAngle(pitch), true), player);
		}
		this.location.setYaw(yaw);
		this.location.setPitch(pitch);
	}
	
	private byte toAngle(float value){
		return (byte) ((int) (value * 256.0F / 360.0F));
	}
	
	/** adds the npc to the target's tablist */
	private void sendTablistPacket(){
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(profile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(profile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
		players.add(data);
		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		setValue(packet, "b", players);
		if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
	}
	/** removes the npc from the target's tablist */
	private void removeFromTablist(){
		boolean isOnline = false;
		for(Player p : Bukkit.getOnlinePlayers()){
			if(this.getName().equals(p.getName()))
					isOnline = true;
		}
		if(isOnline)
			return;
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
		setValue(packet, "b", Arrays.asList(packet.new PlayerInfoData(this.profile, 0, null, null)));
		if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
	}
	/** kills this npc */
	public void deSpawn(){
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.ID);
		if(player != null)
			sendPacket(packet, player);
		else
			sendPacket(packet);
		this.removeFromTablist();
	}
	
	/** plays the item use animation (hit or place blocks...) */
	public void swingArm(){
	    PacketPlayOutAnimation packet18 = new PacketPlayOutAnimation();
	    setValue(packet18, "a", Integer.valueOf(this.ID));
	    setValue(packet18, "b", Integer.valueOf(0));
	    if(player != null)
			sendPacket(packet18, player);
		else
			sendPacket(packet18);
	}
	
	public String getName(){
		return this.name;
	}
	public boolean isSneaking() {
		return isSneaking;
	}
	public boolean isSprinting() {
		return isSprinting;
	}
	public boolean isBlocking() {
		return isBlocking;
	}
	public ItemStack getItemInHand(){
		return this.handHeld;
	}
	public ItemStack[] getArmorContents(){
		return new ItemStack[]{ helmet, chestplate, leggins, boots };
	}
}
