package szhang_unca_edu.RandomArena;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/*
 * This is a sample event listener
 */
public class RandomArenaListener implements Listener {
    private final RandomArena plugin;

    /*
     * This listener needs to know about the plugin which it came from
     */
    public RandomArenaListener(RandomArena plugin) {
        // Register the listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        this.plugin = plugin;
    }

    /*
     * Send the sample message to all players that join
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(this.plugin.getConfig().getString("sample.message"));
        plugin.playersready.put(event.getPlayer(), false);  
        event.getPlayer().setItemInHand(new ItemStack (Material.DIAMOND_SWORD));
        event.getPlayer().getInventory().addItem(new ItemStack(Material.BOW, 1));
        event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 50));        
    }
    
    // prevents the player from moving in certain cases
	@EventHandler(priority = EventPriority.LOW)
	public void arenabounds (PlayerMoveEvent event) {
		if (plugin.worldvariables.get("arenaset")) {		
			if (plugin.playersready.get(event.getPlayer())) {
				if (plugin.worldvariables.get("started") == false) {
					event.setCancelled(true);			
					event.getPlayer().teleport(event.getFrom());	
				}
				
				if (event.getTo().getX() < plugin.arenacoordinates.get("x1") || 
						event.getTo().getX() > plugin.arenacoordinates.get("x2")) {
					event.setCancelled(true);			
					event.getPlayer().teleport(event.getFrom());
				}
				
				if (event.getTo().getZ() < plugin.arenacoordinates.get("z1") || 
						event.getTo().getZ() > plugin.arenacoordinates.get("z2")) {
					event.setCancelled(true);			
					event.getPlayer().teleport(event.getFrom());				
				}
			}
		}
	}    
		
	// when enemy is killed by player
	@EventHandler
	public void enemydeath (EntityDeathEvent event) {	
        Random randomnum = new Random();        							
		List <Player> players;		    	    	
        
		if (event.getEntity().getKiller() != null) {
			players = event.getEntity().getKiller().getWorld().getPlayers();		    	    	
			
			Player player = (Player) event.getEntity().getKiller();
			
			int monsterIds [] = new int[10];
			
			monsterIds[0] = 50;
			monsterIds[1] = 51;	
			monsterIds[2] = 54;
			monsterIds[3] = 55;	
			monsterIds[4] = 56;	
			monsterIds[5] = 57;	
			monsterIds[6] = 59;	
			monsterIds[7] = 60;	
			monsterIds[8] = 61;	
			monsterIds[9] = 62;
							
			if (plugin.worldvariables.get("started") && plugin.playersready.get(event.getEntity().getKiller())) {				
				plugin.monsterskilled.put("killed", plugin.monsterskilled.get("killed") + 1);	
				
		        for (int i = 0; i < plugin.playersready.size(); i++) {
		        	players.get(i).sendMessage(plugin.monsterskilled.get("killed") + " monsters have been killed!");
		        }
			}
			
			if ((plugin.monsterskilled.get("killed") % plugin.monsterskilled.get("killtospawn")) == 0) {
		        plugin.monsterskilled.put("wave", plugin.monsterskilled.get("wave") + 1);
		        
		        // world settings changed here
		        if (plugin.monsterskilled.get("wave") == 5) {
		        	if (randomnum.nextInt(100) > 50) {
		        		worldevents(plugin.monsterskilled.get("wave"), event.getEntity().getKiller(), players);
		        	}
		        }
				
		        // spawn
				int difx = plugin.arenacoordinates.get("x2") - plugin.arenacoordinates.get("x1");
				int difz = plugin.arenacoordinates.get("z2") - plugin.arenacoordinates.get("z1");
				int area = difx * difz;
				int spawnNum = area / 500;
				
				
				// if area is too small, set spawn number to a certain number
				if (spawnNum < 4) {
					spawnNum = 4;
				}				
				spawnNum = spawnNum + randomnum.nextInt(spawnNum / 4);
							
				Location loc = player.getLocation();
		        
				for (int i = 0; i < spawnNum; i++) {       					
					loc.setX(randomnum.nextInt(((plugin.arenacoordinates.get("x2"))
							- (plugin.arenacoordinates.get("x1")))) 
							+ plugin.arenacoordinates.get("x1") + 1);
					
					loc.setZ(randomnum.nextInt(((plugin.arenacoordinates.get("z2"))
							- (plugin.arenacoordinates.get("z1")))) 
							+ plugin.arenacoordinates.get("z1") + 1);	

					loc.setY(loc.getWorld().getHighestBlockYAt(loc));			
					
					player.getWorld().spawnEntity(loc, EntityType.fromId(monsterIds[randomnum.nextInt(10)]));
				}			
			}
		}
	}
    
    
    // sets worldly events to false upon start of plugin
    @EventHandler
    public void worldset(PluginEnableEvent event) {
        plugin.worldvariables.put("arenaset", false); 
        plugin.worldvariables.put("started", false);
		plugin.worldvariables.put("torrent", false);        
        plugin.monsterskilled.put("wave", 0);
    }
    
    // world events decided here
    public void worldevents(int wavenumber, Player player, List <Player> players) {    	
    	if (wavenumber == 5) { 
    		
	        for (int i = 0; i < plugin.playersready.size(); i++) {
	        	players.get(i).sendMessage("A torrent has started!");
	        }
    		
    		player.getWorld().setStorm(true);
    		player.getWorld().setThundering(true);
    		plugin.worldvariables.put("torrent", true);
    	}    	    	
    }
    
    // World events that occur
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void worldcontrol (PlayerMoveEvent event) {
		Random randomnum = new Random();
		
		if (plugin.worldvariables.get("torrent")) {
			if (randomnum.nextInt(500) < 10) {
				Location loc = event.getPlayer().getLocation();
			        
				loc.setX(randomnum.nextInt(((plugin.arenacoordinates.get("x2")) - (plugin.arenacoordinates.get("x1")))) 
							+ plugin.arenacoordinates.get("x1") + 1);					
				loc.setZ(randomnum.nextInt(((plugin.arenacoordinates.get("z2")) - (plugin.arenacoordinates.get("z1")))) 
							+ plugin.arenacoordinates.get("z1") + 1);	
				loc.setY(loc.getWorld().getHighestBlockYAt(loc));			
				
				loc.getBlock().setType(Material.WATER);
			}								
		}			
	}	
}
