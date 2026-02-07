package bs.bsmap.mapplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.geyser.api.GeyserApi;

public class MapCommand implements CommandExecutor {
    private final Main plugin;

    public MapCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = t("Messages.Prefix");

        // Reload Command
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bmaps.admin.reload")) {
                sender.sendMessage(p + t("Messages.No-Permission"));
                return true;
            }
            plugin.reloadConfig();
            plugin.createCityConfig();
            sender.sendMessage(p + t("Messages.Reload-Success"));
            return true;
        }

        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        // UI öffnen
        if (args.length == 0) {
            openUI(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        // Add/Update/Remove Logik
        if (sub.contains("city") || sub.contains("warp")) {
            if (sub.startsWith("add") || sub.startsWith("update")) {
                if (!player.hasPermission("bmaps.admin.update")) {
                    player.sendMessage(p + t("Messages.No-Permission"));
                    return true;
                }
                handleSave(player, args, sub, p);
            } else if (sub.startsWith("remove")) {
                if (!player.hasPermission("bmaps.admin.remove")) {
                    player.sendMessage(p + t("Messages.No-Permission"));
                    return true;
                }
                handleDelete(player, args, sub, p);
            }
            return true;
        }
        return true;
    }

    private void handleSave(Player player, String[] args, String sub, String p) {
        boolean isCity = sub.contains("city");

        // Validierung der Argumente
        if (isCity && args.length < 2) {
            player.sendMessage(p + t("Messages.Usage-AddCity"));
            return;
        }
        if (!isCity && args.length < 3) {
            player.sendMessage(p + t("Messages.Usage-AddWarp"));
            return;
        }

        String path = isCity ? "Städte." + args[1] : "Städte." + args[1] + ".Warps." + args[2];

        // Location nur für Warps speichern
        if (!isCity) {
            Location l = player.getLocation();
            plugin.getCityConfig().set(path + ".World", l.getWorld().getName());
            plugin.getCityConfig().set(path + ".X", l.getX());
            plugin.getCityConfig().set(path + ".Y", l.getY());
            plugin.getCityConfig().set(path + ".Z", l.getZ());
            plugin.getCityConfig().set(path + ".Yaw", (double) l.getYaw());
            plugin.getCityConfig().set(path + ".Pitch", (double) l.getPitch());
        }

        // Icon Logik (für beides)
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() != Material.AIR) {
            plugin.getCityConfig().set(path + ".Icon", hand.getType().name());
            if (hand.hasItemMeta() && hand.getItemMeta().hasCustomModelData()) {
                plugin.getCityConfig().set(path + ".CustomModelData", hand.getItemMeta().getCustomModelData());
            } else {
                plugin.getCityConfig().set(path + ".CustomModelData", null);
            }
        }

        plugin.saveCityConfig();

        // Nachrichten-Ausgabe fixen
        if (sub.startsWith("add")) {
            if (isCity) {
                player.sendMessage(p + t("Messages.City-Created").replace("%city%", args[1]));
            } else {
                player.sendMessage(p + t("Messages.Warp-Created").replace("%warp%", args[2]).replace("%city%", args[1]));
            }
        } else {
            String targetName = isCity ? args[1] : args[2];
            player.sendMessage(p + t("Messages.Update-Success").replace("%target%", targetName));
        }
    }

    private void handleDelete(Player player, String[] args, String sub, String p) {
        if (args.length < 2) {
            player.sendMessage(p + t("Messages.Usage-Remove"));
            return;
        }

        boolean isCity = sub.equals("removecity");
        String path = isCity ? "Städte." + args[1] : "Städte." + args[1] + ".Warps." + args[2];

        plugin.getCityConfig().set(path, null);
        plugin.saveCityConfig();

        if (isCity) {
            player.sendMessage(p + t("Messages.City-Removed").replace("%city%", args[1]));
        } else {
            // Falls args[2] nicht existiert, nehmen wir args[1] als Fallback
            String warpName = (args.length > 2) ? args[2] : "Unbekannt";
            player.sendMessage(p + t("Messages.Warp-Removed").replace("%warp%", warpName).replace("%city%", args[1]));
        }
    }

    private void openUI(Player player) {
        if (GeyserApi.api().isBedrockPlayer(player.getUniqueId())) {
            BedrockUI.openMain(player, plugin);
        } else {
            new MapGUI(plugin).openMainMenu(player);
        }
    }

    private String t(String path) {
        return plugin.getConfig().getString(path, "").replace("&", "§");
    }
}