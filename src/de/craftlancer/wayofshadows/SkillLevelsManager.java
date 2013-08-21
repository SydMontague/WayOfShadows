package de.craftlancer.wayofshadows;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.craftlancer.skilllevels.LevelAction;
import de.craftlancer.skilllevels.SkillLevels;
import de.craftlancer.wayofshadows.event.ShadowAirAssassinEvent;
import de.craftlancer.wayofshadows.event.ShadowBackStabEvent;
import de.craftlancer.wayofshadows.event.ShadowEffectSkillEvent;
import de.craftlancer.wayofshadows.event.ShadowEvent;
import de.craftlancer.wayofshadows.event.ShadowPickPocketEvent;
import de.craftlancer.wayofshadows.event.ShadowPullEvent;
import de.craftlancer.wayofshadows.skills.Skill;

public class SkillLevelsManager implements Listener
{
    private SkillLevels levels;
    
    public SkillLevelsManager(SkillLevels levels)
    {
        this.levels = levels;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAirAssassionation(ShadowAirAssassinEvent e)
    {
        handleShadowEvent(e);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBackStab(ShadowBackStabEvent e)
    {
        handleShadowEvent(e);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSkillEffect(ShadowEffectSkillEvent e)
    {
        handleShadowEvent(e);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickPocket(ShadowPickPocketEvent e)
    {
        handleShadowEvent(e);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPull(ShadowPullEvent e)
    {
        handleShadowEvent(e);
    }
    
    private void handleShadowEvent(ShadowEvent e)
    {
        Skill s = e.getSkill();
        levels.handleAction(LevelAction.CUSTOM, "shadow_" + s.getName(), e.getPlayer());
    }
}
