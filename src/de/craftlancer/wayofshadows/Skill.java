package de.craftlancer.wayofshadows;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class Skill implements Listener
{
    protected WayOfShadows plugin;
    
    private String name;
    private List<String> lore;
    private List<Integer> items;
    private List<String> itemNames;
    
    public Skill(WayOfShadows instance, String key)
    {
        plugin = instance;
        name = key;
        lore = instance.getConfig().getStringList(key + ".lore");
        items = instance.getConfig().getIntegerList(key + ".items");
        itemNames = instance.getConfig().getStringList(key + ".names");
    }
    
    public Skill(WayOfShadows instance, String key, String item)
    {
        plugin = instance;
        name = key;
        
        lore = new ArrayList<String>();
        itemNames = new ArrayList<String>();
        items = new ArrayList<Integer>();
        
        items.add(Integer.parseInt(item));
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<String> getLore()
    {
        return lore;
    }
    
    public List<String> getItemNames()
    {
        return itemNames;
    }
    
    public List<Integer> getItemIds()
    {
        return items;
    }
    
    public double getAngle(Vector vec1, Vector vec2)
    {
        return vec1.angle(vec2) * 180 / Math.PI;
    }
    
    public boolean hasPermission(Player p, ItemStack item)
    {
        if (p.hasPermission("shadow." + getName()))
            return true;
        
        if (p.hasPermission("shadow." + getName() + "." + item.getTypeId()))
            return true;
        
        if (p.hasPermission("shadow." + getName() + "." + item.getItemMeta().getDisplayName()))
            return true;
        
        if (item.getItemMeta().hasLore())
            for (String str : getLore())
                for (String str2 : item.getItemMeta().getLore())
                    if (str2.contains(str) && p.hasPermission("shadow." + getName() + "." + str))
                        return true;
        
        return false;
    }
    
    public boolean isSkillItem(ItemStack item)
    {
        if (getItemIds().contains(item.getTypeId()))
            return true;
        
        if (!item.hasItemMeta())
            if (item.getItemMeta().hasDisplayName() && getItemNames().contains(item.getItemMeta().getDisplayName()))
                return true;
            else if (item.getItemMeta().hasLore())
                for (String str : getLore())
                    for (String str2 : item.getItemMeta().getLore())
                        if (str2.contains(str))
                            return true;
        
        return false;
    }
    
    public void save(FileConfiguration config)
    {
        config.set(getName() + ".lore", lore);
        config.set(getName() + ".items", items);
        config.set(getName() + ".itemNames", itemNames);
    }
}
