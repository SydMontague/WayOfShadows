package com.syd.wayofshadows;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PluginEntityListener implements Listener
{
    WayOfShadows plugin;
    
    public PluginEntityListener(WayOfShadows instance)
    {
        plugin = instance;
    }
    
    // backstab and poison
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            Player damager = (Player) event.getDamager();
            Entity damaged = event.getEntity();
            int damage = event.getDamage();
            List<Integer> dmgitems = plugin.getDmgItems();
            List<Integer> poisonitems = plugin.getPoisonItems();
            double dmgchance = plugin.getDmgChance();
            double poisonchance = plugin.getPoisonChance();
            double critchance = plugin.getCritChance();
            double rand = Math.random();
            int duration = plugin.getPoisonDuration();
            
            if ((rand <= dmgchance && damager.hasPermission("shadow.backstab") && dmgitems.contains(damager.getItemInHand().getTypeId()) && damaged.getLocation().getDirection().dot(damager.getLocation().getDirection()) > 0))
                if (plugin.getDmgSneak() == false || damager.isSneaking())
                {
                    rand = Math.random();
                    if (rand <= critchance && (plugin.getCritSneak() == false || damager.isSneaking()))
                    {
                        event.setDamage((int) (damage * plugin.getCritMultiplier()));
                        damager.sendMessage("CRITICAL!");
                    }
                    else
                    {
                        event.setDamage((int) (damage * plugin.getMultiplier()));
                        damager.sendMessage("Backstab!");
                    }
                    if (damaged instanceof Player)
                        ((Player) damaged).sendMessage("You got stabbed in the back!");
                }
            
            rand = Math.random();
            if (rand <= poisonchance && damager.hasPermission("shadow.poison") && poisonitems.contains(damager.getItemInHand().getTypeId()))
                if (plugin.getPoisonSneak() == false || damager.isSneaking())
                    ((LivingEntity) damaged).addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, 0));
        }
    }
}
