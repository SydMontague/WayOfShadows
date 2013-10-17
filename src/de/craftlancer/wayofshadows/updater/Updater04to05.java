package de.craftlancer.wayofshadows.updater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.skills.BackStab;
import de.craftlancer.wayofshadows.skills.EffectSkill;
import de.craftlancer.wayofshadows.skills.GrapplingHook;
import de.craftlancer.wayofshadows.skills.Skill;

/**
 * Updates a 0.4 config to a 0.5 config
 */
public class Updater04to05
{
    private WayOfShadows plugin;
    private FileConfiguration config;
    
    public Updater04to05(WayOfShadows plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
    }
    
    public void update()
    {
        Map<String, List<ItemEffect>> effectItem = loadPoisonItems();
        Map<String, BackstabItem> backstabItem = loadBackstabItems();
        
        List<Skill> skill = new LinkedList<Skill>();
        
        String attackermsg = config.getString("backstab.attackermsg", "Backstab!");
        String victimmsg = config.getString("backstab.victimmsg", "You got stabbed in the back!");
        String critmsg = config.getString("backstab.critmsg", "CRITICAL!");
        
        for (Entry<String, BackstabItem> e : backstabItem.entrySet())
            skill.add(new BackStab(plugin, "backstab" + e.getKey(), e.getKey(), e.getValue(), attackermsg, victimmsg, critmsg));
        
        for (Entry<String, List<ItemEffect>> e : effectItem.entrySet())
            for (ItemEffect i : e.getValue())
                skill.add(new EffectSkill(plugin, "effect" + e.getKey() + i.key, e.getKey(), i));
        
        skill.add(new GrapplingHook(plugin, "legacyhook", config.getInt("hook.hookitem", 262), config.getInt("hook.pullitem", 287), config.getLong("hook.blocktime", 5) * 20, config.getInt("hook.maxdistance", 100), config.getInt("hook.distancetoinitial", 10), config.getDouble("hook.itemsperblock", 0.01), config.getString("hook.errormsg", "You can't hook there!"), config.getString("hook.initialmsg", "You are to far away from your initial location!"), config.getString("hook.distancemsg", "Your hook is to far away!")));
        
        config.set("backstab", null);
        config.set("poison", null);
        config.set("hook", null);
        config.set("configVersion", 1);
        
        for (Skill s : skill)
            s.save(config);
        
        try
        {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
            plugin.reloadConfig();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }
    
    @SuppressWarnings("deprecation")
    private Map<String, List<ItemEffect>> loadPoisonItems()
    {
        Map<String, List<ItemEffect>> tmp = new HashMap<String, List<ItemEffect>>();
        
        String path = "poison.items";
        
        if (config.getConfigurationSection(path) != null)
            for (String key : config.getConfigurationSection(path).getKeys(false))
            {
                List<ItemEffect> list = new ArrayList<ItemEffect>();
                if (config.getConfigurationSection(path + "." + key) != null)
                    for (String effect : config.getConfigurationSection(path + "." + key).getKeys(false))
                    {
                        String npath = path + "." + key + "." + effect;
                        ItemEffect i = new ItemEffect();
                        i.key = effect;
                        i.type = PotionEffectType.getById(config.getInt(npath + ".type")) != null ? PotionEffectType.getById(config.getInt(npath + ".type")) : PotionEffectType.getByName(config.getString(npath + ".type"));
                        i.chance = config.getDouble(npath + ".chance", 0.0D);
                        i.duration = config.getInt(npath + ".duration", 0) * 20;
                        i.strength = config.getInt(npath + ".strength", 0);
                        i.sneak = config.getBoolean(npath + ".onsneak", false);
                        i.angle = config.getDouble(npath + ".maxangle", 0.0D);
                        list.add(i);
                    }
                tmp.put(key, list);
            }
        
        return tmp;
    }
    
    private Map<String, BackstabItem> loadBackstabItems()
    {
        Map<String, BackstabItem> tmp = new HashMap<String, BackstabItem>();
        String path = "backstab.items";
        
        if (config.getConfigurationSection(path) != null)
            for (String key : config.getConfigurationSection(path).getKeys(false))
            {
                BackstabItem i = new BackstabItem();
                final String npath = path + "." + key;
                i.chance = config.getDouble(npath + ".chance", 0.0D);
                i.critchance = config.getDouble(npath + ".critchance", 0.0D);
                i.critmultiplier = config.getDouble(npath + ".critmultiplier", 0.0D);
                i.critsneak = config.getBoolean(npath + ".critonsneak", false);
                i.multiplier = config.getDouble(npath + ".multiplier", 0.0D);
                i.sneak = config.getBoolean(npath + ".onsneak", false);
                i.angle = config.getDouble(npath + ".maxangle", 0.0D);
                tmp.put(key, i);
            }
        
        return tmp;
    }
}
