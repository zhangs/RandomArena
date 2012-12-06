package szhang_unca_edu.RandomArena;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/*Project by s_beta, powershot06, and insomniaensues
 * 
 * 
 * This is the main class of the sample plug-in
 */
public class RandomArena extends JavaPlugin {	
	//Initial variable declaration
	RandomArenaLogger logger;
	public Map <String, Integer> arenacoordinates;
	public Map <String, Integer> monsterskilled;
	public Map <Player, Boolean> playersready;
	public Map <String, Boolean> worldvariables;
	public Map <String, Player> arenasetter;


    /*
     * This is called when your plug-in is enabled
     */
    @Override
    public void onEnable() {
		arenacoordinates = new HashMap<String, Integer>();	
		monsterskilled = new HashMap<String, Integer>();	
		playersready = new HashMap<Player, Boolean>();	
		worldvariables = new HashMap<String, Boolean>();
		arenasetter = new HashMap<String, Player>();
		
		//Initiates logger
		logger = new RandomArenaLogger(this);
		logger.info("plugin enabled");

        // save the configuration file
        saveDefaultConfig();
        
        // Create the SampleListener
        new RandomArenaListener(this);
        
        // set the command executor for sample
        this.getCommand("random").setExecutor(new RandomArenaCommandExecutor(this));
    }
    
    /*
     * This is called when your plug-in shuts down
     */
    @Override
    public void onDisable() {
    	 logger.info("plugin disabled");
        
    }
    	//unused
	public void setMetadata(Player player, String key, Object value, RandomArena plugin) {
		player.setMetadata(key, new FixedMetadataValue(plugin, value));
	}
		
	public Object getMetadata(Player player, String key, RandomArena plugin) {
		List<MetadataValue> values = player.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin().getDescription().getName()
			.equals(plugin.getDescription().getName())) {
			return (value.asBoolean());
			}
		}
		return null;
	}

}
