package de.craftlancer.wayofshadows;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;

public class EffectSkill extends Skill
{
    
    public EffectSkill(WayOfShadows instance)
    {
        super(instance);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity) || ((LivingEntity) event.getEntity()).getNoDamageTicks() >= 1)
            return;
        
        Player damager;
        
        if (event.getDamager() instanceof Player)
            damager = (Player) event.getDamager();
        else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player)
            damager = (Player) ((Arrow) event.getDamager()).getShooter();
        else
            return;
        
        LivingEntity damaged = (LivingEntity) event.getEntity();
        int iteminhand = damager.getItemInHand().getTypeId();
        double angle = getAngle(damaged.getLocation().getDirection(), damager.getLocation().getDirection());
        List<ItemEffect> effect = plugin.effectItem.get(String.valueOf(iteminhand));
        
        if (effect != null)
            for (ItemEffect poison : effect)
                if (poison != null && hasEPerm(damager, poison.type, iteminhand) && Math.random() <= poison.chance && angle < poison.angle && (!poison.sneak || damager.isSneaking()))
                    damaged.addPotionEffect(new PotionEffect(poison.type, poison.duration, poison.strength));
    }
    
}
