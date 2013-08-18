package de.craftlancer.wayofshadows;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ShadowListener implements Listener
{
    private WayOfShadows plugin;
    
    public ShadowListener(WayOfShadows plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e)
    {
        if (!e.getEntity().getShooter().getType().equals(EntityType.PLAYER))
            return;
        
        e.getEntity().setMetadata("shootingItem", new FixedMetadataValue(plugin, ((Player) e.getEntity().getShooter()).getItemInHand().clone()));
    }
    
}
