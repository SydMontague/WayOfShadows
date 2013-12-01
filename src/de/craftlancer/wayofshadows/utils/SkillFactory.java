package de.craftlancer.wayofshadows.utils;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.skills.AirAssassination;
import de.craftlancer.wayofshadows.skills.BackStab;
import de.craftlancer.wayofshadows.skills.EffectSkill;
import de.craftlancer.wayofshadows.skills.GrapplingHook;
import de.craftlancer.wayofshadows.skills.LockpickSkill;
import de.craftlancer.wayofshadows.skills.PickPocket;
import de.craftlancer.wayofshadows.skills.Skill;

public class SkillFactory
{
    /**
     * Creates a new Skill instance based of the "type" value given in the
     * config
     * 
     * @param key - the key of the skill in config
     * @param plugin - the instance of the plugin, this skill is created for
     * @return a Skill object
     */
    public static Skill createSkill(String key, WayOfShadows plugin)
    {
        if (!plugin.getConfig().isSet(key + ".type"))
        {
            plugin.error("Missing 'type' node in Skill \"" + key + "\"!");
            return null;
        }
        
        String type = plugin.getConfig().getString(key + ".type");
        
        if (type.equalsIgnoreCase("backstab"))
            return new BackStab(plugin, key);
        else if (type.equalsIgnoreCase("effect"))
            return new EffectSkill(plugin, key);
        else if (type.equalsIgnoreCase("grapplinghook"))
            return new GrapplingHook(plugin, key);
        else if (type.equalsIgnoreCase("pickpocket"))
            return new PickPocket(plugin, key);
        else if (type.equalsIgnoreCase("airassassination"))
            return new AirAssassination(plugin, key);
        else if (type.equalsIgnoreCase("lockpick"))
            return new LockpickSkill(plugin, key);
        
        plugin.error("Wrong 'type' node in Skill \"" + key + "\"! Valid types are 'backstab', 'effect', 'grapplinghook', 'pickpocket', 'airassassination' and 'lockpick'.");
        return null;
    }
}
