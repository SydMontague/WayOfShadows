package de.craftlancer.wayofshadows;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.skilllevels.SkillLevels;
import de.craftlancer.wayofshadows.metrics.Metrics;
import de.craftlancer.wayofshadows.skills.Skill;
import de.craftlancer.wayofshadows.updater.Updater04to05;
import de.craftlancer.wayofshadows.utils.SkillFactory;

//TODO pickpocket for chests - low prio
//TODO reload command
public class WayOfShadows extends JavaPlugin
{
    private Logger log;
    private FileConfiguration config;
    private FileConfiguration valueConfig;
    
    private Set<Skill> skills = new HashSet<Skill>();
    private SkillLevels slevel = null;
    private Map<String, ValueCatalogue> valCatalogue = new HashMap<String, ValueCatalogue>();
    
    @Override
    public void onEnable()
    {
        log = getLogger();
        PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("SkillLevels") != null && pm.getPlugin("SkillLevels").isEnabled())
        {
            slevel = (SkillLevels) pm.getPlugin("SkillLevels");
            pm.registerEvents(new SkillLevelsManager(slevel), this);
        }
        
        pm.registerEvents(new ShadowListener(this), this);
        
        if (!new File(getDataFolder(), "config.yml").exists())
            saveDefaultConfig();
        
        if (!new File(getDataFolder(), "values.yml").exists())
            saveResource("values.yml", false);
        
        valueConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "values.yml"));
        config = getConfig();
        
        for (String key : valueConfig.getKeys(false))
            valCatalogue.put(key, new ValueCatalogue(this, valueConfig, key));
        
        if (!config.isSet("configVersion"))
            new Updater04to05(this).update();
        
        for (String key : config.getKeys(false))
        {
            if (key.equalsIgnoreCase("configVersion"))
                continue;
            
            Skill s = SkillFactory.createSkill(key, this);
            if (s == null)
                continue;
            skills.add(s);
            pm.registerEvents(s, this);
        }
        
        try
        {
            Metrics metrics = new Metrics(this);
            metrics.start();
        }
        catch (IOException e)
        {
        }
    }
    
    @Override
    public void onDisable()
    {
        config = null;
        getServer().getScheduler().cancelTasks(this);
    }
    
    /**
     * Get the instance of the SkillLevels plugin.
     * 
     * @return the instance of SkillLevels
     */
    public SkillLevels getSkillLevels()
    {
        return slevel;
    }
    
    /**
     * Get the ValueCatalogue, which is mapped to the string
     * 
     * @param string - the name of the requested catalogue
     * @return the catalogue which is mappes to string
     */
    public ValueCatalogue getValueCatalogue(String string)
    {
        return valCatalogue.get(string);
    }
    
    /**
     * Get the level of a player in a certain level system
     * 
     * @param p - the player the level is calculated of
     * @param levelSystem - the name of the system
     * @return the level of the player in the given system or 0 if the plugin is
     *         not loaded,
     *         or the player is not registered in this system
     */
    public int getLevel(Player p, String levelSystem)
    {
        return getSkillLevels() != null ? getSkillLevels().getUserLevel(levelSystem, p.getName()) : 0;
    }
    
    /**
     * Just a wrapper for log.severe();
     * 
     * @param s - the message, which should be given out
     */
    public void error(String s)
    {
        log.severe(s);
    }
}
