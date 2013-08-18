package de.craftlancer.wayofshadows;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public abstract class Skill implements Listener
{
    protected WayOfShadows plugin;
    
    public Skill(WayOfShadows instance)
    {
        plugin = instance;
    }

    public static boolean hasEPerm(Player damager, PotionEffectType type, int id)
    {
        return damager != null ? (damager.hasPermission("shadow.effect") || damager.hasPermission("shadow.effect." + id) || damager.hasPermission("shadow.effect." + id + "." + type.getName())) : false;
    }
    
    public static boolean hasBPerm(Player damager, int id)
    {
        return damager != null ? (damager.hasPermission("shadow.backstab") || damager.hasPermission("shadow.backstab." + id)) : false;
    }
    
    public static double getAngle(Vector vec1, Vector vec2)
    {
        return vec1.angle(vec2) * 180 / Math.PI;
    }
}
