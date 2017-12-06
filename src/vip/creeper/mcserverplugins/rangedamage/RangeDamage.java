package vip.creeper.mcserverplugins.rangedamage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RangeDamage extends JavaPlugin implements Listener {
    private String loreRegex;
    private Settings settings;

    public void onEnable() {
        settings = new Settings();

        loadConfig();

        loreRegex = settings.getLore().replace("{range}", "\\d+");

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();

        FileConfiguration config = getConfig();

        settings.setMaxY(config.getInt("maxY"));
        settings.setLore(config.getString("lore"));
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity target =  event.getEntity();
        Entity damager = event.getDamager();

        if (!(target instanceof LivingEntity) || !(damager instanceof Player)) {
            return;
        }

        LivingEntity livingEntityTarget = (LivingEntity) target;
        Player playerDamager = (Player) damager;


    }

    public int getItemDamageRange(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return -1;
        }

        List<String> lores = item.getItemMeta().getLore();

        if (lores == null || lores.size() == 0) {
            return -1;
        }

        for (String lore : lores) {
            if (lore.matches(loreRegex)) {

            }
        }
    }
}
