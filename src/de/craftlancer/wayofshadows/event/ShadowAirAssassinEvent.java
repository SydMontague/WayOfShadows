package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.AirAssassination;

public class ShadowAirAssassinEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Player player;
    private Entity entity;
    private AirAssassination skill;
    private double height;
    
    public ShadowAirAssassinEvent(Player player, Entity entity, AirAssassination skill, double height)
    {
        this.player = player;
        this.entity = entity;
        this.skill = skill;
        this.height = height;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public Entity getEntity()
    {
        return entity;
    }
    
    public AirAssassination getSkill()
    {
        return skill;
    }
    
    public double getHeight()
    {
        return height;
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
