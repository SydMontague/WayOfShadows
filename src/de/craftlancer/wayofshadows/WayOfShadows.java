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

//TODO call SkillLevels on Skill execution (for XP rewards)
//TODO check EventHandlers for their priorities
//TODO add javaDocs
//TODO pickpocket for chests - low prio
//TODO update internal config.yml
//TOTEST everything! (especially ValueCatalogue)
public class WayOfShadows extends JavaPlugin
{
    public Logger log;
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
        
        if (pm.getPlugin("SkillLevels") != null)
            slevel = (SkillLevels) pm.getPlugin("SkillLevels");
        
        if (!new File(getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
            saveDefaultConfig();
        
        valueConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "values.yml"));
        
        for (String key : valueConfig.getKeys(false))
            valCatalogue.put(key, new ValueCatalogue(this, valueConfig, key));
        
        config = getConfig();
        
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
    
    public SkillLevels getSkillLevels()
    {
        return slevel;
    }
    
    public ValueCatalogue getValueCatalogue(String string)
    {
        return valCatalogue.get(string);
    }
    
    public int getLevel(Player p, String levelSystem)
    {
        return getSkillLevels() != null ? getSkillLevels().getUserLevel(levelSystem, p.getName()) : 0;
    }
}
