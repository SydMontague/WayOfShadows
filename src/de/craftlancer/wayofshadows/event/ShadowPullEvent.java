package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.GrapplingHook;

/**
 * Called when a player tries to pull to his hook.
 */
public class ShadowPullEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Arrow arrow;
    private GrapplingHook skill;
    private boolean cancel = false;
    
    public ShadowPullEvent(Player player, Arrow arrow, GrapplingHook skill)
    {
        this.player = player;
        this.arrow = arrow;
        this.skill = skill;
    }
    
    /**
     * Get the player who tries to pull himself
     * 
     * @return the player
     */
    public Player getPlayer()
    {
        return player;
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
        return player.getEyeLocation().distance(arrow.getLocation());
    }
    
    public GrapplingHook getSkill()
    {
        return skill;
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
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
