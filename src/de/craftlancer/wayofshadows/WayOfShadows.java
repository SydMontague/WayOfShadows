package de.craftlancer.wayofshadows;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

//TODO air assassination
//TODO pickpocket
//TODO use Entity#getType().equals(EntityType) instead of instanceof
//TODO define skill items via name/lore
public class WayOfShadows extends JavaPlugin
{
    public Logger log;
    public BackStab backstab = new BackStab(this);
    public EffectSkill effectskill = new EffectSkill(this);
    public GrapplingHook grapple = new GrapplingHook(this);
    public PickPocket pickpocket = new PickPocket(this);
    private FileConfiguration config;
    public Map<String, BackstabItem> backstabItem = new HashMap<String, BackstabItem>();
    public Map<String, List<ItemEffect>> effectItem = new HashMap<String, List<ItemEffect>>();
    
    @Override
    public void onEnable()
    {
        log = getLogger();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(backstab, this);
        pm.registerEvents(effectskill, this);
        pm.registerEvents(grapple, this);
        pm.registerEvents(pickpocket, this);
        
        if (!new File(getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
            saveDefaultConfig();
        
        config = getConfig();
        
        loadBackstabItems();
        loadPoisonItems();
    }
    
    @Override
    public void onDisable()
    {
        config = null;
        getServer().getScheduler().cancelTasks(this);
    }
    
    private void loadPoisonItems()
    {
        String path = "poison.items";
        
        for (String key : config.getConfigurationSection(path).getKeys(false))
        {
            List<ItemEffect> list = new ArrayList<ItemEffect>();
            for (String effect : config.getConfigurationSection(path + "." + key).getKeys(false))
            {                
                String npath = path + "." + key + "." + effect;
                ItemEffect i = new ItemEffect();
                i.type = PotionEffectType.getById(config.getInt(npath + ".type")) != null ? PotionEffectType.getById(config.getInt(npath + ".type")) : PotionEffectType.getByName(config.getString(npath + ".type"));
                i.chance = config.getDouble(npath + ".chance", 0.0D);
                i.duration = config.getInt(npath + ".duration", 0) * 20;
                i.strength = config.getInt(npath + ".strength", 0);
                i.sneak = config.getBoolean(npath + ".onsneak", false);
                i.angle = config.getDouble(npath + ".maxangle", 0.0D);
                list.add(i);
            }
            effectItem.put(key, list);
        }
    }
    
    private void loadBackstabItems()
    {
        String path = "backstab.items";
        
        for (String key : config.getConfigurationSection(path).getKeys(false))
        {
            BackstabItem i = new BackstabItem();
            final String npath = path + "." + key;
            i.chance = config.getDouble(npath + ".chance", 0.0D);
            i.critchance = config.getDouble(npath + ".critchance", 0.0D);
            i.critmultiplier = config.getDouble(npath + ".critmultiplier", 0.0D);
            i.critsneak = config.getBoolean(npath + ".critonsneak", false);
            i.multiplier = config.getDouble(npath + ".multiplier", 0.0D);
            i.sneak = config.getBoolean(npath + ".onsneak", false);
            i.angle = config.getDouble(npath + ".maxangle", 0.0D);
            backstabItem.put(key, i);
        }
    }
    
    public String getAttackerMsg()
    {
        return setColored(config.getString("backstab.attackermsg", "Backstab!"));
    }
    
    public String getVictimMsg()
    {
        return setColored(config.getString("backstab.victimmsg", "You got stabbed in the back!"));
    }
    
    public String getCritAttackerMsg()
    {
        return setColored(config.getString("backstab.critmsg", "CRITICAL!"));
    }
    
    public int getPullItem()
    {
        return config.getInt("hook.pullitem", 287);
    }
    
    public long getBlockTime()
    {
        return config.getLong("hook.blocktime", 5) * 20;
    }
    
    public double getMaxDistance()
    {
        return config.getInt("hook.maxdistance", 100);
    }
    
    public double getMaxDistance2()
    {
        return config.getInt("hook.distancetoinitial", 10);
    }
    
    public double getStringPerBlock()
    {
        return config.getDouble("hook.itemsperblock", 0.01);
    }
    
    public String getHookErrorMsg()
    {
        return setColored(config.getString("hook.errormsg", "You can't hook there!"));
    }
    
    public String getHookInitialMsg()
    {
        return setColored(config.getString("hook.initialmsg", "You are to far away from your initial location!"));
    }
    
    public String getHookDistanceMsg()
    {
        return setColored(config.getString("hook.distancemsg", "Your hook is to far away!"));
    }
    
    public int getHookItem()
    {
        return config.getInt("hook.hookitem", 262);
    }
    
    public static String setColored(String string)
    {
        string = string.replace("&0", ChatColor.BLACK.toString());
        string = string.replace("&1", ChatColor.DARK_BLUE.toString());
        string = string.replace("&2", ChatColor.DARK_GREEN.toString());
        string = string.replace("&3", ChatColor.DARK_AQUA.toString());
        string = string.replace("&4", ChatColor.DARK_RED.toString());
        string = string.replace("&5", ChatColor.DARK_PURPLE.toString());
        string = string.replace("&6", ChatColor.GOLD.toString());
        string = string.replace("&7", ChatColor.GRAY.toString());
        string = string.replace("&8", ChatColor.DARK_GRAY.toString());
        string = string.replace("&9", ChatColor.BLUE.toString());
        string = string.replace("&a", ChatColor.GREEN.toString());
        string = string.replace("&b", ChatColor.AQUA.toString());
        string = string.replace("&c", ChatColor.RED.toString());
        string = string.replace("&d", ChatColor.LIGHT_PURPLE.toString());
        string = string.replace("&e", ChatColor.YELLOW.toString());
        string = string.replace("&f", ChatColor.WHITE.toString());
        string = string.replace("&k", ChatColor.MAGIC.toString());
        string = string.replace("&l", ChatColor.BOLD.toString());
        string = string.replace("&m", ChatColor.STRIKETHROUGH.toString());
        string = string.replace("&n", ChatColor.UNDERLINE.toString());
        string = string.replace("&o", ChatColor.ITALIC.toString());
        string = string.replace("&r", ChatColor.RESET.toString());
        return string;
    }
}

class BackstabItem
{
    public double multiplier = 1.0;
    public double chance = 0.0;
    public boolean sneak = false;
    public double critmultiplier = 1.0;
    public double critchance = 0.0;
    public boolean critsneak = false;
    public double angle = 0;
}

class ItemEffect
{
    public double angle = 0.0;
    public PotionEffectType type = null;
    public double chance = 0.0;
    public int duration = 0;
    public int strength = 0;
    public boolean sneak = false;
}
