package de.craftlancer.wayofshadows;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ValueCatalogue
{
    private String catalogueName;
    private Map<Integer, Integer> itemValues = new HashMap<Integer, Integer>();
    private Map<String, Integer> nameValues = new HashMap<String, Integer>();
    private Map<String, Integer> loreValues = new HashMap<String, Integer>();
    private Map<String, Map<Integer, Integer>> enchantmentValues = new HashMap<String, Map<Integer, Integer>>();
    
    /**
     * Create a new ValueCatalogue
     * 
     * @param instance - the instance of the plugin, the catalogue is created
     *            for
     * @param config - the config, where the catalogue is saved in
     * @param catalogueName - the name of the catalogue, which is used in the
     *            config
     */
    public ValueCatalogue(WayOfShadows instance, FileConfiguration config, String catalogueName)
    {
        this.catalogueName = catalogueName;
        
        if (config.isConfigurationSection(catalogueName + ".items"))
            for (String key : config.getConfigurationSection(catalogueName + ".items").getKeys(false))
                try
                {
                    itemValues.put(Integer.parseInt(key), config.getInt(catalogueName + ".items." + key));
                }
                catch (NumberFormatException e)
                {
                    instance.error("A itemkey in valueCatalogue \"" + catalogueName + "\" is not a integer!");
                    continue;
                }
        
        if (config.isConfigurationSection(catalogueName + ".names"))
            for (String key : config.getConfigurationSection(catalogueName + ".names").getKeys(false))
                nameValues.put(key, config.getInt(catalogueName + ".names." + key));
        
        if (config.isConfigurationSection(catalogueName + ".lore"))
            for (String key : config.getConfigurationSection(catalogueName + ".lore").getKeys(false))
                loreValues.put(key, config.getInt(catalogueName + ".lore." + key));
        
        if (config.isConfigurationSection(catalogueName + ".enchantments"))
            for (String key : config.getConfigurationSection(catalogueName + ".enchantments").getKeys(false))
            {
                Map<Integer, Integer> helpmap = new HashMap<Integer, Integer>();
                
                for (String level : config.getConfigurationSection(catalogueName + ".enchantments." + key).getKeys(false))
                    try
                    {
                        helpmap.put(Integer.parseInt(level), config.getInt(catalogueName + ".enchantments." + key + "." + level));
                    }
                    catch (NumberFormatException e)
                    {
                        instance.error("A enchantmentLevel in valueCatalogue \"" + catalogueName + "\" is not a integer!");
                        continue;
                    }
                
                enchantmentValues.put(key, helpmap);
            }
    }
    
    /**
     * Get the value of the item, according to this catalogue based on itemtype,
     * name, lore and enchantments.
     * 
     * @param item - the item you want the value of
     * @return the value of the item. If no property of the item matches with
     *         the catalogue it returns 0;
     */
    public int getValue(ItemStack item)
    {
        int value = 0;
        
        if (itemValues.containsKey(item.getTypeId()))
            value += itemValues.get(item.getTypeId());
        
        if (item.hasItemMeta())
        {
            if (item.getItemMeta().hasDisplayName() && nameValues.containsKey(item.getItemMeta().getDisplayName()))
                value += nameValues.get(item.getItemMeta().getDisplayName());
            
            if (item.getItemMeta().hasLore())
                for (String s : loreValues.keySet())
                    for (String str : item.getItemMeta().getLore())
                        if (str.contains(s))
                            value += loreValues.get(s);
            
            if (item.getItemMeta().hasEnchants())
                for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet())
                    if (enchantmentValues.containsKey(entry.getKey().getName()))
                        if (enchantmentValues.get(entry.getKey().getName()).containsKey(entry.getValue()))
                            value += enchantmentValues.get(entry.getKey().getName()).get(entry.getValue());
        }
        
        return value;
    }
    
    /**
     * Get the name of the catalogue
     * 
     * @return the name of the catalogue
     */
    public String getCatalogueName()
    {
        return catalogueName;
    }
    
    public boolean canSteal(ItemStack item)
    {
        if (itemValues.containsKey(item.getTypeId()))
            return true;
        
        if (item.hasItemMeta())
        {
            if (item.getItemMeta().hasDisplayName() && nameValues.containsKey(item.getItemMeta().getDisplayName()))
                return true;
            
            if (item.getItemMeta().hasLore())
                for (String s : loreValues.keySet())
                    for (String str : item.getItemMeta().getLore())
                        if (str.contains(s))
                            return true;
            
            if (item.getItemMeta().hasEnchants())
                for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet())
                    if (enchantmentValues.containsKey(entry.getKey().getName()))
                        if (enchantmentValues.get(entry.getKey().getName()).containsKey(entry.getValue()))
                            return true;
        }
        return false;
    }
}
