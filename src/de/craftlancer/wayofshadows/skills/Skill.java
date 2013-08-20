package de.craftlancer.wayofshadows.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

public abstract class Skill implements Listener
{
    protected WayOfShadows plugin;
    
    private String name;
    private List<String> lore;
    private List<Integer> items;
    private List<String> itemNames;
    private String levelSystem;
    private String cooldownMsg;
    private ValueWrapper cooldown;
    
    public Skill(WayOfShadows instance, String key)
    {
        plugin = instance;
        name = key;
        lore = instance.getConfig().getStringList(key + ".lore");
        items = instance.getConfig().getIntegerList(key + ".items");
        itemNames = instance.getConfig().getStringList(key + ".names");
        levelSystem = instance.getConfig().getString(key + ".levelSystem");
        cooldown = new ValueWrapper(instance.getConfig().getString(key + ".cooldown", "0"));
        cooldownMsg = instance.getConfig().getString(key + ".cooldownMsg", "This skill for %time% seconds on cooldown!");
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
    
    public String getLevelSys()
    {
        return levelSystem;
    }
    
    public String getCooldownMsg(Player p)
    {
        return cooldownMsg.replace("%time%", String.valueOf(getRemainingCooldown(p)));
    }
    
    public boolean isOnCooldown(Player p)
    {
        return p.hasMetadata("cooldown." + getName()) && p.getMetadata("cooldown." + getName()).get(0).asLong() >= System.currentTimeMillis();
    }
    
    public void setOnCooldown(Player p)
    {
        long time = System.currentTimeMillis();
        int level = plugin.getLevel(p, levelSystem);
        p.setMetadata("cooldown." + getName(), new FixedMetadataValue(plugin, time + (cooldown.getValue(level) * 1000)));
    }
    
    public double getRemainingCooldown(Player p)
    {
        return (p.getMetadata("cooldown." + getName()).get(0).asLong() - System.currentTimeMillis()) / 1000;
    }
    
    public boolean hasPermission(Player p, ItemStack item)
    {
        if (p.hasPermission("shadow." + getName()))
            return true;
        
        if (p.hasPermission("shadow." + getName() + "." + item.getTypeId()))
            return true;
        
        if (item.hasItemMeta())
        {
            if (p.hasPermission("shadow." + getName() + "." + item.getItemMeta().getDisplayName()))
                return true;
            if (item.getItemMeta().hasLore())
                for (String str : getLore())
                    for (String str2 : item.getItemMeta().getLore())
                        if (str2.contains(str) && p.hasPermission("shadow." + getName() + "." + str))
                            return true;
        }
        
        return false;
    }
    
    public boolean isSkillItem(ItemStack item)
    {
        if (getItemIds().isEmpty() && getItemNames().isEmpty() && getLore().isEmpty())
            return true;
        
        if (getItemIds().contains(item.getTypeId()))
            return true;
        
        if (item.hasItemMeta())
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
        config.set(getName() + ".levelSystem", getLevelSys());
    }
}
