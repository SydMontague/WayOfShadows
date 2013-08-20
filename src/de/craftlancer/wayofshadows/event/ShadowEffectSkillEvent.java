package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.EffectSkill;

public class ShadowEffectSkillEvent extends Event implements Cancellable
{
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Entity entity;
    private EffectSkill skill;
    private boolean cancel = false;
    
    public ShadowEffectSkillEvent(Player player, Entity entity, EffectSkill skill)
    {
        this.player = player;
        this.entity = entity;
        this.skill = skill;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public Entity getEntity()
    {
        return entity;
    }
    
    public EffectSkill getSkill()
    {
        return skill;
    }
    
    @Override
    public boolean isCancelled()
    {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancel = cancel;
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
