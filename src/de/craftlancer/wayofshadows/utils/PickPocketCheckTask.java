package de.craftlancer.wayofshadows.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runs distance and angle checks for ongoing pickpockets
 */
public class PickPocketCheckTask extends BukkitRunnable
{
    private Player p1;
    private Player p2;
    private double limit;
    private double maxAngle;
    
    public PickPocketCheckTask(Player p1, Player p2, double limit, double maxAngle)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.limit = limit;
        this.maxAngle = maxAngle;
    }
    
    @Override
    public void run()
    {
        if (p1.getLocation().distance(p2.getLocation()) > limit)
        {
            p1.closeInventory();
            cancel();
        }
        
        if (Utils.getAngle(p1.getLocation().getDirection(), p2.getLocation().getDirection()) > maxAngle)
        {
            p1.closeInventory();
            cancel();
        }
    }
    
}
