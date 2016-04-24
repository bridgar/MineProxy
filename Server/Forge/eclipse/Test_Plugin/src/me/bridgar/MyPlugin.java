package me.bridgar;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
	
	public HashMap<UUID,PermissionAttachment> attachments;
	public HashMap<UUID, InetSocketAddress> playerIps;
	public PlayerListener pl;
	public Permission playerPermission;
	public Transcoder trans;
	public ChestFactory chest;
	public NetworkManager nm;
	public Random r;
	
	@Override
	public void onEnable() {
		attachments = new HashMap<UUID, PermissionAttachment>();
		playerIps = new HashMap<UUID, InetSocketAddress>();
		
		trans = new Transcoder();
		chest = new ChestFactory(trans);
		nm = new NetworkManager(chest);
		r = new Random();
		
		pl = new PlayerListener(this, attachments, playerIps);
		PluginManager pm = getServer().getPluginManager();
		playerPermission = new Permission(pl.PERM);
		pm.addPermission(playerPermission);
	}
	
	@Override
	public void onDisable() {
		//TODO save perms list to file
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("fun")) {
			if(args.length != 1) {
				sender.sendMessage("Needs exactly one argument!");
				return false;
			}
			
			Player target = (Bukkit.getServer().getPlayer(args[0]));
			if (target == null) {
				sender.sendMessage(args[0] + " is not online!");
				//add perms to offline player
				this.getConfig().set(args[0], true);
				sender.sendMessage(args[0] + " has been added to the perms list anyway.");
			} else {
				//add perms to online player
				this.getConfig().set(args[0], true);
				
				attachments.get(target.getUniqueId()).setPermission(pl.PERM, true);
				
				pl.createPlayerWorld(target);
			}
			this.saveConfig();
			return true;
		} else if(cmd.getName().equalsIgnoreCase("nofun")) {
			if(args.length != 1) {
				sender.sendMessage("Needs exactly one argument!");
				return false;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(args[0] + " is not online!");
				//remove perms from offline player
				this.getConfig().set(args[0], null);
				sender.sendMessage(args[0] + " has been removed from the perms list anyway.");
				
				pl.deleteStringWorld(args[0]);
			} else {
				//remove perms from online player
				this.getConfig().set(args[0], null);
				
				attachments.get(target.getUniqueId()).setPermission(pl.PERM, false);
				
				pl.deletePlayerWorld(target);
			}
			
			this.saveConfig();
			return true;
		} else if(cmd.getName().equalsIgnoreCase("test")) {
			ArrayList<Byte> bytes = new ArrayList<Byte>();
			
			byte[] ins = new byte[54];
			r.nextBytes(ins);
			
			for(int i = 0; i < 54; i++) {
				bytes.add(ins[i]);
			}
			
			ItemStack item = trans.encodeItemStack(bytes);
			
			if(item.equals(null)) {
				sender.sendMessage("Null");
				return true;
			}
			
			if(!item.hasItemMeta()) {
				sender.sendMessage("No Item Meta");
				return true;
			}
			
			if(!item.getItemMeta().hasDisplayName()) {
				sender.sendMessage("No Display Name");
				return true;
			}
			
			Player p = (Player) sender;
			p.setItemInHand(item);
			
			ArrayList<Byte> newBytes =  trans.decodeItemStack(item);
			
			for(int i = 0; i < 54; i++) {
				if(bytes.get(i) != (newBytes.get(i))) {
					sender.sendMessage("Byte " + i + " does not match\n Original: " + bytes.get(i) + "\n Returned: " + newBytes.get(i));
				}
			}
		}
		
		return false;
	}
	
	
	
}
