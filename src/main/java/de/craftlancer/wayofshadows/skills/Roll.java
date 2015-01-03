package de.craftlancer.wayofshadows.skills;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

//TODO add events, cooldown and items
public class Roll extends Skill
{
    private ValueWrapper damageMod; // x = level, y = ticks between sneak and damage event
    private String message = null;
    
    public Roll(WayOfShadows instance, String key)
    {
        super(instance, key);
        FileConfiguration config = instance.getConfig();
        damageMod = new ValueWrapper(config.getString(key + ".damageMod", "y/5+0.2"));
        message = config.getString(key + ".message", null);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void toggleSneak(PlayerToggleSneakEvent event)
    {
        if (event.getPlayer().isSneaking() || event.getPlayer().getFallDistance() == 0)
            return;
        
        event.getPlayer().setMetadata("shadow." + getName() + ".sneaktime", new FixedMetadataValue(plugin, event.getPlayer().getWorld().getFullTime()));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        
        if (entity.getType() != EntityType.PLAYER || event.getCause() != DamageCause.FALL)
            return;
        
        if (!event.getEntity().hasMetadata("shadow." + getName() + ".sneaktime"))
            return;
        
        int level = plugin.getLevel((Player) entity, getLevelSys());
        
        long delta = entity.getWorld().getFullTime() - entity.getMetadata("shadow." + getName() + ".sneaktime").get(0).asLong();
        double mod = damageMod.getValue(level, delta);
        
        if(mod >= 1 || mod < 0)
            return;

        event.setDamage(mod * event.getDamage());
        
        if(message != null)
            ((Player) entity).sendMessage(message);
            
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.ROLL;
    }
    
}
