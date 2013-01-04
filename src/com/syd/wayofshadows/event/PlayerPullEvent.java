package com.syd.wayofshadows.event;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player tries to pull to his hook.
 */
public class PlayerPullEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private Arrow arrow;
    private boolean cancel = false;
    
    public PlayerPullEvent(Player p, Arrow arrow)
    {
        this.p = p;
        this.arrow = arrow;
    }
    
    /**
     * Get the player who tries to pull himself
     * 
     * @return the player
     */
    public Player getPlayer()
    {
        return p;
    }
    
    /**
     * Get the arrow to which location the player tries to pull himself
     * 
     * @return the arrow
     */
    public Arrow getArrow()
    {
        return arrow;
    }
    
    /**
     * Get the distance between Player and Arrow
     * 
     * @return the distance between Player and Arrow
     */
    public double getDistance()
    {
        return p.getEyeLocation().distance(arrow.getLocation());
        
    }
    
    @Override
    public boolean isCancelled()
    {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean arg0)
    {
        cancel = arg0;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
