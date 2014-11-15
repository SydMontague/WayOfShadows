package de.craftlancer.wayofshadows.skills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import de.craftlancer.wayofshadows.skills.NewLockpickSkill.PickResult;
import de.craftlancer.wayofshadows.skills.NewLockpickSkill.Solution;

@SuppressWarnings("deprecation")
public class LockpickSession
{
    private NewLockpickSkill skill;
    private Solution solution;
    private Inventory inventory;
    private Block block;
    
    public LockpickSession(NewLockpickSkill skill, Player player, Solution solution, Block block)
    {
        this.skill = skill;
        this.solution = solution;
        this.inventory = Bukkit.createInventory(null, 27, "Lockpick");
        this.block = block;
        
        /*
         * 0000000##
         * #########
         * 0000000#B
         */
        
        for (int i = 0; i < 27; i++)
        {
            if (i < solution.getSize())
                continue;
            
            ItemStack button = new ItemStack(Material.SLIME_BALL);
            ItemMeta meta = button.getItemMeta();
            meta.setDisplayName("Confirm");
            button.setItemMeta(meta);
            
            if (i >= 18 && i - 18 < solution.getSize())
                inventory.setItem(i, new ItemStack(Material.WOOL));
            else if (i == 26)
                inventory.setItem(i, button);
            else
                inventory.setItem(i, new ItemStack(Material.IRON_FENCE));
        }
        
        inventory.setMaxStackSize(1);
        player.openInventory(getInventory());
    }
    
    public void handleInventoryClose(InventoryCloseEvent event)
    {
        if (!event.getInventory().equals(getInventory()))
            return;
        
        for (int i = 0; i < solution.getSize(); i++)
        {
            ItemStack item = event.getInventory().getItem(i);
            if (item != null)
                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
            inventory.setItem(i, null);
        }
        
        if (event.getPlayer() instanceof Player)
            skill.setOnCooldown((Player) event.getPlayer());
    }
    
    public void handleInventoryInteract(InventoryClickEvent event)
    {
        if (!event.getInventory().equals(getInventory()))
            return;
        
        if (event.getRawSlot() == 26)
            execute(event.getWhoClicked());
        
        if (event.getRawSlot() >= solution.getSize() && event.getRawSlot() < 27)
            event.setCancelled(true);
        else
        {
            
        }
    }
    
    private void execute(HumanEntity player)
    {
        Material[] input = new Material[solution.getSize()];
        
        for (int i = 0; i < solution.getSize(); i++)
        {
            ItemStack item = inventory.getItem(i);
            input[i] = item != null ? item.getType() : Material.AIR;
        }
        
        PickResult[] result = solution.getPickResult(input);
        
        for (int i = 0; i < solution.getSize(); i++)
            inventory.setItem(i + 18, result[i].getWoolItem());
        
        if (!isCorrect(result))
        {
            for (int i = 0; i < result.length; i++)
            {
                switch (result[i])
                {
                    case FALSE:
                        if (!skill.isUseOnFalse())
                            continue;
                        break;
                    case POSITION:
                        if (!skill.isUseOnPosition())
                            continue;
                        break;
                    case TRUE:
                        if (!skill.isUseOnTrue())
                            continue;
                        break;
                }
                inventory.setItem(i, null);
            }
            return;
        }
                
        BlockState state = block.getState();
        
        switch (block.getType())
        {
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            {
                BlockState state2 = null;
                
                Door data = (Door) block.getState().getData();
                Block tmp = null;
                
                if ((data.isTopHalf()))
                {
                    tmp = block.getRelative(0, -1, 0);
                    if (tmp.getType() == block.getType())
                        state2 = tmp.getState();
                }
                else
                {
                    tmp = block.getRelative(0, 1, 0);
                    if (tmp.getType() == block.getType())
                        state2 = tmp.getState();
                }
                
                Door data2 = state2 != null ? (Door) state2.getData() : null;
                
                if (state2 != null && data2 != null)
                {
                    data2.setOpen(!data2.isOpen());
                    state2.setData(data2);
                    state2.update();
                }
                
                data.setOpen(!data.isOpen());
                state.setData(data);
                state.update();
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(block.getLocation(), data.isOpen() ? Sound.DOOR_OPEN : Sound.DOOR_CLOSE, 1, 1);
                
                skill.addToCloseMap((Player) player, block);
                skill.addToCloseMap((Player) player, tmp);
                break;
            }
            case FENCE_GATE:
            case TRAP_DOOR:
            {
                Openable data = (Openable) state.getData();
                data.setOpen(!data.isOpen());
                state.setData((MaterialData) data);
                state.update();
                break;
            }
            case LEVER:
            {
                Lever data = (Lever) state.getData();
                data.setPowered(!data.isPowered());
                state.setData(data);
                state.update();
                break;
            }
            default:
            {
                if (block.getState() instanceof InventoryHolder)
                    player.openInventory(((InventoryHolder) block.getState()).getInventory());
            }
        }
        
        skill.removeSolution(block.getLocation());
        
        if (skill.useOnSuccess())
            for (int i = 0; i < result.length; i++)
                inventory.setItem(i, null);

        player.closeInventory();
    }
    
    private static boolean isCorrect(PickResult[] result)
    {
        for (PickResult r : result)
            if (r != PickResult.TRUE)
                return false;
        
        return true;
    }
    
    public Inventory getInventory()
    {
        return inventory;
    }
}
