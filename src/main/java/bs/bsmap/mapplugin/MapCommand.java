package bs.bsmap.mapplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.geyser.api.GeyserApi;

public class MapCommand implements CommandExecutor {
    private final Main plugin;
    public MapCommand(Main plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String p = t("Messages.Prefix");
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bmaps.admin.reload")) { sender.sendMessage(p + t("Messages.No-Permission")); return true; }
            plugin.reloadConfig(); plugin.createCityConfig();
            sender.sendMessage(p + t("Messages.Reload-Success")); return true;
        }
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length == 0) { openUI(player); return true; }

        String sub = args[0].toLowerCase();
        if (sub.contains("city") || sub.contains("warp")) {
            if (sub.startsWith("add") || sub.startsWith("update")) {
                if (!player.hasPermission("bmaps.admin.update")) { player.sendMessage(p + t("Messages.No-Permission")); return true; }
                handleSave(player, args, sub, p);
            } else if (sub.startsWith("remove")) {
                if (!player.hasPermission("bmaps.admin.remove")) { player.sendMessage(p + t("Messages.No-Permission")); return true; }
                handleDelete(player, args, sub, p);
            }
            return true;
        }
        return true;
    }

    private void handleSave(Player player, String[] args, String sub, String p) {
        if (args.length < 2) { player.sendMessage(p + (sub.contains("city") ? t("Messages.Usage-AddCity") : t("Messages.Usage-AddWarp"))); return; }
        String path = sub.contains("city") ? "Städte." + args[1] : "Städte." + args[1] + ".Warps." + args[2];
        if (sub.contains("warp")) {
            Location l = player.getLocation();
            plugin.getCityConfig().set(path + ".World", l.getWorld().getName());
            plugin.getCityConfig().set(path + ".X", l.getX()); plugin.getCityConfig().set(path + ".Y", l.getY()); plugin.getCityConfig().set(path + ".Z", l.getZ());
            plugin.getCityConfig().set(path + ".Yaw", (double) l.getYaw()); plugin.getCityConfig().set(path + ".Pitch", (double) l.getPitch());
        }
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() != Material.AIR) {
            plugin.getCityConfig().set(path + ".Icon", hand.getType().name());
            if (hand.hasItemMeta() && hand.getItemMeta().hasCustomModelData()) {
                plugin.getCityConfig().set(path + ".CustomModelData", hand.getItemMeta().getCustomModelData());
            } else { plugin.getCityConfig().set(path + ".CustomModelData", null); }
        }
        plugin.saveCityConfig();
        player.sendMessage(p + (sub.startsWith("add") ? t("Messages.City-Created").replace("%city%", args[1]) : t("Messages.Update-Success").replace("%target%", args[1])));
    }

    private void handleDelete(Player player, String[] args, String sub, String p) {
        if (args.length < 2) { player.sendMessage(p + t("Messages.Usage-Remove")); return; }
        String path = sub.equals("removecity") ? "Städte." + args[1] : "Städte." + args[1] + ".Warps." + args[2];
        plugin.getCityConfig().set(path, null); plugin.saveCityConfig();
        player.sendMessage(p + (sub.equals("removecity") ? t("Messages.City-Removed").replace("%city%", args[1]) : t("Messages.Warp-Removed").replace("%warp%", args[args.length-1])));
    }

    private void openUI(Player player) {
        if (GeyserApi.api().isBedrockPlayer(player.getUniqueId())) BedrockUI.openMain(player, plugin);
        else new MapGUI(plugin).openMainMenu(player);
    }
    private String t(String path) { return plugin.getConfig().getString(path, "").replace("&", "§"); }
}