package de.craftlancer.wayofshadows.skills;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.event.ShadowLockpickEvent;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

//TODO MISSING SAVE
public class LockpickSkill extends Skill
{
    private boolean loseItemOnFail;
    private boolean loseItemOnSuccess;
    private ValueWrapper chance;
    private ValueWrapper damageOnFail;
    private ValueWrapper closeCooldown;
    private Map<Material, ValueWrapper> pickMod = new HashMap<Material, ValueWrapper>();
    
    private String successMsg;
    private String failMsg;
    private String closeCooldownMsg;
    private String sneakMsg;
    
    private Map<Block, Long> closeMap = new HashMap<Block, Long>();
    
    public LockpickSkill(WayOfShadows instance, String key)
    {
        super(instance, key);
        FileConfiguration config = instance.getConfig();
        
        chance = new ValueWrapper(config.getString(key + ".chance", "0"));
        closeCooldown = new ValueWrapper(config.getString(key + ".closeCooldown", "300"));
        damageOnFail = new ValueWrapper(config.getString(key + ".damageOnFail"));
        
        loseItemOnFail = config.getBoolean(key + ".loseItemOnFail", false);
        loseItemOnSuccess = config.getBoolean(key + ".loseItemOnSuccess", false);
        
        successMsg = config.getString(key + ".successMsg", "You successfully picked the lock!");
        failMsg = config.getString(key + ".failMsg", "You failed at picking the lock!");
        sneakMsg = config.getString(key + ".sneakMsg", "You can't pick locks while sneaking!");
        closeCooldownMsg = config.getString(key + ".closeCooldownMsg", "You can't close this for another %time% seconds!");
        
        if (config.getConfigurationSection(key + ".pickable") != null)
            for (String s : config.getConfigurationSection(key + ".pickable").getKeys(false))
            {
                Material mat = Material.getMaterial(s);
                if (mat == null)
                    continue;
                
                pickMod.put(mat, new ValueWrapper(config.getString(key + ".pickable." + s)));
            }
        
        new CloseMapUpdateTask(closeMap).runTaskTimer(instance, 20L, 20L);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e)
    {
        if (!e.hasBlock() || !e.isCancelled() || e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        ItemStack item = e.getItem();
        Player p = e.getPlayer();
        
        if (!isSkillItem(item) || !hasPermission(p, item))
            return;
        
        if (p.isSneaking())
        {
            p.sendMessage(sneakMsg);
            return;
        }
        
        int level = plugin.getLevel(p, getLevelSys());
        double mod = 0;
        if (pickMod.containsKey(e.getClickedBlock().getType()))
            mod = pickMod.get(e.getClickedBlock().getType()).getValue(level);
        
        double c = chance.getValue(level, mod);
        if (c == 0)
            return;
        
        if (isOnCooldown(p))
        {
            p.sendMessage(getCooldownMsg(p));
            return;
        }
        
        ShadowLockpickEvent event = new ShadowLockpickEvent(p, this, e.getClickedBlock(), Math.random() <= c);
        plugin.getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled())
            return;
        
        if (event.isSuccess())
        {
            e.setCancelled(false);
            
            Block b1 = e.getClickedBlock();
            long time = System.currentTimeMillis() + (long) closeCooldown.getValue(level, mod) * 1000;
            switch (e.getClickedBlock().getType())
            {
                case WOODEN_DOOR:
                    Block b2 = b1.getRelative(0, -1, 0);
                    if (b1.getRelative(0, 1, 0).getType() == Material.WOODEN_DOOR)
                        b2 = b1.getRelative(0, 1, 0);
                    closeMap.put(b2, time);
                    //$FALL-THROUGH$
                case TRAP_DOOR:
                case FENCE_GATE:
                case LEVER:
                    closeMap.put(b1, time);
                    break;
                default:
            }
            
            p.sendMessage(successMsg);
            if (loseItemOnSuccess)
            {
                ItemStack i = new ItemStack(item);
                i.setAmount(1);
                p.getInventory().removeItem(i);
            }
        }
        else
        {
            p.sendMessage(failMsg);
            double dmg = damageOnFail.getValue(level, mod);
            if (dmg != 0)
                p.damage(dmg);
            
            if (loseItemOnFail)
            {
                ItemStack i = new ItemStack(item);
                i.setAmount(1);
                p.getInventory().removeItem(i);
            }
        }
        
        setOnCooldown(e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDoorInteract(PlayerInteractEvent e)
    {
        if (!e.hasBlock())
            return;
        
        switch (e.getClickedBlock().getType())
        {
            case WOODEN_DOOR:
            case TRAP_DOOR:
            case FENCE_GATE:
            case LEVER:
                break;
            default:
                return;
        }
        
        if (!closeMap.containsKey(e.getClickedBlock()))
            return;
        
        long time = closeMap.get(e.getClickedBlock());
        time = time - System.currentTimeMillis();
        
        if (time <= 0)
        {
            closeMap.remove(e.getClickedBlock());
            return;
        }
        
        e.setCancelled(true);
        e.getPlayer().sendMessage(closeCooldownMsg.replace("%time%", String.valueOf(time / 1000)));
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.LOCKPICK;
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        
        config.set(getName() + ".loseItemOnFail", loseItemOnFail);
        config.set(getName() + ".loseItemOnSuccess", loseItemOnSuccess);
        config.set(getName() + ".chance", chance);
        config.set(getName() + ".damageOnFail", damageOnFail);
        config.set(getName() + ".closeCooldown", closeCooldown);
        
        for (Entry<Material, ValueWrapper> entry : pickMod.entrySet())
            config.set(getName() + ".pickable." + entry.getKey().name(), entry.getValue().getInput());
        
        config.set(getName() + ".successMsg", successMsg);
        config.set(getName() + ".failMsg", failMsg);
        config.set(getName() + ".closeCooldownMsg", closeCooldownMsg);
        config.set(getName() + ".sneakMsg", sneakMsg);
    }
}

class CloseMapUpdateTask extends BukkitRunnable
{
    private Map<Block, Long> closeMap;
    
    protected CloseMapUpdateTask(Map<Block, Long> closeMap)
    {
        this.closeMap = closeMap;
    }
    
    @Override
    public void run()
    {
        Set<Block> remove = new HashSet<Block>();
        
        for (Entry<Block, Long> b : closeMap.entrySet())
            if (System.currentTimeMillis() >= b.getValue())
                remove.add(b.getKey());
        
        for (Block b : remove)
            closeMap.remove(b);
    }
}
