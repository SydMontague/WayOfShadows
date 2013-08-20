package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.BackStab;

public class ShadowBackstabEvent extends Event implements Cancellable
{
    
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Player player;
    private Entity entity;
    private BackStab skill;
    
    public ShadowBackstabEvent(Player player, Entity entity, BackStab skill)
    {
        this.skill = skill;
        this.entity = entity;
        this.player = player;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public Entity getEntity()
    {
        return entity;
    }
    
    public BackStab getSkill()
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
