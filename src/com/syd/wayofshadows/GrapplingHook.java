package com.syd.wayofshadows;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.syd.wayofshadows.event.PlayerPullEvent;


public class GrapplingHook implements Listener
{
    private WayOfShadows plugin;
    private HashMap<Player, Hook> map = new HashMap<Player, Hook>();
    
    public GrapplingHook(WayOfShadows instance)
    {
        plugin = instance;
    }
    
    @EventHandler
    public void onHookShoot(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && (event.getItem() != null && event.getItem().getTypeId() == plugin.getHookItem()) && player.hasPermission("shadow.hook"))
        {
            Arrow arrow = player.launchProjectile(Arrow.class);
            player.getInventory().removeItem(new ItemStack(plugin.getHookItem(), 1));
            
            map.put(player, new Hook(arrow, false, player.getLocation()));
        }
    }
    
    @EventHandler
    public void onPull(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        int pullitem = plugin.getPullItem();
        
        if (map.containsKey(player) && map.get(player).hit && map.get(player).id == null)
        {
            Arrow arrow = map.get(player).arrow;
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && (event.hasItem() && event.getItem().getTypeId() == pullitem))
            {
                event.setCancelled(true);
                
                PlayerPullEvent ownevent = new PlayerPullEvent(player, arrow);
                Bukkit.getServer().getPluginManager().callEvent(ownevent);
                
                if (!ownevent.isCancelled())
                {
                    Location ploc = player.getEyeLocation();
                    final Location aloc = arrow.getLocation();
                    double distance1 = ploc.distance(aloc);
                    
                    int amount = (int) Math.ceil(distance1 * plugin.getStringPerBlock());
                    
                    if (distance1 <= plugin.getMaxDistance())
                        if (player.getLocation().distance(map.get(player).loc) < plugin.getMaxDistance2())
                        {
                            player.teleport(map.get(player).loc);
                            
                            Arrow ball = player.launchProjectile(Arrow.class);
                            map.get(player).id = ball.getUniqueId();
                            
                            player.getInventory().removeItem(new ItemStack(pullitem, amount));
                        }
                        else
                            player.sendMessage(plugin.getHookInitialMsg());
                    else
                        player.sendMessage(plugin.getHookDistanceMsg());
                }
            }
        }
    }
    
    @EventHandler
    public void onHookHit(ProjectileHitEvent event)
    {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player)
        {
            Player player = (Player) event.getEntity().getShooter();
            Arrow arrow = (Arrow) event.getEntity();
            
            if (map.containsKey(arrow.getShooter()) && map.get(player).id == arrow.getUniqueId())
            {
                Location loc = arrow.getLocation();
                arrow.remove();
                
                if ((!loc.getBlock().getType().isTransparent() || !loc.getBlock().getRelative(0, 1, 0).getType().isTransparent() || !loc.getBlock().getRelative(0, 2, 0).getType().isTransparent()))
                {
                    player.sendMessage(plugin.getHookErrorMsg());
                    return;
                }
                player.teleport(loc);
                
                if (player.getWorld().getBlockAt(player.getLocation().clone().subtract(0, 1, 0)).getTypeId() == 0)
                {
                    final Block block = player.getWorld().getBlockAt(player.getLocation().clone().subtract(0, 1, 0));
                    block.setType(Material.GLASS);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            block.setTypeId(0);
                        }
                    }, plugin.getBlockTime());
                }
                player.setFallDistance(0);
                map.remove(player);
            }
            else if (map.containsKey(player) && map.get(player).arrow.getUniqueId() == arrow.getUniqueId())
            {
                map.get(player).hit = true;
                map.get(player).arrow = arrow;
            }
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Arrow)
            if (event.getEntity() instanceof Player && map.get(event.getEntity()) != null && map.get(event.getEntity()).id == event.getDamager().getUniqueId())
                event.setCancelled(true);
            else
            {
                Arrow arrow = ((Arrow) event.getDamager());
                if (arrow.getShooter() instanceof Player)
                {
                    Player player = (Player) arrow.getShooter();
                    if (map.containsKey(player) && (map.get(player).arrow.getUniqueId() == arrow.getUniqueId() || player.getItemInHand().getTypeId() == 262 || (map.get(player).id != null && map.get(player).id == arrow.getUniqueId())))
                    {
                        map.get(player).hit = false;
                        event.setCancelled(true);
                    }
                }
            }
        
    }
}

class Hook
{
    public Arrow arrow;
    public Location loc;
    public UUID id;
    public boolean hit;
    
    public Hook(Arrow arrow, boolean hit, Location loc)
    {
        this.arrow = arrow;
        this.hit = hit;
        this.loc = loc;
        this.id = null;
    }
}
