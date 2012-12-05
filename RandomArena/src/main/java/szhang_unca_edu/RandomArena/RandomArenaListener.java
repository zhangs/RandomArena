package szhang_unca_edu.RandomArena;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RandomArenaListener implements Listener {
    private final RandomArena plugin;
    int zombieIncrease = 1;

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
	
	// When creator of event leaves server
	@EventHandler
	public void arenacreatorquit (PlayerQuitEvent event) {
		if (plugin.arenasetter.get("arenasetter") == event.getPlayer()) {
			plugin.worldvariables.put("arenaset", false);
			plugin.worldvariables.put("started", false);
			
			List <Player> players = event.getPlayer().getWorld().getPlayers();
	        for (int i = 0; i < players.size(); i++) {
	        	players.get(i).sendMessage("Arena setter has exited the server. Game ends!");
	        }
	        
	        // Remove wall of obsidian to release player	        
	        Location blockloc = event.getPlayer().getLocation();

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
	}
	
		
	// when enemy is killed by player
	@EventHandler
	public void enemydeath (EntityDeathEvent event) {	
        Random randomnum = new Random();        							
		List <Player> players;		    	    	
        
		if (event.getEntity().getKiller() != null) {
			players = event.getEntity().getKiller().getWorld().getPlayers();		    	    	
			
			Player player = (Player) event.getEntity().getKiller();
			
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
			
			//Array for monster ids that are spawned in
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
				if(event.getEntity().getType().getTypeId() == 53) {
					plugin.monsterskilled.put("killed", plugin.monsterskilled.get("killed") + spawnNum);
				} 
				else {
				plugin.monsterskilled.put("killed", plugin.monsterskilled.get("killed") + 1);	
				
		        for (int i = 0; i < plugin.playersready.size(); i++) {
		        	players.get(i).sendMessage(plugin.monsterskilled.get("killed") + " monsters have been killed!");
		        	}
				}
			}
			
			if ((plugin.monsterskilled.get("killed") % plugin.monsterskilled.get("killtospawn")) == 0 || event.getEntity().getType().getTypeId() == 53) {
		        plugin.monsterskilled.put("wave", plugin.monsterskilled.get("wave") + 1);
		    	
		        //Logger for wave incrementation
		        plugin.logger.info("The current wave is " + (plugin.monsterskilled.get("wave") + 1));
		        
		    	// Reward for player
		        if (plugin.monsterskilled.get("wave") == 1) {
		        	player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		        	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_FIRE, 1);
                }               
                if (plugin.monsterskilled.get("wave") == 2) {
                	player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		        	player.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_FIRE, 1);
                }
                if (plugin.monsterskilled.get("wave") == 3) {
                	player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		        	player.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_FIRE, 1);
                }               
                if (plugin.monsterskilled.get("wave") == 4) {
                	player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		        	player.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_FIRE, 1);
                }		        
                if (plugin.monsterskilled.get("wave") == 5) {
                	ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
                	weapon.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                	player.getInventory().addItem(weapon);
		        	player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 5));
                }
                if (plugin.monsterskilled.get("wave") == 6) {
                	player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
                	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_FIRE, 2);
                }
                if (plugin.monsterskilled.get("wave") == 7) {
                	player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
		        	player.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_FIRE, 2);
                }
                if (plugin.monsterskilled.get("wave") == 8) {
                	player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
		        	player.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_FIRE, 2);
                }               
                if (plugin.monsterskilled.get("wave") == 9) {
                	player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
		        	player.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_FIRE, 2);
                }		        
                if (plugin.monsterskilled.get("wave") == 10) {
                	ItemStack weapon = new ItemStack(Material.BOW);
                	weapon.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                	player.getInventory().addItem(weapon);
                	player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 5));
                }
                if (plugin.monsterskilled.get("wave") == 11) {
                	player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_FIRE, 3);
                }
                if (plugin.monsterskilled.get("wave") == 12) {
                	player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		        	player.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_FIRE, 3);
                }
                if (plugin.monsterskilled.get("wave") == 13) {
                	player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		        	player.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_FIRE, 3);
                }               
                if (plugin.monsterskilled.get("wave") == 14) {
                	player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
		        	player.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_FIRE, 3);
                }		        
                if (plugin.monsterskilled.get("wave") == 15) {
                	ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
                	weapon.addEnchantment(Enchantment.DAMAGE_ALL, 3);
                	weapon.addEnchantment(Enchantment.KNOCKBACK, 2);
                	player.getInventory().addItem(weapon);
                	player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 5));
                }
                if (plugin.monsterskilled.get("wave") == 16) {
                	player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_FIRE, 4);
                	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                }
                if (plugin.monsterskilled.get("wave") == 17) {
                	player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		        	player.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_FIRE, 4);
                	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                }
                if (plugin.monsterskilled.get("wave") == 18) {
                	player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		        	player.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_FIRE, 4);
                	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                }               
                if (plugin.monsterskilled.get("wave") == 19) {
                	player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		        	player.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_FIRE, 4);
                	player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                }		        
                if (plugin.monsterskilled.get("wave") == 20) {
                	ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
                	weapon.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                	weapon.addEnchantment(Enchantment.KNOCKBACK, 2);
                	player.getInventory().addItem(weapon);
                	player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 5));
                }
                if (plugin.monsterskilled.get("wave") > 20) {
                	player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 5));
                }
                if(plugin.monsterskilled.get("wave") > 20 && plugin.monsterskilled.get("wave") % 5 == 0) {
                	ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
                	weapon.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                	weapon.addEnchantment(Enchantment.KNOCKBACK, 2);
                	weapon.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 5);
                	weapon.addEnchantment(Enchantment.DAMAGE_UNDEAD, 5);
                	player.getInventory().addItem(weapon);
                }
                
		    	// Whenever a new wave starts, any world events are removed until triggered again
		        plugin.worldvariables.put("torrent", false);
				plugin.worldvariables.put("lightning", false); 		
				plugin.worldvariables.put("teleport", false); 		        				
				player.getWorld().setThundering(false);
				player.getWorld().setStorm(false);			
		        
		        // world settings changed here
		        if (plugin.monsterskilled.get("wave") % 5 == 0 || plugin.monsterskilled.get("wave") % 2 == 0 || 
		        		plugin.monsterskilled.get("wave") % 13 == 0) {
		        	if (randomnum.nextInt(100) > 40) {
		        		worldevents(plugin.monsterskilled.get("wave"), event.getEntity().getKiller(), players);
		        	}
		        }	
		        
				// abilities added here
		        // all players share the opportunity to use a command
		        if (plugin.monsterskilled.get("wave") % 5 == 0) {
		        	plugin.monsterskilled.put("heal", plugin.monsterskilled.get("heal") + 1); 
			        for (int i = 0; i < plugin.playersready.size(); i++) {
			        	players.get(i).sendMessage("For one time, a player can fully heal with /random heal");
		        	}
				}
		        	
		        if (plugin.monsterskilled.get("wave") % 2 == 0) {
		        	plugin.monsterskilled.put("wolfsummon", plugin.monsterskilled.get("wolfsummon") + 1); 
			        for (int i = 0; i < plugin.playersready.size(); i++) {
			        	players.get(i).sendMessage("For one time, a player can summon a tamed wolf with /random wolf");
			        }							        	
		        }
		        if (plugin.monsterskilled.get("wave") % 6 == 0) {
		        	plugin.monsterskilled.put("golemsummon", plugin.monsterskilled.get("golemsummon") + 1); 
			        for (int i = 0; i < plugin.playersready.size(); i++) {
			        	players.get(i).sendMessage("For one time, a player can summon an Iron Goem with /random golem");
			        }
		        }		        
							
				Location loc = player.getLocation();
		        
				for (int i = 0; i < spawnNum; i++) {       					
					loc.setX(randomnum.nextInt(((plugin.arenacoordinates.get("x2"))
							- (plugin.arenacoordinates.get("x1")))) 
							+ plugin.arenacoordinates.get("x1") + 1);
					
					loc.setZ(randomnum.nextInt(((plugin.arenacoordinates.get("z2"))
							- (plugin.arenacoordinates.get("z1")))) 
							+ plugin.arenacoordinates.get("z1") + 1);	

					loc.setY(loc.getWorld().getHighestBlockYAt(loc));	
					
					if(plugin.monsterskilled.get("wave") % 5 != 0) {
						player.getWorld().spawnEntity(loc, EntityType.fromId(monsterIds[randomnum.nextInt(10)]));						
					}
					else if (plugin.monsterskilled.get("wave") % 5 == 0) {
						player.getWorld().spawnEntity(loc, EntityType.fromId(53));						
						spawnNum = 1;						
					}
					
					
				}			
			}
		}
	}
    
    
    // sets worldly events upon start of plugin
    @EventHandler
    public void worldset(PluginEnableEvent event) {
        plugin.worldvariables.put("arenaset", false); 
        plugin.worldvariables.put("started", false);
		plugin.worldvariables.put("torrent", false);
		plugin.worldvariables.put("lightning", false); 
		plugin.worldvariables.put("teleport", false);        				
        plugin.monsterskilled.put("wave", 0);
        plugin.monsterskilled.put("killed", 0);   
        plugin.monsterskilled.put("wolfsummon", 0);
        plugin.monsterskilled.put("golemsummon", 0);
        plugin.monsterskilled.put("heal", 0);        
    }
    
    // world events decided here
    public void worldevents(int wavenumber, Player player, List <Player> players) {   
    	// Rainfall is classified as violent when the precipitation rate is greater than "5"0 mm per hour!
    	if (wavenumber % 5 == 0) {     		
	        for (int i = 0; i < plugin.playersready.size(); i++) {
	        	players.get(i).sendMessage("A torrent has started!");
	        }
    		
    		player.getWorld().setStorm(true);
    		plugin.worldvariables.put("torrent", true);
    	} 
    	
    	// Lightning strikes are the number 2 weather killer in the US!
    	if (wavenumber % 2 == 0) {     		
	        for (int i = 0; i < plugin.playersready.size(); i++) {
	        	players.get(i).sendMessage("Beware of Thunder!");
	        }
    		
    		player.getWorld().setStorm(true);
    		player.getWorld().setThundering(true);
    		plugin.worldvariables.put("lightning", true);
    	}
    	
    	// How unlucky! ......RIP
    	if (wavenumber % 13 == 0) {     		
	        for (int i = 0; i < plugin.playersready.size(); i++) {
	        	players.get(i).sendMessage("Suddenly, enemies...!");
	        }
    		
    		plugin.worldvariables.put("teleport", true);
    	}    	
    }
    
    // World events that occur
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void worldcontrol (PlayerMoveEvent event) {
		Random randomnum = new Random();
		
		// Be careful, a flood might be coming?!
		if (plugin.worldvariables.get("torrent")) {
			if (randomnum.nextInt(500) < 5) {
				Location loc = event.getPlayer().getLocation();
			        
				loc.setX(randomnum.nextInt(((plugin.arenacoordinates.get("x2")) - (plugin.arenacoordinates.get("x1")))) 
							+ plugin.arenacoordinates.get("x1") + 1);					
				loc.setZ(randomnum.nextInt(((plugin.arenacoordinates.get("z2")) - (plugin.arenacoordinates.get("z1")))) 
							+ plugin.arenacoordinates.get("z1") + 1);	
				loc.setY(loc.getWorld().getHighestBlockYAt(loc));			
				
				loc.getBlock().setType(Material.WATER);
			}								
		}
		
		// Not that you can dodge it, but beware of lightning!
		if (plugin.worldvariables.get("lightning")) {
			if (randomnum.nextInt(500) < 4) {
				Location loc = event.getPlayer().getLocation();				
				
				loc.getWorld().strikeLightning(loc);
			}								
		}		
	}
	
	@EventHandler
	public void playerdeath (PlayerDeathEvent event) {
		// resets player to not ready when they die
		plugin.playersready.put(event.getEntity(), false);
	}
	
	@EventHandler
	public void enemytarget (EntityTargetEvent event) {
		// entities teleport at player when they target player
		if (plugin.worldvariables.get("teleport")) {
			Location loc = event.getTarget().getLocation();
			loc.setX(loc.getX() - 2);
			loc.setZ(loc.getZ() - 2);
			loc.setY(loc.getWorld().getHighestBlockYAt(loc));
			
			event.getEntity().teleport(loc);
		}
	}	
}
