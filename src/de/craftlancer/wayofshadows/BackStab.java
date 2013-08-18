package de.craftlancer.wayofshadows;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BackStab extends Skill
{    
    public BackStab(WayOfShadows instance)
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
        double damage = event.getDamage();
        double angle = getAngle(damaged.getLocation().getDirection(), damager.getLocation().getDirection());
        BackstabItem backstab = plugin.backstabItem.get(String.valueOf(iteminhand));
        
        if (backstab != null && hasBPerm(damager, iteminhand) && Math.random() <= backstab.chance && angle < backstab.angle && (!backstab.sneak || damager.isSneaking()))
        {
            if (Math.random() <= backstab.critchance && (!backstab.critsneak || damager.isSneaking()))
            {
                event.setDamage(damage * backstab.critmultiplier);
                damager.sendMessage(plugin.getCritAttackerMsg());
            }
            else
            {
                event.setDamage(damage * backstab.multiplier);
                damager.sendMessage(plugin.getAttackerMsg());
            }
            if (damaged instanceof Player)
                ((Player) damaged).sendMessage(plugin.getVictimMsg());
        }
    }
}
