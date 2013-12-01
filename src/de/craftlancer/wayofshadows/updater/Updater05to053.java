package de.craftlancer.wayofshadows.updater;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import de.craftlancer.wayofshadows.ValueCatalogue;
import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.skills.Skill;

public class Updater05to053
{
    private WayOfShadows plugin;
    
    public Updater05to053(WayOfShadows plugin)
    {
        this.plugin = plugin;
    }
    
    public void update()
    {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration valueConfig = plugin.getValueConfig();
        
        for (Skill s : plugin.getSkills())
            s.save(config);
        
        for (String s : valueConfig.getKeys(false))
            valueConfig.set(s, null);
        
        for (ValueCatalogue v : plugin.getValueCatalogues())
            v.save(valueConfig);
        
        config.set("configVersion", 2);
        plugin.saveConfig();
        try
        {
            valueConfig.save(new File(plugin.getDataFolder(), "values.yml"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
}
