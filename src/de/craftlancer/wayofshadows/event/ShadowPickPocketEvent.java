package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.wayofshadows.skills.PickPocket;

public class ShadowPickPocketEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Player victim;
    private PickPocket skill;
    private ItemStack item;
    private boolean cancel = false;
    
    public ShadowPickPocketEvent(Player player, Player victim, PickPocket skill, ItemStack item)
    {
        this.player = player;
        this.victim = victim;
        this.skill = skill;
        this.item = item;
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
    
    public Player getVictim()
    {
        return victim;
    }
    
    public PickPocket getSkill()
    {
        return skill;
    }
    
    public ItemStack getItem()
    {
        return item;
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
