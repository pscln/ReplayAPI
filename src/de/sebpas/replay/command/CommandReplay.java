package de.sebpas.replay.command;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.sebpas.replay.RePlayer;
import de.sebpas.replay.ReplaySystem;

public class CommandReplay implements CommandExecutor{
	private ReplaySystem plugin;
	
	public CommandReplay(ReplaySystem plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("replay.command")){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + "&cDazu bist du nicht berechtigt!"));
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("rplstart")){
			plugin.start();
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + " &3Aufnahme begonnen!"));
		}
		if(cmd.getName().equalsIgnoreCase("rplstop")){
			plugin.stop();
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + " &3Aufnahme beendet!"));
		}
		if(cmd.getName().equalsIgnoreCase("replay")){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getErrorPrefix() + "Nur für " +
                        "Spieler geeignet!"));
				return true;
			}
			if(args.length != 1 && args.length != 2){
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getErrorPrefix() + "Fehler: /replay <args: stop / play / time> [file / hour:minute:second]"));
			}
			Player p = (Player) sender;
			if(args.length == 2 && args[0].equalsIgnoreCase("play")){
				if(plugin.isAlreadyInReplay(p)){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getErrorPrefix() + "Du befindest dich bereits in einem Replay! Du kannst dieses mit /replay stop verlassen"));
					return true;
				}
				RePlayer player = new RePlayer(args[1], p);
				player.start();
			}
			if(args.length == 2 && args[0].equalsIgnoreCase("time")){
				String[] temp = args[1].split(":");
				int ticks = 0;
				if(temp.length == 1){
					ticks += Integer.parseInt(temp[0]) * 20;
				}
				if(temp.length == 2){
					System.out.println(temp[0] + ". " + temp[1]);
					ticks += Integer.parseInt(temp[0]) * 60 * 20 + Integer.parseInt(args[1]) * 20;
				}
				if(temp.length == 3){
					ticks += Integer.parseInt(temp[0]) * 60 * 60 * 20 + Integer.parseInt(temp[1]) * 60 * 20 + Integer.parseInt(args[2]) * 20;
				}
				RePlayer r = plugin.getPlayersRePlayer(p);
				if(r == null){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getErrorPrefix() + "Du befindest nicht in einem Replay!"));
					return true;
				}
				if(r.setCurrentTick(ticks))
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + "&3Die Zeit wurde auf &c" + args[1] + " &3gesetzt!"));
				else
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getErrorPrefix() + "&cDie angegebene Zeit ist zu lang."));
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("stop")){
				RePlayer r = ReplaySystem.getInstance().getPlayersRePlayer(p);
				if(r == null){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getErrorPrefix() + "Du befindest nicht in einem Replay!"));
					return true;
				}
				r.stop();
			}
		}
		return true;
	}
	
}
