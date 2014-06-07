package de.craftlancer.wayofshadows.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.wayofshadows.event.EndReason;
import de.craftlancer.wayofshadows.event.ShadowPickPocketEndEvent;
import de.craftlancer.wayofshadows.skills.PickPocket;

/**
 * Runs distance and angle checks for ongoing pickpockets
 */
public class PickPocketCheckTask extends BukkitRunnable
{
    private Player p1;
    private Player p2;
    private PickPocket skill;
    private int level;
    
    public PickPocketCheckTask(Player p1, Player p2, PickPocket skill, int level)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.skill = skill;
        this.level = level;
    }
    
    @Override
    public void run()
    {
        if (!p1.getLocation().getWorld().equals(p2.getLocation().getWorld()) || p1.getLocation().distance(p2.getLocation()) > skill.getMaxDistance().getValue(level))
        {
            p1.closeInventory();
            Bukkit.getPluginManager().callEvent(new ShadowPickPocketEndEvent(p1, skill, p2.getInventory(), EndReason.DISTANCE));
            cancel();
        }
        
        if (Utils.getAngle(p1.getLocation().getDirection(), p2.getLocation().getDirection()) > skill.getMaxAngle().getValue(level))
        {
            p1.closeInventory();
            Bukkit.getPluginManager().callEvent(new ShadowPickPocketEndEvent(p1, skill, p2.getInventory(), EndReason.ANGLE));
            cancel();
        }
    }
    
}
