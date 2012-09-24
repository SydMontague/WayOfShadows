package com.syd.wayofshadows;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Backstab implements Listener
{
    WayOfShadows plugin;
    
    public Backstab(WayOfShadows instance)
    {
        plugin = instance;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (event.isCancelled())
            return;
        
        if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow))
            return;
        
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getNoDamageTicks() != 0)
            return;
        
        Player damager = null;
        
        if (event.getDamager() instanceof Player)
            damager = (Player) event.getDamager();
        else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player)
            damager = (Player) ((Arrow) event.getDamager()).getShooter();
            
        Entity damaged = event.getEntity();
        
        int damage = event.getDamage();
        List<Integer> dmgitems = plugin.getDmgItems();
        List<Integer> poisonitems = plugin.getPoisonItems();
        double dmgchance = plugin.getDmgChance();
        double poisonchance = plugin.getPoisonChance();
        double critchance = plugin.getCritChance();
        double rand = Math.random();
        
        if ((damager.hasPermission("shadow.backstab") && rand <= dmgchance && dmgitems.contains(damager.getItemInHand().getTypeId()) && damaged.getLocation().getDirection().dot(damager.getLocation().getDirection()) > 0))
            if (plugin.getDmgSneak() == false || damager.isSneaking())
            {
                rand = Math.random();
                if (rand <= critchance && (!plugin.getCritSneak() || damager.isSneaking()))
                {
                    event.setDamage((int) (damage * plugin.getCritMultiplier()));
                    damager.sendMessage(plugin.getCritAttackerMsg());
                }
                else
                {
                    event.setDamage((int) (damage * plugin.getMultiplier()));
                    damager.sendMessage(plugin.getAttackerMsg());
                }
                if (damaged instanceof Player)
                    ((Player) damaged).sendMessage(plugin.getVictimMsg());
            }
        
        rand = Math.random();
        if (damager.hasPermission("shadow.poison") && rand <= poisonchance && poisonitems.contains(damager.getItemInHand().getTypeId()))
            if (!plugin.getPoisonSneak() || damager.isSneaking())
                ((LivingEntity) damaged).addPotionEffect(new PotionEffect(PotionEffectType.POISON, plugin.getPoisonDuration(), plugin.getPoisonStrength()));
    }
}
