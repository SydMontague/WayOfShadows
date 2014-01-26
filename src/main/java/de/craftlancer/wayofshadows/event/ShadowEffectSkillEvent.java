package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.EffectSkill;

/**
 * Called when a player puts an effect on another entity
 */
public class ShadowEffectSkillEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Entity entity;
    
    public ShadowEffectSkillEvent(Player player, EffectSkill skill, Entity entity)
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
    public EffectSkill getSkill()
    {
        return (EffectSkill) super.getSkill();
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
