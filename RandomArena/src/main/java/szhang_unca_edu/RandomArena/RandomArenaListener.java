package szhang_unca_edu.RandomArena;

import java.text.MessageFormat;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;

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
    }
    
	@EventHandler(priority = EventPriority.HIGH)
	public void arenabounds (PlayerMoveEvent event) {
		if (plugin.abilities.get("arenaset")) {						
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
    
    
    // sets worldly events to false upon start of plugin
    @EventHandler
    public void worldset(PluginEnableEvent event) {
        plugin.abilities.put("arenaset", false);   
    }
    
    
    /*
     * Another example of a event handler. This one will give you the name of
     * the entity you interact with, if it is a Creature it will give you the
     * creature Id.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        final EntityType entityType = event.getRightClicked().getType();

        event.getPlayer().sendMessage(MessageFormat.format(
                "You interacted with a {0} it has an id of {1}",
                entityType.getName(),
                entityType.getTypeId()));
    }
}
