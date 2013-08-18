package de.craftlancer.wayofshadows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.wayofshadows.event.ShadowPullEvent;

public class GrapplingHook extends Skill
{
    public GrapplingHook(WayOfShadows instance)
    {
        super(instance);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onHookShoot(PlayerInteractEvent event)
    {
        if (!event.hasItem() || !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;
        
        Player player = event.getPlayer();
        
        if ((event.getItem().getTypeId() == plugin.getHookItem()) && player.hasPermission("shadow.hook"))
        {
            Arrow arrow = player.launchProjectile(Arrow.class);
            
            arrow.setMetadata("playerLocation", new FixedMetadataValue(plugin, player.getLocation()));
            player.setMetadata("hookArrow", new FixedMetadataValue(plugin, arrow));
            
            player.getInventory().removeItem(new ItemStack(plugin.getHookItem(), 1));
        }
    }
    
    @EventHandler
    public void onPull(PlayerInteractEvent e)
    {
        if (!e.hasItem() || e.getItem().getTypeId() != plugin.getPullItem() || !(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR))
            return;
        
        Player player = e.getPlayer();
        
        if (!player.hasMetadata("hookArrow") || !((Arrow) player.getMetadata("hookArrow").get(0).value()).hasMetadata("isHit"))
            return;
        
        Arrow arrow = (Arrow) player.getMetadata("hookArrow").get(0).value();
        
        ShadowPullEvent event = new ShadowPullEvent(player, arrow);
        Bukkit.getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled())
            return;
        
        e.setCancelled(true);
        
        Location ploc = player.getEyeLocation();
        final Location aloc = arrow.getLocation();
        double distance1 = ploc.distance(aloc);
        
        int amount = (int) Math.ceil(distance1 * plugin.getStringPerBlock());
        
        if (distance1 > plugin.getMaxDistance())
        {
            player.sendMessage(plugin.getHookDistanceMsg());
            return;
        }
        
        if (ploc.distance((Location) arrow.getMetadata("playerLocation").get(0).value()) > plugin.getMaxDistance2())
        {
            player.sendMessage(plugin.getHookInitialMsg());
            return;
        }
        
        player.teleport((Location) arrow.getMetadata("playerLocation").get(0).value());
        Arrow ball = player.launchProjectile(Arrow.class);
        
        ball.setMetadata("teleportArrow", new FixedMetadataValue(plugin, true));
        player.setMetadata("teleportArrow", new FixedMetadataValue(plugin, ball.getEntityId()));
        player.getInventory().removeItem(new ItemStack(plugin.getPullItem(), amount));
        
    }
    
    @EventHandler
    public void onHookHit(ProjectileHitEvent event)
    {
        if (!event.getEntity().getType().equals(EntityType.ARROW) || !event.getEntity().getShooter().getType().equals(EntityType.PLAYER))
            return;
        
        Player player = (Player) event.getEntity().getShooter();
        Arrow arrow = (Arrow) event.getEntity();
        
        if (arrow.hasMetadata("playerLocation"))
        {
            arrow.setMetadata("isHit", new FixedMetadataValue(plugin, true));
            return;
        }
        
        if (player.hasMetadata("teleportArrow") && (arrow.getEntityId() == player.getMetadata("teleportArrow").get(0).asInt()))
        {
            Location loc = arrow.getLocation();
            arrow.remove();
            
            if ((loc.getBlock().getType().isSolid() || loc.getBlock().getRelative(0, 1, 0).getType().isSolid() || loc.getBlock().getRelative(0, 2, 0).getType().isSolid()))
            {
                player.sendMessage(plugin.getHookErrorMsg());
                player.removeMetadata("teleportArrow", plugin);
                player.removeMetadata("hookArrow", plugin);
                return;
            }
            
            player.teleport(loc);
            
            final Block block = player.getLocation().getBlock().getRelative(0, -1, 0);
            
            if (!block.getType().isSolid())
            {
                final Material type = block.getType();
                block.setType(Material.GLASS);
                
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        block.setType(type);
                    }
                }.runTaskLater(plugin, plugin.getBlockTime());
            }
            
            player.setFallDistance(0);
            player.removeMetadata("teleportArrow", plugin);
            player.removeMetadata("hookArrow", plugin);
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (!event.getDamager().getType().equals(EntityType.ARROW))
            return;
        
        Arrow arrow = (Arrow) event.getDamager();
        
        if (arrow.hasMetadata("teleportArrow") || arrow.hasMetadata("playerLocation"))
            event.setCancelled(true);
    }
}
