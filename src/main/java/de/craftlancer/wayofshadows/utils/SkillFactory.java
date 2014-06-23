package de.craftlancer.wayofshadows.utils;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.skills.AirAssassination;
import de.craftlancer.wayofshadows.skills.BackStab;
import de.craftlancer.wayofshadows.skills.ChestThief;
import de.craftlancer.wayofshadows.skills.EffectSkill;
import de.craftlancer.wayofshadows.skills.GrapplingHook;
import de.craftlancer.wayofshadows.skills.LockpickSkill;
import de.craftlancer.wayofshadows.skills.NewLockpickSkill;
import de.craftlancer.wayofshadows.skills.PickPocket;
import de.craftlancer.wayofshadows.skills.Skill;

public class SkillFactory
{
    /**
     * Creates a new Skill instance based of the "type" value given in the
     * config
     * 
     * @param key
     *            - the key of the skill in config
     * @param plugin
     *            - the instance of the plugin, this skill is created for
     * @return a Skill object
     */
    public static Skill createSkill(String key, WayOfShadows plugin)
    {
        if (!plugin.getConfig().isSet(key + ".type"))
        {
            plugin.error("Missing 'type' node in Skill \"" + key + "\"!");
            return null;
        }
        
        SkillType type = SkillType.matchType(plugin.getConfig().getString(key + ".type"));
        
        switch (type)
        {
            case AIRASSASSINATION:
                return new AirAssassination(plugin, key);
            case BACKSTAB:
                return new BackStab(plugin, key);
            case CHESTTHIEF:
                return new ChestThief(plugin, key);
            case EFFECT:
                return new EffectSkill(plugin, key);
            case GRAPPLINGHOOK:
                return new GrapplingHook(plugin, key);
            case LOCKPICK:
                return new LockpickSkill(plugin, key);
            case PICKPOCKET:
                return new PickPocket(plugin, key);
            case NEWLOCKPICK:
                return new NewLockpickSkill(plugin, key);
            default:
                plugin.error("Wrong 'type' node in Skill \"" + key + "\"! Valid types are 'backstab', 'effect', 'grapplinghook', 'pickpocket', 'airassassination', 'lockpick' and 'newlockpick'.");
        }
        
        return null;
    }
}
