package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.GrapplingHook;

/**
 * Called when a player tries to pull to his hook.
 */
public class ShadowPullEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Arrow arrow;
    
    public ShadowPullEvent(Player player, GrapplingHook skill, Arrow arrow)
    {
        super(player, skill);
        this.arrow = arrow;
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
        return getPlayer().getEyeLocation().distance(getArrow().getLocation());
    }
    
    @Override
    public GrapplingHook getSkill()
    {
        return (GrapplingHook) super.getSkill();
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
