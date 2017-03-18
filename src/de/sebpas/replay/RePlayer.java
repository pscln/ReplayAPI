package de.sebpas.replay;

import de.sebpas.replay.event.ReplayStartEvent;
import de.sebpas.replay.npc.NPC;
import de.sebpas.replay.util.PlayingPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RePlayer {
	private double lastTick;
	private Map<Player, PlayingPlayer> players;
	private List<String> tickList;
	private double currentTick;
	private double velocity = 1;
	private boolean isRunning = false;
	
	/** NPC stuff */
	private List<NPC> npcs = new ArrayList<NPC>();
	
	/** thread stuff */
	private int taskID;
	private Runnable task = new Runnable() {
		
		@Override
		public void run() {
			if(isRunning){
//				if(currentTick - lastTick > 1 && lastTick % 1 == 0){
					double ticks = Math.floor(currentTick - lastTick);
					System.out.println("Last tick: " + lastTick + ", current: " + currentTick + ", ticks to run: " + ticks);
					for(int j = 0; j < ticks; j++){
						System.out.println("running: " + j  + " / " + ticks + " ticks!");
						for(String s : getCurrentStringList((int) lastTick + j)){
							if(s.split(";").length == 1)
								break;
							String name = s.split(";")[2];
							String uuid = s.split(";")[1];
							if(!isExisting(s.split(";")[2])){
								double x = 0, y = 0, z = 0;
								float yaw = 0, pitch = 0;
								String[] temp = s.split(";")[3].replace("moved:", "").split(",");
								try{
								x = Double.parseDouble(temp[0]);
								y = Double.parseDouble(temp[1]);
								z = Double.parseDouble(temp[2]);
			
								yaw = Float.parseFloat(temp[3]);
								pitch = Float.parseFloat(temp[4]);
								}catch(Exception e){
									x = 0;
									y = 255;
									z = 0;
									yaw = 45;
									pitch = 0;
								}
								NPC npc = new NPC(uuid, name, new Location(((Player) players.keySet().toArray()[0]).getWorld(), x, y, z, yaw, pitch), (Player) players.keySet().toArray()[0]);
								npcs.add(npc);
								npc.spawn();
							}else if(s.split(";")[3].startsWith("moved:")){ /** movement */
								double x = 0, y = 0, z = 0;
								float yaw = 0, pitch = 0;
								String[] temp = s.split(";")[3].replace("moved:", "").split(",");
								x = Double.parseDouble(temp[0]);
								y = Double.parseDouble(temp[1]);
								z = Double.parseDouble(temp[2]);
			
								yaw = Float.parseFloat(temp[3]);
								pitch = Float.parseFloat(temp[4]);
			
								if(currentTick % 2 != 0){
									float yawR = (float) Math.toRadians(yaw);
									x = -Math.sin(yawR);
									z = Math.cos(yawR);						
								}

								double speed = 4.3D;

								if(s.split(";").length == 4 && getNPCByName(name).isBlocking() || getNPCByName(name).isSneaking() || getNPCByName(name).isSprinting())
									getNPCByName(name).resetMovement();

								if(s.split(";").length == 5){
									if(s.split(";")[4].equalsIgnoreCase("sprint"))
										getNPCByName(name).sprint();
									if(s.split(";")[4].equalsIgnoreCase("sneak")){
										getNPCByName(name).sneak();
										speed /= 3D;
									}
									if(s.split(";")[4].equalsIgnoreCase("block")){
										getNPCByName(name).block();
										speed /= 4D;
									}
								}

								if(s.split(";").length == 6){
									if(s.split(";")[4].equalsIgnoreCase("block"))
										getNPCByName(name).block();
								}

								getNPCByName(name).look(yaw, pitch);
								getNPCByName(name).move(x * (speed / 20), y - getNPCByName(name).getLocation().getY(), z * (speed / 20), yaw, pitch);

								if(currentTick % 2 == 0)
									getNPCByName(name).teleport(new Location(((Player) players.keySet().toArray()[0]).getWorld(), x, y, z, yaw, pitch));
							}else if(s.split(";").length == 1)
								break;
							
							if(s.split(";")[3].startsWith("swing")) /** swinging the item in hand */ {
								getNPCByName(name).swingArm();
							}else if(s.split(";")[3].startsWith("dmg")) /** damage animation */ {
								getNPCByName(name).damageAnimation();
							}else if(s.split(";")[3].startsWith("armr")) /** armor content updating */ {
								String[] temp = s.split(";")[3].replace("armr:", "").split(",");
								ItemStack[] armor = new ItemStack[4]; 
								for(int i = 0; i < temp.length; i++){
									armor[i] = new ItemStack(Material.getMaterial(temp[i]));
								}
								getNPCByName(name).updateItems(getNPCByName(name).getItemInHand(), armor[3], armor[2], armor[1], armor[0]);
							}else if(s.split(";")[3].startsWith("itmhnd")) /** item in hand */ {
									getNPCByName(name).updateItems(new ItemStack(Material.getMaterial(s.split(";")[3].replace("itmhnd:", ""))), getNPCByName(name).getArmorContents()[0], getNPCByName(name).getArmorContents()[1], getNPCByName(name).getArmorContents()[2], getNPCByName(name).getArmorContents()[3]);
							}else if(s.split(";")[3].startsWith("lggdin")) /** joined the game */ {
								double x = 0, y = 0, z = 0;
								String[] temp = s.split(";")[3].replace("lggdin:", "").split(",");
								try{
									x = Double.parseDouble(temp[0]);
									y = Double.parseDouble(temp[1]);
									z = Double.parseDouble(temp[2]);
								}catch(Exception e){
									x = 0;
									y = 255;
									z = 0;
								}

								sendChatMessageToAll(s.split(";")[4]);
								getNPCByName(name).spawn(new Location(((Player) players.keySet().toArray()[0]).getWorld(), x, y, z));
							}else if(s.split(";")[3].startsWith("lggdout")) /** left the game */ {
								sendChatMessageToAll(s.split(";")[4]);
								getNPCByName(name).deSpawn();
							}else if(s.split(";")[3].startsWith("died")) /** died */ {
								sendChatMessageToAll(s.split(";")[4]);
								getNPCByName(name).deSpawn();
							}else if(s.split(";")[3].startsWith("rspn")) /** respawned */ {
								double x = 0, y = 0, z = 0;
								String[] temp = s.split(";")[3].replace("rspn:", "").split(",");
								try{
									x = Double.parseDouble(temp[0]);
									y = Double.parseDouble(temp[1]);
									z = Double.parseDouble(temp[2]);
								}catch(Exception e){
									x = 0;
									y = 255;
									z = 0;
								}

								sendChatMessageToAll(s.split(";")[4]);
								getNPCByName(name).spawn(new Location(((Player) players.keySet().toArray()[0]).getWorld(), x, y, z));
							}else if(s.split(";")[3].startsWith("cht")) /** chat message sent */ {
								for(Player p : players.keySet())
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.split(";")[4]));
							}
						}
						lastTick = Math.floor(currentTick);
					}
			
				/** tick decrease */
				currentTick += velocity;
			}
			for(Player p : players.keySet()){
				p.setExp((float) currentTick / (float) getLastTick()); 
				if(currentTick % 20 == 0)
					p.setLevel((int) currentTick / 20);
				p.setHealth(20D);
				p.setFoodLevel(20);
			}
			if(currentTick > getLastTick() || players.isEmpty())
				stop();

		}
	};
	
	public void pause(){
		this.isRunning = false;
	}
	
	public void continueReplay(){
		this.isRunning = true;
	}
	
	public RePlayer(String file, Player player){
		this.currentTick = 0;
		this.players = new HashMap<Player, PlayingPlayer>();
		this.players.put(player, new PlayingPlayer(player));
		tickList = ReplaySystem.getInstance().getFileManager().readFile(file);
		if(!tickList.isEmpty())
			ReplaySystem.getInstance().addPlayer(this);
	}
	
	public RePlayer(String file, Map<Player, PlayingPlayer> players){
		this.currentTick = 0;
		this.players = players;
		tickList = ReplaySystem.getInstance().getFileManager().readFile(file);
		if(!tickList.isEmpty())
			ReplaySystem.getInstance().addPlayer(this);
	}
	
	@SuppressWarnings("deprecation")
	public void start(){
		this.isRunning = true;
		Bukkit.getPluginManager().callEvent(new ReplayStartEvent(this));
		if(tickList == null){
			for(Player p : this.players.keySet()){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getErrorPrefix() + " &3Fehler beim lesen der Datei!"));	
			}
			return;
		}
		for(Player p : this.players.keySet()){
			p.setGameMode(GameMode.ADVENTURE);
			p.setAllowFlight(true);
			p.setFlying(true);
			p.setHealth(20D);
			p.setFoodLevel(20);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getPrefix() + "&3Replay gestartet!"));	
		}
		taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(ReplaySystem.getInstance(), task, 1L, 1L);
	}
	
	public void stop(){
		this.isRunning = false;
		Bukkit.getScheduler().cancelTask(taskID);
		for(NPC n : npcs)
			n.deSpawn();
		for(Player p : this.players.keySet()){
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			this.getPlayers().get(p).throwIntoGame(p, true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getPrefix() + " &3Das Replay ist beendet. Du hast das Replay verlassen."));
		}
		ReplaySystem.getInstance().onPlayerStopped(this);
	}
	
	/**
	 * ONLY use while stopping plugin
	 */
	public void stopWithoutTask(){
		this.isRunning = false;
		Bukkit.getScheduler().cancelTask(taskID);
		for(NPC n : npcs)
			n.deSpawn();
		for(Player p : this.players.keySet()){
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			this.getPlayers().get(p).throwIntoGame(p, false);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getPrefix() + " &3Das Replay ist beendet. Du hast das Replay verlassen."));
		}
		ReplaySystem.getInstance().onPlayerStopped(this);
	}
	
	@Deprecated
	/**
	 * use RePlayer.stop() instead
	 * @param p
	 */
	public void removePlayer(Player p){
		this.players.remove(p);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', ReplaySystem.getInstance().getPrefix() + " &3Du hast das Replay verlassen."));
		this.stop();
	}
	
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	public double getCurrentTick() {
		return currentTick;
	}
	public boolean setCurrentTick(double currentTick) {
		if(currentTick >= this.getLastTick() || currentTick < 0)
			return false;
		this.currentTick = currentTick;
		this.lastTick = currentTick -= velocity;
		return true;
	}
	public Map<Player, PlayingPlayer> getPlayers() {
		return players;
	}
	public boolean isRunning() {
		return isRunning;
	}
	private int getLastTick(){
		int max = 0;
		for(String s : tickList){
			if(Integer.parseInt(s.split(";")[0]) > max)
			max = Integer.parseInt(s.split(";")[0]);
		}
		return max;
	}
	
	private List<String> getCurrentStringList(int tick){
		List<String> rtn = new ArrayList<String>();
		for(String s : tickList){
			if(Integer.parseInt(s.split(";")[0]) == tick)
				rtn.add(s);
		}
		return rtn;
	}
	private boolean isExisting(String name){
		for(NPC n : this.npcs){
			if(n.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	public NPC getNPCByName(String name){
		for(NPC n : npcs){
			if(n.getName().equalsIgnoreCase(name))
				return n;
		}
		return null;
	}

	public List<NPC> getNpcs() {
		return npcs;
	}
	private void sendChatMessageToAll(String msg){
		for(Player p : this.players.keySet())
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
}
