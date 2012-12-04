package szhang_unca_edu.RandomArena;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

public class RandomArenaCommandExecutor implements CommandExecutor {	
    private final RandomArena plugin;

    /* 
     * This command executor needs to know about its plugin from which it came from
     */
    public RandomArenaCommandExecutor(RandomArena plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return false;
		}     	
		
		else if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "you must be logged on to use these commands");
			return false;

		} 
		
		// help on available commands
		else if (args[0].equalsIgnoreCase("help")) {
			Player player = (Player) sender;				
			player.sendMessage("Available commands:");
			player.sendMessage("/random set <x1> <z1> <x2> <z2> : set arena coordinates");
			player.sendMessage("/random teleport : join active arena");
			player.sendMessage("/random cancel : cancel active arena");
			player.sendMessage("/random start : start game");
			
			return true;
		}
		
		// sets arena perimeters
		else if (args[0].equalsIgnoreCase("set")) {
			if (args.length > 4) {
				Player player = (Player) sender;				
				
				if (plugin.worldvariables.get("arenaset") == false) {					
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
					
					//Logger for set arena location
					 plugin.logger.info("Arena set at: x1: " + plugin.arenacoordinates.get("x1") 
							 + " x2: " + plugin.arenacoordinates.get("x2") 
							 + " z1: " + plugin.arenacoordinates.get("z1")
							 + " z2: " + plugin.arenacoordinates.get("z2"));
					 
					//Setting hashmaps
			        plugin.worldvariables.put("arenaset", true);
			        plugin.arenasetter.put("arenasetter", player);
			        plugin.playersready.put(player, true);	
			        
			        //Gets middle of location
			        Location loc = player.getLocation();
			        int newx = (plugin.arenacoordinates.get("x1") + plugin.arenacoordinates.get("x2")) / 2;
			        int newz = (plugin.arenacoordinates.get("z1") + plugin.arenacoordinates.get("z2")) / 2;
			        
			        //Sets loc's x and z to middle coordinates
			        loc.setX(newx);
			        loc.setZ(newz);
			        
			        //Sets the y value of loc
			        int newy = loc.getWorld().getHighestBlockYAt(loc);
			        loc.setY(newy);
			        
			        // Create wall of obsidian to stop everything from escaping			        
			        Location blockloc = player.getLocation();

			        for(int x = plugin.arenacoordinates.get("x1"); x <= plugin.arenacoordinates.get("x2"); x++) {
			        	for(int z = plugin.arenacoordinates.get("z1"); z <= plugin.arenacoordinates.get("z2"); z++) {
			        		if(x == plugin.arenacoordinates.get("x1") || x == plugin.arenacoordinates.get("x2")
			        				|| z == plugin.arenacoordinates.get("z1") || z == plugin.arenacoordinates.get("z2")) {
			        			if(x == plugin.arenacoordinates.get("x1")) {
			        				blockloc.setZ(z - 1);
			        				blockloc.setX(x);
			        			}
			        			if(x == plugin.arenacoordinates.get("x2")) {
			        				blockloc.setZ(z + 1);
			        				blockloc.setX(x);			        				
			        			}
			        			if(z == plugin.arenacoordinates.get("z1")) {
			        				blockloc.setZ(z);			        				
			        				blockloc.setX(x - 1);
			        			}
			        			
			        			if(z == plugin.arenacoordinates.get("z2")) {
			        				blockloc.setZ(z);
			        				blockloc.setX(x + 1);
			        			}
			        			
			        			// set height of wall
			        			int highestblock = blockloc.getWorld().getHighestBlockYAt(blockloc);

			        			for(int y = highestblock; y < highestblock + 12; y++) {
			        				blockloc.setY(y);
			        				blockloc.getWorld().getBlockAt(blockloc).setType(Material.OBSIDIAN);
			        			}
			        		}			        					        	
			        	}
			        }
			        
			        //Teleports player to middle location
			        player.teleport(loc);
			        
			        player.getWorld().setTime(12000);
				}
				else {
					player.sendMessage("Arena already created, please use /random teleport to join");
				}		        
		        
		        return true;
			}
		}
		
		// teleports player to starting point
		else if (args[0].equalsIgnoreCase("teleport")) {
			Player player = (Player) sender;								
			
			//if there is an arena that hasn't started yet, allows the player to teleport to it
			if (plugin.worldvariables.get("arenaset") == true && plugin.worldvariables.get("started") == false) {								        
		        player.teleport(plugin.arenasetter.get("arenasetter").getLocation());		
		        
		        plugin.playersready.put(player, true);
			}
			else {
				player.sendMessage("No arena active");
			}
	        
	        return true;
		}
			
		// cancels arena set
		else if (args[0].equalsIgnoreCase("cancel")) {
			Player player = (Player) sender;	
			List <Player> players = player.getWorld().getPlayers();

			if (plugin.arenasetter.get("arenasetter") == player) {
		        plugin.worldvariables.put("arenaset", false);
		        plugin.worldvariables.put("started", false);
		        
		        for (int i = 0; i < plugin.playersready.size(); i++) {
		        	plugin.playersready.put(players.get(i), false);
		        }
		        
		        // Remove wall of obsidian to release player	        
		        Location blockloc = player.getLocation();

		        for(int x = plugin.arenacoordinates.get("x1"); x <= plugin.arenacoordinates.get("x2"); x++) {
		        	for(int z = plugin.arenacoordinates.get("z1"); z <= plugin.arenacoordinates.get("z2"); z++) {
		        		if(x == plugin.arenacoordinates.get("x1") || x == plugin.arenacoordinates.get("x2")
		        				|| z == plugin.arenacoordinates.get("z1") || z == plugin.arenacoordinates.get("z2")) {
		        			if(x == plugin.arenacoordinates.get("x1")) {
		        				blockloc.setZ(z - 1);
		        				blockloc.setX(x);
		        			}
		        			if(x == plugin.arenacoordinates.get("x2")) {
		        				blockloc.setZ(z + 1);
		        				blockloc.setX(x);			        				
		        			}
		        			if(z == plugin.arenacoordinates.get("z1")) {
		        				blockloc.setZ(z);			        				
		        				blockloc.setX(x - 1);
		        			}
		        			
		        			if(z == plugin.arenacoordinates.get("z2")) {
		        				blockloc.setZ(z);
		        				blockloc.setX(x + 1);
		        			}
		        			
		        			// set height of wall
		        			int originalblock = blockloc.getWorld().getHighestBlockYAt(blockloc);
		        			originalblock = originalblock - 12;

		        			for(int y = originalblock; y <originalblock + 12; y++) {
		        				blockloc.setY(y);
		        				blockloc.getWorld().getBlockAt(blockloc).setType(Material.AIR);
		        			}
		        		}			        					        	
		        	}
		        }		        
			}
			else {
				player.sendMessage("You did not create the arena active");
			}									
			
			return true;
		}
		
		// starts arena game
		else if (args[0].equalsIgnoreCase("start")) {
			plugin.monsterskilled.put("killed", 0);
			
			//Array for monster ids for spawning
			int monsterIds [] = new int[10];
			
			monsterIds[0] = 50; //53 is Giant
			monsterIds[1] = 51;	
			monsterIds[2] = 54;
			monsterIds[3] = 55;	
			monsterIds[4] = 56;	
			monsterIds[5] = 57;	
			monsterIds[6] = 59;	
			monsterIds[7] = 60;	
			monsterIds[8] = 61;	
			monsterIds[9] = 62;				
			
			Player player = (Player) sender;	

			if (plugin.worldvariables.get("arenaset")) {
				if (plugin.arenasetter.get("arenasetter") == player) {
					if (plugin.worldvariables.get("started") == false) {
				        // spawn
						int difx = plugin.arenacoordinates.get("x2") - plugin.arenacoordinates.get("x1");
						int difz = plugin.arenacoordinates.get("z2") - plugin.arenacoordinates.get("z1");
						int area = difx * difz;
						int spawnNum = area / 500;					
						
						// if area is too small, set spawn number to a certain number
						if (spawnNum < 4) {
							spawnNum = 4;
						}						
						
						plugin.monsterskilled.put("killtospawn", spawnNum - 1);
						
						Location loc = player.getLocation();
				        Random randomnum = new Random();        	
						
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
						
						plugin.worldvariables.put("started", true);
					}
					else {
						player.sendMessage("Arena game has already started");
					}
				}
				else {
					player.sendMessage("You did not create the arena active");
				}
			}
			else {
				player.sendMessage("Arena has not been set");
			}
			
			return true;
		}
		
		// forced spawn if player cannot find any monsters
		else if (args[0].equalsIgnoreCase("spawn")) {
			Player player = (Player) sender;	
			List <Player> players = player.getWorld().getPlayers();
			
			if (plugin.worldvariables.get("arenaset")) {
				if (plugin.arenasetter.get("arenasetter") == player) {
					if (plugin.worldvariables.get("started") == true) {
						//Array for monster ids for spawning
						int monsterIds [] = new int[10];
						
						monsterIds[0] = 50; //53 is Giant
						monsterIds[1] = 51;	
						monsterIds[2] = 54;
						monsterIds[3] = 55;	
						monsterIds[4] = 56;	
						monsterIds[5] = 57;	
						monsterIds[6] = 59;	
						monsterIds[7] = 60;	
						monsterIds[8] = 61;	
						monsterIds[9] = 62;				

				        // spawn
						int difx = plugin.arenacoordinates.get("x2") - plugin.arenacoordinates.get("x1");
						int difz = plugin.arenacoordinates.get("z2") - plugin.arenacoordinates.get("z1");
						int area = difx * difz;
						int spawnNum = area / 500;					
						
						// if area is too small, set spawn number to a certain number
						if (spawnNum < 4) {
							spawnNum = 4;
						}				
						
						Location loc = player.getLocation();
				        Random randomnum = new Random();        	
						
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
					else {
						player.sendMessage("Arena game has not started");
					}					
				}
				else {
					player.sendMessage("You did not create the arena active");
				}				
			}
			else {
				player.sendMessage("Arena has not been set");
			}			
			
			return true;
		}		

		return false;	
	}	
}