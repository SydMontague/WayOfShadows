package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.wayofshadows.skills.PickPocket;

/**
 * Called when a player performs a pickpocket
 */
public class ShadowPickPocketEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Player victim;
    private ItemStack item;
    
    public ShadowPickPocketEvent(Player player, PickPocket skill, Player victim, ItemStack item)
    {
        super(player, skill);
        this.victim = victim;
        this.item = item;
    }
    
    /**
     * Get the player who gets robbed
     * 
     * @return the robbed Player
     */
    public Player getVictim()
    {
        return victim;
    }
    
    /**
     * Get the item which is supposed to be stolen
     * 
     * @return the ItemStack of the Item
     */
    public ItemStack getItem()
    {
        return item;
    }
    
    @Override
    public PickPocket getSkill()
    {
        return (PickPocket) super.getSkill();
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
