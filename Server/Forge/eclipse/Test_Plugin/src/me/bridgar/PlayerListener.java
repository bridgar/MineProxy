package me.bridgar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;


public class PlayerListener implements Listener {
	
	public static final String PERM = "playerAbilities.allowed";
	public HashMap<UUID,PermissionAttachment> attachments;
	public HashMap<UUID, InetSocketAddress> playerIps;
	private PrintWriter pw;
	private long time;
	private final long HOUR = 1000 * 3600; 
	
	MyPlugin plugin;
	
	//Constructor that will register its events
	public PlayerListener(MyPlugin plugin, HashMap<UUID,PermissionAttachment> attachments, HashMap<UUID, InetSocketAddress> playerIps) {
		this.plugin = plugin;
		this.attachments = attachments;
		this.playerIps = playerIps;

		replaceWriter();
		
		time = System.currentTimeMillis();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	//Creates a world for the player and teleports them there
	//Each player gets their own world genned with their name
	//Player is also put into creative mode
	public void createPlayerWorld(Player player) {
		WorldCreator creator = new WorldCreator(player.getName());
		World world = Bukkit.createWorld(creator);
		
		player.teleport(world.getSpawnLocation());
		player.setGameMode(GameMode.CREATIVE);
	}
	
	//Teleports player out of their world and deletes their world
	//Player is also put into survival mode
	public void deletePlayerWorld(Player player) {
		World overworld = Bukkit.getWorlds().get(0);
		
		player.teleport(overworld.getSpawnLocation());
		player.setGameMode(GameMode.SURVIVAL);
		
		World world = Bukkit.getWorld(player.getName());
		unloadWorld(world);
		
		File worldFolder = world.getWorldFolder();
		if (deleteWorld(worldFolder)) {
			Bukkit.getServer().broadcastMessage("world deleted");
		} else {
			Bukkit.getServer().broadcastMessage("world not deleted");
		}
	}
	
	//Deletes world with supplied name
	public void deleteStringWorld(String s) {
		World world = Bukkit.getWorld(s);
		
		if(world == null) return;
		
		File worldFolder = world.getWorldFolder();
		if (deleteWorld(worldFolder)) {
			Bukkit.getServer().broadcastMessage("world deleted");
		} else {
			Bukkit.getServer().broadcastMessage("world not deleted");
		}
	}
	
	//When players log in, add their attachment
	//If they have permissions, create a world for them
	@EventHandler
	public void playerLoggedIn(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		PermissionAttachment attachment = player.addAttachment(plugin);
		
		attachments.put(player.getUniqueId(), attachment);
		
		//if the player is on the list, give them the permission
		if( plugin.getConfig().getKeys(false).contains(player.getName())) {
			attachment.setPermission(PERM, true);
		}
		
		if(player.hasPermission(PERM)) {
			createPlayerWorld(player);
		}
		
		InetSocketAddress pAddr = player.getAddress();
		
		UUID id = player.getUniqueId();
		
		playerIps.put(id, pAddr);
		
		player.performCommand("spawn");
	}
	
	//When players log out, remove their attachment
	@EventHandler
	public void playerLoggedOut(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		
		if(player.hasPermission(PERM)) {
			deletePlayerWorld(player);
		}
		
		PermissionAttachment attachment = attachments.get(player.getUniqueId());
		
		player.removeAttachment(attachment);
		
		attachments.remove(attachment);
		
		UUID id = player.getUniqueId();
		
		playerIps.remove(id);
	}
	
	//If a player has permissions, spit out their pitch
	@EventHandler
	public void playerTeleported(PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		
		Location loc = e.getTo();
		
		String worldName = loc.getWorld().getName();
		
		UUID id = player.getUniqueId();
		
		InetSocketAddress ip = playerIps.get(id);
		
		
		if(System.currentTimeMillis() >= time + HOUR) {
			time = System.currentTimeMillis();
			
			pw.close();
			replaceWriter();
			
		}
		
		pw.println(ip.toString() + " " + worldName + " " + System.currentTimeMillis());
		pw.flush();
		
		
	}
	
	private void unloadWorld(World world) {
	    if(!world.equals(null)) {
	        Bukkit.getServer().unloadWorld(world, true);
	    }
	}
	
	private boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
	
	private void replaceWriter() {
		String name = new Date().toString();
		name = name.replace(" ", "_");
		name = name.replace(":", "_");
		
		try {
			pw = new PrintWriter(name, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
