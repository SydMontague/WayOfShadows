package com.syd.wayofshadows;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WayOfShadows extends JavaPlugin
{
    public Logger log = Logger.getLogger("Minecraft");
    public PluginEntityListener listener = new PluginEntityListener(this);
    public GrapplingHook grapple = new GrapplingHook(this);
    FileConfiguration config;
    
    
    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(listener, this);
        pm.registerEvents(grapple, this);

        config = getConfig();
        if (!new File(this.getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
            saveDefaultConfig();
        
        log.info("[BackStab] enabled");
    }

    public void onDisable()
    {
        log.info("[BackStab] disabled");
    }

    //config reader
    public List<Integer> getDmgItems()
    {
        return config.getIntegerList("backstab.items");
    }

    public List<Integer> getPoisonItems()
    {
        return config.getIntegerList("poison.items");
    }
    
    public double getMultiplier()
    {
        return config.getDouble("backstab.multiplier", 1.5);
    }

    public double getDmgChance()
    {
        return config.getDouble("backstab.chance", 1);
    }

    public double getPoisonChance()
    {
        return config.getDouble("poison.chance", 1);
    }

    public int getPoisonDuration()
    {
        return config.getInt("poison.duration", 5) * 20;
    }

    public boolean getDmgSneak()
    {
        return config.getBoolean("backstab.onsneak", false);
    }

    public boolean getPoisonSneak()
    {
        return config.getBoolean("poison.onsneak", false);
    }

    public double getCritMultiplier()
    {
        return config.getDouble("backstab.critmultiplier", 3.0);
    }

    public boolean getCritSneak()
    {
        return config.getBoolean("backstab.critonsneak", true);
    }

    public double getCritChance()
    {
        return config.getDouble("backstab.critchance", 0.01);
    }

    public int getPullItem()
    {
        return config.getInt("hook.pullitem", 287);
    }
    
    public long getBlockTime()
    {
        return config.getLong("hook.blocktime", 5) * 20;
    }

    public double getPullTimeMod()
    {
        return config.getDouble("hook.pulltimemod", 0.33);
    }

    public double getMaxDistance()
    {
        return config.getInt("hook.maxdistance", 100);
    }
}