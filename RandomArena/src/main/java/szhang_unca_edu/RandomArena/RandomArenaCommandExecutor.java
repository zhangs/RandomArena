package szhang_unca_edu.RandomArena;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

/*
 * This is a sample CommandExectuor
 */
public class RandomArenaCommandExecutor implements CommandExecutor {
    private final RandomArena plugin;

    /* 
     * This command executor needs to know about its plugin from which it came from
     */
    public RandomArenaCommandExecutor(RandomArena plugin) {
        this.plugin = plugin;
    }

    /*
     * On command set the sample message
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return false;
		}     	
		
		else if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "you must be logged on to use these commands");
			return false;

		} 
		
		// sets arena perimeters  /random set 50 50 100 100 (x1 z1 x2 z2)
		else if (args[0].equalsIgnoreCase("set")) {
			if (args.length > 4) {
				Player player = (Player) sender;				
				
				if (Integer.parseInt(args[1]) > Integer.parseInt(args[3])) {
			        plugin.arenacoordinates.put("x1", Integer.parseInt(args[3]));
			        plugin.arenacoordinates.put("x2", Integer.parseInt(args[1]));					
				}
				else {
			        plugin.arenacoordinates.put("x1", Integer.parseInt(args[1]));
			        plugin.arenacoordinates.put("x2", Integer.parseInt(args[3]));					
				}
				
				if (Integer.parseInt(args[2]) > Integer.parseInt(args[4])) {
			        plugin.arenacoordinates.put("z1", Integer.parseInt(args[4]));
			        plugin.arenacoordinates.put("z2", Integer.parseInt(args[2]));					
				}
				else {
			        plugin.arenacoordinates.put("z1", Integer.parseInt(args[2]));
			        plugin.arenacoordinates.put("z2", Integer.parseInt(args[4]));					
				}				

		        plugin.abilities.put("arenaset", true);
		        
		        Location loc = player.getLocation();
		        int newx = (plugin.arenacoordinates.get("x1") + plugin.arenacoordinates.get("x2")) / 2;
		        int newz = (plugin.arenacoordinates.get("z1") + plugin.arenacoordinates.get("z2")) / 2;
		        
		        loc.setX(newx);
		        loc.setZ(newz);
		        
		        int newy = loc.getWorld().getHighestBlockYAt(loc);
		        loc.setY(newy);
		        
		        player.teleport(loc);
		        
		        return true;
			}
			else {
				return false;
			}		
		}
		
		else {
			return false;
		}
    }

}
