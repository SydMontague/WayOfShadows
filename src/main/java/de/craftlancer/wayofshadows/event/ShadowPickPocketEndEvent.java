package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.PickPocket;
import de.craftlancer.wayofshadows.skills.Skill;

/**
 * Gets called when a pickpocket comes to it's end,
 */
public class ShadowPickPocketEndEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Player victim;
    private EndReason reason;
    
    public ShadowPickPocketEndEvent(Player player, Skill skill, Player victim, EndReason reason)
    {
        super(player, skill);
        this.victim = victim;
    }
    
    @Override
    public PickPocket getSkill()
    {
        return (PickPocket) super.getSkill();
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
     * Get the reason, why the pickpocket has ended
     * 
     * @return the reason
     */
    public EndReason getReason()
    {
        return reason;
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
