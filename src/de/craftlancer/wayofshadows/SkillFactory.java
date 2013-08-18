package de.craftlancer.wayofshadows;

public class SkillFactory
{
    public static Skill createSkill(String key, WayOfShadows plugin)
    {
        if (plugin.getConfig().getString(key + ".type").equalsIgnoreCase("backstab"))
            return new BackStab(plugin, key);
        else if (plugin.getConfig().getString(key + ".type").equalsIgnoreCase("effect"))
            return new EffectSkill(plugin, key);
        else if (plugin.getConfig().getString(key + ".type").equalsIgnoreCase("grapplinghook"))
            return new GrapplingHook(plugin, key);
        else if (plugin.getConfig().getString(key + ".type").equalsIgnoreCase("pickpocket"))
            return new PickPocket(plugin, key);
        else if (plugin.getConfig().getString(key + ".type").equalsIgnoreCase("airassassination"))
            return new AirAssassination(plugin, key);
        
        throw new IllegalArgumentException("The skill \"" + key + "\" has no valid type!");
    }
}
