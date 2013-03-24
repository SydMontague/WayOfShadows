package de.craftlancer.wayofshadows;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Backstab implements Listener
{
    private WayOfShadows plugin;
    
    public Backstab(WayOfShadows instance)
    {
        plugin = instance;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        Player damager = null;
        
        if (event.isCancelled())
            return;
        
        if (event.getEntity() instanceof LivingEntity && ((LivingEntity) event.getEntity()).getNoDamageTicks() >= 1)
            return;
        
        if (event.getDamager() instanceof Player)
            damager = (Player) event.getDamager();
        else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player)
            damager = (Player) ((Arrow) event.getDamager()).getShooter();
        else
            return;
        
        LivingEntity damaged = (LivingEntity) event.getEntity();
        int iteminhand = damager.getItemInHand().getTypeId();
        int damage = event.getDamage();
        double angle = getAngle(damaged.getLocation().getDirection(), damager.getLocation().getDirection());
        List<ItemEffect> effect = plugin.effectItem.get(String.valueOf(iteminhand));
        BackstabItem backstab = plugin.backstabItem.get(String.valueOf(iteminhand));
        
        if (backstab != null && hasBPerm(damager, iteminhand) && Math.random() <= backstab.chance && angle < backstab.angle && (!backstab.sneak || damager.isSneaking()))
        {
            if (Math.random() <= backstab.critchance && (!backstab.critsneak || damager.isSneaking()))
            {
                event.setDamage((int) (damage * backstab.critmultiplier));
                damager.sendMessage(plugin.getCritAttackerMsg());
            }
            else
            {
                event.setDamage((int) (damage * backstab.multiplier));
                damager.sendMessage(plugin.getAttackerMsg());
            }
            if (damaged instanceof Player)
                ((Player) damaged).sendMessage(plugin.getVictimMsg());
        }
        
        if (effect != null)
            for (ItemEffect poison : effect)
                if (poison != null && hasEPerm(damager, poison.type, iteminhand) && Math.random() <= poison.chance && angle < poison.angle && (!poison.sneak || damager.isSneaking()))
                    damaged.addPotionEffect(new PotionEffect(poison.type, poison.duration, poison.strength));
    }
    
    private static boolean hasEPerm(Player damager, PotionEffectType type, int id)
    {
        return damager != null ? (damager.hasPermission("shadow.effect") || damager.hasPermission("shadow.effect." + id) || damager.hasPermission("shadow.effect." + id + "." + type.getName())) : false;
    }
    
    private static boolean hasBPerm(Player damager, int id)
    {
        return damager != null ? (damager.hasPermission("shadow.backstab") || damager.hasPermission("shadow.backstab." + id)) : false;
    }
    
    private static double getAngle(Vector vec1, Vector vec2)
    {
        return vec1.angle(vec2) * 180 / Math.PI;
    }
}
