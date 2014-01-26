package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.BackStab;

/**
 * Called when a player performs a backstab
 */
public class ShadowBackStabEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Entity entity;
    
    public ShadowBackStabEvent(Player player, BackStab skill, Entity entity)
    {
        super(player, skill);
        this.entity = entity;
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
    
    @Override
    public BackStab getSkill()
    {
        return (BackStab) super.getSkill();
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
