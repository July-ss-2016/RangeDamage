package vip.creeper.mcserverplugins.rangedamage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RangeDamage extends JavaPlugin implements Listener {
    private String loreRegex;
    private Settings settings;

    public void onEnable() {
        settings = new Settings();

        loadConfig();

        loreRegex = settings.getLore().replace("{range}", "\\d+");
        getCommand("rd");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender cs, Command cmd, String lable, String[] args) {
        if (cs.hasPermission("RangeDamage.admin") && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            loadConfig();
            cs.sendMessage("ok.");
            return true;
        }

        return false;
    }

    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();

        FileConfiguration config = getConfig();

        settings.setLore(config.getString("lore"));
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity target =  event.getEntity();
        Entity damager = event.getDamager();

        if (!(target instanceof LivingEntity) || !(damager instanceof Player)) {
            return;
        }

        LivingEntity livingEntityTarget = (LivingEntity) target;
        Player playerDamager = (Player) damager;
        int itemDamageRange = getItemDamageRange(playerDamager.getItemInHand());


        if (itemDamageRange > 0) {
            List<Entity> entities = livingEntityTarget.getNearbyEntities(itemDamageRange, itemDamageRange, itemDamageRange);

            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).damage(event.getFinalDamage());
                }
            }
        }
    }

    private int getItemDamageRange(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return -1;
        }

        List<String> lores = item.getItemMeta().getLore();

        if (lores == null || lores.size() == 0) {
            return -1;
        }

        for (String lore : lores) {
            if (lore.matches(loreRegex)) {
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(lore);

                if (matcher.find()) {
                    return Integer.parseInt(matcher.group());
                }
            }
        }

        return -1;
    }
}
