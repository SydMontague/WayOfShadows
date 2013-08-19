package de.craftlancer.wayofshadows;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PickPocket extends Skill
{    
    /*
     * Trigger per Rechtsklick von Hinten (maxAngle) während des schleichens (onSneak).
     * Auswahl des Items per Linksklick
     * Berechnung der Wahrscheinlichkeit anhand einer Formel (x = wert, y = level)
     * 
     * Es existiert ein Maxwert an Items, den man pro Aktion stehlen kann (fest oder formel mit x = level)
     * Es gibt die Möglichkeit unterschiedliche Wertkataloge auf verschiedene Skills zu lesen
     */
    
    private ValueWrapper maxAngle;
    private boolean onSneak;
    private ValueWrapper chance;
    private ValueWrapper maxValue;
    private String valueCatalogue;
    
    public PickPocket(WayOfShadows plugin, String key)
    {
        super(plugin, key);
        FileConfiguration config = plugin.getConfig();
        
        maxAngle = new ValueWrapper(config.getString(key + ".maxAngle", "90"));
        chance = new ValueWrapper(config.getString(key + ".chance", "90"));
        maxValue = new ValueWrapper(config.getString(key + ".maxValue", "90"));
        onSneak = config.getBoolean(key + ".onSneak", true);
        valueCatalogue = config.getString(key + ".valueCatalogue"); //TODO replace with something else
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();
        ItemStack item = p.getItemInHand();
        
        if(!e.getRightClicked().getType().equals(EntityType.PLAYER))
            return;
        if (!isSkillItem(item) || !hasPermission(p, item))
            return;
        
        Player victim = (Player) e.getRightClicked();
        double angle = getAngle(p.getLocation().getDirection(), victim.getLocation().getDirection());
        int level = plugin.getSkillLevels() != null ? plugin.getSkillLevels().getUserLevel(getLevelSys(), p.getName()) : 0;
        
        if((!onSneak || p.isSneaking()) && angle < maxAngle.getValue(level))
        {
            p.openInventory(victim.getInventory());
            //TODO some check code
        }
    }
    
    @EventHandler
    public void onSteal(InventoryClickEvent e)
    {
        
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
