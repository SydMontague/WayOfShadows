package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.AirAssassination;

/**
 * Called when a player performs a air assassionation
 */
public class ShadowAirAssassinEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Entity entity;
    private double height;
    
    public ShadowAirAssassinEvent(Player player, AirAssassination skill, Entity entity, double height)
    {
        super(player, skill);
        this.entity = entity;
        this.height = height;
    }
    
    /**
     * Get the entity, which is victim of the attack
     * 
     * @return the Entity, which is attacked
     */
    public Entity getEntity()
    {
        return entity;
    }
    
    /**
     * Returns the height, based on fallheight - minheight of the skill.
     * 
     * @return the height, which is accounted for this attack
     */
    public double getHeight()
    {
        return height;
    }
    
    @Override
    public AirAssassination getSkill()
    {
        return (AirAssassination) super.getSkill();
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
