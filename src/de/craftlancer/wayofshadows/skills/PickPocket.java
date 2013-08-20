package de.craftlancer.wayofshadows.skills;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import de.craftlancer.wayofshadows.ValueCatalogue;
import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.PickPocketCheckTask;
import de.craftlancer.wayofshadows.utils.Utils;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

public class PickPocket extends Skill
{
    private ValueWrapper maxAngle;
    private ValueWrapper maxDistance;
    private ValueWrapper chance;
    private ValueWrapper maxValue;
    private boolean onSneak;
    private ValueCatalogue valueCatalogue;
    private boolean abortOnDamage;
    
    private String maxValueMsg;
    
    public PickPocket(WayOfShadows plugin, String key)
    {
        super(plugin, key);
        FileConfiguration config = plugin.getConfig();
        
        maxAngle = new ValueWrapper(config.getString(key + ".maxAngle", "90"));
        chance = new ValueWrapper(config.getString(key + ".chance", "1"));
        maxValue = new ValueWrapper(config.getString(key + ".maxValue", "90"));
        maxDistance = new ValueWrapper(config.getString(key + ".maxDistance", "10"));
        onSneak = config.getBoolean(key + ".onSneak", true);
        abortOnDamage = config.getBoolean(key + ".abortOnDamage", true);
        valueCatalogue = plugin.getValueCatalogue(config.getString(key + ".valueCatalogue"));
        
        maxValueMsg = config.getString(key + ".maxValueMsg", "You've reached your stealing limit.");
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();
        ItemStack item = p.getItemInHand();
        
        if (!e.getRightClicked().getType().equals(EntityType.PLAYER))
            return;
        if (!isSkillItem(item) || !hasPermission(p, item))
            return;
        
        Player victim = (Player) e.getRightClicked();
        double angle = Utils.getAngle(p.getLocation().getDirection(), victim.getLocation().getDirection());
        int level = plugin.getLevel(p, getLevelSys());
        
        if ((!onSneak || p.isSneaking()) && angle < maxAngle.getValue(level))
        {
            if (isOnCooldown(p))
            {
                p.sendMessage(getCooldownMsg(p));
                return;
            }
            
            p.openInventory(victim.getInventory());
            p.setMetadata("stealingPlayer", new FixedMetadataValue(plugin, victim));
            p.setMetadata("stealingValue", new FixedMetadataValue(plugin, 0));
            
            new PickPocketCheckTask(p, victim, maxDistance.getValue(level), maxAngle.getValue(level)).runTaskTimer(plugin, 10L, 10L);
        }
    }
    
    @EventHandler
    public void onSteal(InventoryClickEvent e)
    {
        Player p = ((Player) e.getWhoClicked());
        
        if (!p.hasMetadata("stealingPlayer"))
            return;
        
        e.setCancelled(true);
        
        if ((e.getRawSlot() >= e.getView().getTopInventory().getSize()) || e.getRawSlot() == -999)
            return;
        
        ItemStack item = e.getCurrentItem().clone();
        item.setAmount(1);
        
        if (item.getType().equals(Material.AIR))
            return;
        
        int level = plugin.getLevel(p, getLevelSys());
        int value = valueCatalogue.getValue(item);
        
        if (p.getMetadata("stealingValue").get(0).asDouble() + value > maxValue.getIntValue(level))
        {
            p.sendMessage(maxValueMsg);
            return;
        }
        
        if (Math.random() < chance.getValue(level))
        {
            e.getView().getTopInventory().removeItem(item);
            p.getInventory().addItem(item);
        }
        
        p.setMetadata("stealingValue", new FixedMetadataValue(plugin, p.getMetadata("stealingValue").get(0).asDouble() + value));
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        if (!e.getPlayer().hasMetadata("stealingPlayer"))
            return;
        
        e.getPlayer().removeMetadata("stealingPlayer", plugin);
        e.getPlayer().removeMetadata("stealingValue", plugin);
        
        setOnCooldown((Player) e.getPlayer());
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e)
    {
        if (abortOnDamage && e.getEntity().hasMetadata("stealingPlayer") && e.getEntity().getType().equals(EntityType.PLAYER))
            ((Player) e.getEntity()).closeInventory();
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        
        config.set(getName() + ".type", "pickpocket");
        config.set(getName() + ".chance", chance.getInput());
        config.set(getName() + ".maxValue", maxValue.getInput());
        config.set(getName() + ".maxAngle", maxAngle.getInput());
        config.set(getName() + ".onSneak", onSneak);
        config.set(getName() + ".valueCatalogue", valueCatalogue);
    }
}
