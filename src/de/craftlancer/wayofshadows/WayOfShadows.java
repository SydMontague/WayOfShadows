package de.craftlancer.wayofshadows;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.skilllevels.SkillLevels;
import de.craftlancer.wayofshadows.metrics.Metrics;
import de.craftlancer.wayofshadows.updater.Updater04to05;

//TODO air assassination
//TODO pickpocket
public class WayOfShadows extends JavaPlugin
{
    public Logger log;
    private FileConfiguration config;
    
    private Set<Skill> skills = new HashSet<Skill>();
    private SkillLevels slevel = null;
    
    @Override
    public void onEnable()
    {
        log = getLogger();
        PluginManager pm = getServer().getPluginManager();
        
        if (pm.getPlugin("SkillLevels") != null)
            slevel = (SkillLevels) pm.getPlugin("SkillLevels");
        
        if (!new File(getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
            saveDefaultConfig();
        
        config = getConfig();
        
        if (!config.isSet("configVersion"))
            new Updater04to05(this).update();
        
        for (String key : config.getKeys(false))
        {
            if (key.equalsIgnoreCase("configVersion"))
                continue;
            
            Skill s = SkillFactory.createSkill(key, this);
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
}
