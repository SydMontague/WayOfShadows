package com.syd.wayofshadows;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WayOfShadows extends JavaPlugin
{
    public Logger log;
    public Backstab listener = new Backstab(this);
    public GrapplingHook grapple = new GrapplingHook(this);
    FileConfiguration config;
    
    
    public void onEnable()
    {
        log = getLogger();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(listener, this);
        pm.registerEvents(grapple, this);

        if (!new File(this.getDataFolder().getPath() + File.separatorChar + "config.yml").exists())
            saveDefaultConfig();   
        
        config = getConfig();     
    }

    public void onDisable()
    {
        config = null;
    }

    //config reader
    public List<Integer> getDmgItems()
    {
        return config.getIntegerList("backstab.items");
    }
    
    public double getMultiplier()
    {
        return config.getDouble("backstab.multiplier", 1.5);
    }

    public double getDmgChance()
    {
        return config.getDouble("backstab.chance", 1.0);
    }
    
    public boolean getDmgSneak()
    {
        return config.getBoolean("backstab.onsneak", false);
    }

    public String getAttackerMsg()
    {
        return config.getString("backstab.attackermsg", "Backstab!");
    }  
    
    public String getVictimMsg()
    {
        return config.getString("backstab.victimmsg", "You got stabbed in the back!");
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
    
    public String getCritAttackerMsg()
    {
        return config.getString("backstab.critmsg", "CRITICAL!");
    }    

    public List<Integer> getPoisonItems()
    {
        return config.getIntegerList("poison.items");
    }
    
    public double getPoisonChance()
    {
        return config.getDouble("poison.chance", 1.0);
    }

    public int getPoisonDuration()
    {
        return config.getInt("poison.duration", 5) * 20;
    }
    
    public int getPoisonStrength()
    {
        return config.getInt("poison.strength", 0);
    }

    public boolean getPoisonSneak()
    {
        return config.getBoolean("poison.onsneak", false);
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
        return config.getString("hook.errormsg", "You can't hook there!");
    }

    public String getHookInitialMsg()
    {
        return config.getString("hook.initialmsg", "You are to far away from your initial location!");
    }

    public String getHookDistanceMsg()
    {
        return config.getString("hook.distancemsg", "Your hook is to far away!");
    }

    public int getHookItem()
    {
        return config.getInt("hook.hookitem", 262);
    }
}