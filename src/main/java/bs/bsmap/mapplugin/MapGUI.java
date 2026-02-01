package bs.bsmap.mapplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class MapGUI {
    private final Main plugin;
    public MapGUI(Main plugin) { this.plugin = plugin; }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, t("Java-UI.Main.Title"));
        Material mat = Material.matchMaterial(plugin.getConfig().getString("Java-UI.Main.City-Button-Material", "BRICKS"));
        inv.setItem(13, createItem(mat != null ? mat : Material.BRICKS, t("Java-UI.Main.City-Button-Name"), -1));
        player.openInventory(inv);
    }

    public void openCitiesMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, t("Java-UI.Cities.Title"));
        inv.setItem(45, backBtn(null));
        ConfigurationSection sec = plugin.getCityConfig().getConfigurationSection("Städte");
        if (sec != null) for (String k : sec.getKeys(false)) inv.addItem(createIcon("Städte." + k));
        player.openInventory(inv);
    }

    public void openWarpMenu(Player player, String city) {
        Inventory inv = Bukkit.createInventory(null, 54, t("Java-UI.Warps.Title").replace("%city%", city));
        inv.setItem(45, backBtn(city));
        ConfigurationSection sec = plugin.getCityConfig().getConfigurationSection("Städte." + city + ".Warps");
        if (sec != null) for (String k : sec.getKeys(false)) inv.addItem(createIcon("Städte." + city + ".Warps." + k));
        player.openInventory(inv);
    }

    private ItemStack createIcon(String path) {
        Material m = Material.matchMaterial(plugin.getCityConfig().getString(path + ".Icon", "PAPER"));
        int cmd = plugin.getCityConfig().contains(path + ".CustomModelData") ? plugin.getCityConfig().getInt(path + ".CustomModelData") : -1;
        return createItem(m != null ? m : Material.PAPER, "§b§l" + path.substring(path.lastIndexOf(".") + 1), cmd);
    }

    private ItemStack createItem(Material m, String n, int cmd) {
        ItemStack i = new ItemStack(m); ItemMeta meta = i.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(n);
            if (cmd != -1) meta.setCustomModelData(cmd);
            i.setItemMeta(meta);
        }
        return i;
    }

    private ItemStack backBtn(String d) {
        ItemStack i = createItem(Material.ARROW, t("Java-UI.Back-Button-Name"), -1);
        if (d != null) {
            ItemMeta m = i.getItemMeta(); List<String> l = new ArrayList<>();
            l.add("§8" + d); m.setLore(l); i.setItemMeta(m);
        }
        return i;
    }
    private String t(String p) { return plugin.getConfig().getString(p, "").replace("&", "§"); }
}