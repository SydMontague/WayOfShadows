package com.syd.wayofshadows;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GrapplingHook implements Listener
{
    WayOfShadows plugin;
    HashMap<Player, Hook> map = new HashMap<Player, Hook>();
    
    public GrapplingHook(WayOfShadows instance)
    {
        plugin = instance;
    }
    
    @EventHandler
    public void onHookShoot(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && (event.getItem() != null && event.getItem().getTypeId() == 262) && player.hasPermission("shadow.hook"))
        {
            Arrow arrow = player.launchProjectile(Arrow.class);
            player.getInventory().removeItem(new ItemStack(262, 1));
            
            map.put(player, new Hook(arrow, false));
        }
    }
    
    @EventHandler
    public void onPull(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        int pullitem = plugin.getPullItem();
        
        if (map.get(player) != null && map.get(player).hit == true)
        {
            Arrow arrow = map.get(player).arrow;
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && (event.getItem() != null && event.getItem().getTypeId() == pullitem))
            {
                Location ploc = player.getEyeLocation();
                final Location aloc = arrow.getLocation();
                double distance1 = ploc.distance(aloc);
                
                if (distance1 <= plugin.getMaxDistance())
                {
                    Vector direction = aloc.toVector().subtract(ploc.toVector());
                    double x = direction.getX();
                    double y = direction.getY();
                    double z = direction.getZ();
                    
                    Location ploc1 = ploc.clone();
                    Location aloc1 = aloc.clone();
                    aloc1.setY(0);
                    ploc1.setY(0);
                    double distance = ploc1.distance(aloc1);
                    
                    double pitch = Math.toDegrees(-Math.atan2(y, distance)) + 5;
                    
                    double yaw = Math.toDegrees(-Math.atan2(x, z));
                    
                    if (yaw > 0)
                        yaw = yaw - 360;
                    
                    ploc.setPitch((float) pitch);
                    ploc.setYaw((float) yaw);
                    
                    ploc.subtract(0, player.getEyeHeight(), 0);
                    player.teleport(ploc);
                    
                    Snowball ball = player.launchProjectile(Snowball.class);
                    player.setVelocity(ball.getVelocity());
                    
                    double timemod = plugin.getPullTimeMod();
                    long time = (long) (distance1 / timemod);
                    
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                    {
                        public void run()
                        {
                            player.teleport(aloc);
                            if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).getTypeId() == 0)
                            {
                                final Block block = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0));
                                block.setType(Material.GLASS);
                                
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                                {
                                    public void run()
                                    {
                                        block.setTypeId(0);
                                    }
                                }, plugin.getBlockTime());
                            }
                            player.setFallDistance(0);
                            
                            map.remove(player);
                        }
                    }, time);
                    
                    player.getInventory().removeItem(new ItemStack(pullitem, 1));
                }
            }
        }
    }
    
    @EventHandler
    public void onHookHit(final ProjectileHitEvent event)
    {
        if (event.getEntity() instanceof Arrow)
        {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player)
            {
                Player player = (Player) arrow.getShooter();
                
                if (map.get(player) != null && map.get(player).arrow.getUniqueId() == arrow.getUniqueId())
                {
                    map.get(player).hit = true;
                    map.get(player).arrow = arrow;
                }
            }
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        if (event instanceof EntityDamageByEntityEvent)
        {
            if (((EntityDamageByEntityEvent) event).getDamager() instanceof Arrow)
            {
                Arrow arrow = ((Arrow) ((EntityDamageByEntityEvent) event).getDamager());
                if (arrow.getShooter() instanceof Player)
                {
                    Player player = (Player) arrow.getShooter();
                    if ((map.get(player) != null && map.get(player).arrow.getUniqueId() == arrow.getUniqueId()) || player.getItemInHand().getTypeId() == 262)
                    {
                        map.get(player).hit = false;
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}

class Hook
{
    Arrow arrow;
    boolean hit;
    
    public Hook(Arrow arrow, boolean hit)
    {
        this.arrow = arrow;
        this.hit = hit;
    }
}
