package de.craftlancer.wayofshadows;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ValueCatalogue
{
    private String catalogueName;
    private int defaultValue;
    private Map<Material, Integer> itemValues = new HashMap<Material, Integer>();
    private Map<String, Integer> nameValues = new HashMap<String, Integer>();
    private Map<String, Integer> loreValues = new HashMap<String, Integer>();
    private Map<String, Map<Integer, Integer>> enchantmentValues = new HashMap<String, Map<Integer, Integer>>();
    
    /**
     * Create a new ValueCatalogue
     * 
     * @param instance
     *        - the instance of the plugin, the catalog is created
     *        for
     * @param config
     *        - the config, where the catalog is saved in
     * @param catalogueName
     *        - the name of the catalog, which is used in the
     *        config
     */
    public ValueCatalogue(WayOfShadows instance, FileConfiguration config, String catalogueName)
    {
        this.catalogueName = catalogueName;
        defaultValue = config.getInt(catalogueName + ".defaultValue", -1);
        
        if (config.isConfigurationSection(catalogueName + ".items"))
            for (String key : config.getConfigurationSection(catalogueName + ".items").getKeys(false))
            {
                Material mat = Material.matchMaterial(key);
                
                if (mat == null)
                    instance.error("A itemkey in valueCatalogue \"" + catalogueName + "\" is no valid Material.");
                else
                    itemValues.put(mat, config.getInt(catalogueName + ".items." + key));
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
     * Get the value of the item, according to this catalog based on item type,
     * name, lore and enchantment.
     * 
     * @param item
     *        - the item you want the value of
     * @return the value of the item. If no property of the item matches with
     *         the catalog it returns 0;
     */
    public int getValue(ItemStack item)
    {
        int value = 0;
        
        if (itemValues.containsKey(item.getType().name()))
            value += itemValues.get(item.getType().name());
        
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
        
        return value == 0 ? getDefaultValue() : value;
    }
    
    public int getDefaultValue()
    {
        return defaultValue;
    }
    
    /**
     * Get the name of the catalog
     * 
     * @return the name of the catalog
     */
    public String getCatalogueName()
    {
        return catalogueName;
    }
    
    public boolean canSteal(ItemStack item)
    {
        return getValue(item) >= 0;
    }
    
    public void save(FileConfiguration config)
    {
        config.set(catalogueName + ".defaultValue", defaultValue);
        
        for (Entry<Material, Integer> item : itemValues.entrySet())
            config.set(catalogueName + ".items." + item.getKey().name(), item.getValue());
        
        for (Entry<String, Integer> item : nameValues.entrySet())
            config.set(catalogueName + ".names." + item.getKey(), item.getValue());
        
        for (Entry<String, Integer> item : loreValues.entrySet())
            config.set(catalogueName + ".lore." + item.getKey(), item.getValue());
        
        for (Entry<String, Map<Integer, Integer>> item : enchantmentValues.entrySet())
            for (Entry<Integer, Integer> value : item.getValue().entrySet())
                config.set(catalogueName + ".enchantments." + item.getKey() + "." + value.getKey(), value.getValue());
    }
}
