package bs.bsmap.mapplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MapListener implements Listener {
    private final Main plugin;
    public MapListener(Main plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        String mainT = t("Java-UI.Main.Title");
        String cityT = t("Java-UI.Cities.Title");
        String warpT = t("Java-UI.Warps.Title").split("%")[0];

        if (title.contains(mainT) || title.contains(cityT) || title.contains(warpT)) {
            event.setCancelled(true);
            if (event.getClickedInventory() != event.getView().getTopInventory()) return;
            Player p = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (item == null || !item.hasItemMeta()) return;
            String name = item.getItemMeta().getDisplayName();
            MapGUI gui = new MapGUI(plugin);

            if (name.equals(t("Java-UI.Back-Button-Name"))) {
                if (title.contains(cityT)) gui.openMainMenu(p); else gui.openCitiesMenu(p);
            } else if (name.equals(t("Java-UI.Main.City-Button-Name"))) {
                gui.openCitiesMenu(p);
            } else if (title.contains(cityT)) {
                gui.openWarpMenu(p, name.replace("§b§l", ""));
            } else if (title.contains(warpT)) {
                handleTp(p, event, name);
            }
        }
    }

    private void handleTp(Player p, InventoryClickEvent e, String n) {
        ItemStack b = e.getInventory().getItem(45);
        if (b == null || b.getItemMeta().getLore() == null) return;
        String city = b.getItemMeta().getLore().get(0).replace("§8", "");
        String warp = n.replace("§b§l", "");
        String path = "Städte." + city + ".Warps." + warp;
        World w = Bukkit.getWorld(plugin.getCityConfig().getString(path + ".World", "world"));
        if (w != null) {
            Location loc = new Location(w, plugin.getCityConfig().getDouble(path + ".X"), plugin.getCityConfig().getDouble(path + ".Y"), plugin.getCityConfig().getDouble(path + ".Z"), (float) plugin.getCityConfig().getDouble(path + ".Yaw"), (float) plugin.getCityConfig().getDouble(path + ".Pitch"));
            p.teleport(loc);
            p.sendMessage(t("Messages.Prefix") + t("Messages.Teleport-Success").replace("%warp%", warp).replace("%city%", city));
            p.closeInventory();
        }
    }
    private String t(String p) { return plugin.getConfig().getString(p, "").replace("&", "§"); }
}