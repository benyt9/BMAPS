package bs.bsmap.mapplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class BedrockUI {

    public static void openMain(Player p, Main plugin) {
        GeyserConnection conn = GeyserApi.api().connectionByUuid(p.getUniqueId());
        if (conn == null) return;
        List<String> btns = new ArrayList<>();
        btns.add(t(plugin, "Bedrock-UI.Main-Button-Cities"));
        send(p, conn, t(plugin, "Bedrock-UI.Main-Title"), t(plugin, "Bedrock-UI.Main-Content"), btns, plugin, "main", "");
    }

    public static void openCities(Player p, GeyserConnection conn, Main plugin) {
        List<String> btns = new ArrayList<>();
        ConfigurationSection sec = plugin.getCityConfig().getConfigurationSection("Städte");
        if (sec != null) btns.addAll(sec.getKeys(false));
        btns.add(t(plugin, "Bedrock-UI.Back-Button"));
        send(p, conn, t(plugin, "Bedrock-UI.Cities-Title"), t(plugin, "Bedrock-UI.Cities-Content"), btns, plugin, "cities", "");
    }

    public static void openWarps(Player p, GeyserConnection conn, Main plugin, String city) {
        List<String> btns = new ArrayList<>();
        ConfigurationSection sec = plugin.getCityConfig().getConfigurationSection("Städte." + city + ".Warps");
        if (sec != null) btns.addAll(sec.getKeys(false));
        btns.add(t(plugin, "Bedrock-UI.Back-Button"));
        send(p, conn, t(plugin, "Bedrock-UI.Warps-Title").replace("%city%", city), t(plugin, "Bedrock-UI.Warps-Content").replace("%city%", city), btns, plugin, "warps", city);
    }

    private static void send(Player p, GeyserConnection conn, String title, String content, List<String> btns, Main plugin, String ctx, String city) {
        try {
            ClassLoader loader = conn.getClass().getClassLoader();
            Class<?> simpleForm = Class.forName("org.geysermc.cumulus.form.SimpleForm", true, loader);

            Object builder = simpleForm.getMethod("builder").invoke(null);
            builder.getClass().getMethod("title", String.class).invoke(builder, title);
            builder.getClass().getMethod("content", String.class).invoke(builder, content);
            for (String b : btns) builder.getClass().getMethod("button", String.class).invoke(builder, b);

            Class<?> consumer = Class.forName("java.util.function.Consumer", true, loader);
            Object handler = Proxy.newProxyInstance(loader, new Class[]{consumer}, (proxy, method, args) -> {
                if (method.getName().equals("accept") && args[0] != null) {
                    try {
                        Object response = args[0];
                        Method getIdx = response.getClass().getMethod("clickedButtonId");
                        int id = (int) getIdx.invoke(response);
                        Bukkit.getScheduler().runTask(plugin, () -> handle(p, conn, plugin, id, ctx, city, btns.size()));
                    } catch (Exception e) { e.printStackTrace(); }
                }
                return null;
            });

            builder.getClass().getMethod("validResultHandler", consumer).invoke(builder, handler);
            Object form = builder.getClass().getMethod("build").invoke(builder);

            // Brutaler Fix für den Mismatch: Wir suchen die Methode, die wirklich dieses Objekt nimmt
            boolean sent = false;
            for (Method m : conn.getClass().getMethods()) {
                if (m.getName().equals("sendForm") && m.getParameterCount() == 1) {
                    try {
                        m.invoke(conn, form);
                        sent = true;
                        break;
                    } catch (Exception ignored) {}
                }
            }
            if (!sent) Bukkit.getLogger().warning("[BMaps] Konnte sendForm Methode nicht korrekt aufrufen!");

        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void handle(Player p, GeyserConnection conn, Main plugin, int id, String ctx, String city, int size) {
        boolean back = (id == size - 1);
        if (ctx.equals("main")) { if (id == 0) openCities(p, conn, plugin); }
        else if (ctx.equals("cities")) {
            if (back) openMain(p, plugin);
            else {
                List<String> k = new ArrayList<>(plugin.getCityConfig().getConfigurationSection("Städte").getKeys(false));
                if (id < k.size()) openWarps(p, conn, plugin, k.get(id));
            }
        } else if (ctx.equals("warps")) {
            if (back) openCities(p, conn, plugin);
            else {
                List<String> k = new ArrayList<>(plugin.getCityConfig().getConfigurationSection("Städte." + city + ".Warps").getKeys(false));
                if (id < k.size()) p.teleport(getLoc(plugin, city, k.get(id)));
            }
        }
    }

    private static org.bukkit.Location getLoc(Main pl, String c, String w) {
        String p = "Städte." + c + ".Warps." + w;
        return new org.bukkit.Location(Bukkit.getWorld(pl.getCityConfig().getString(p + ".World")), pl.getCityConfig().getDouble(p + ".X"), pl.getCityConfig().getDouble(p + ".Y"), pl.getCityConfig().getDouble(p + ".Z"), (float) pl.getCityConfig().getDouble(p + ".Yaw"), (float) pl.getCityConfig().getDouble(p + ".Pitch"));
    }

    private static String t(Main pl, String p) { return pl.getConfig().getString(p, "").replace("&", "§"); }
}