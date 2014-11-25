package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.Leap;
import de.craftlancer.wayofshadows.skills.Skill;

public class ShadowLeapEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Entity victim;
    
    public ShadowLeapEvent(Player player, Skill skill, Entity victim)
    {
        super(player, skill);
        this.victim = victim;
    }
    
    public Entity getVictim()
    {
        return victim;
    }
    
    @Override
    public Leap getSkill()
    {
        return (Leap) super.getSkill();
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
