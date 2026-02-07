package bs.bsmap.mapplugin;

import org.bukkit.Bukkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final Main plugin;
    private final int resourceId;

    public UpdateChecker(Main plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    // Diese Methode entscheidet, welchen Scheduler der Server nutzt
    public void getVersion(final Consumer<String> consumer) {
        if (plugin.isFolia()) {
            // Für Folia-Server
            Bukkit.getAsyncScheduler().runNow(this.plugin, (task) -> {
                checkLogic(consumer);
            });
        } else {
            // Für normales Paper / Spigot
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                checkLogic(consumer);
            });
        }
    }

    // Hier liegt die eigentliche URL-Abfrage
    private void checkLogic(final Consumer<String> consumer) {
        try (InputStream inputStream = new URL("https://api.modrinth.com/v2/project/bmaps/version").openStream();
             Scanner scanner = new Scanner(inputStream)) {

            if (scanner.hasNextLine()) {
                String response = scanner.useDelimiter("\\A").next();
                if (response.contains("\"name\":\"")) {
                    String part = response.split("\"name\":\"")[1];
                    String latestVersion = part.split("\"")[0];
                    consumer.accept(latestVersion);
                }
            }
        } catch (IOException exception) {
            plugin.getLogger().info("Update-Check fehlgeschlagen: " + exception.getMessage());
        }
    }
}