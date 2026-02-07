package bs.bsmap.mapplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    private File cityFile;
    private FileConfiguration cityConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        exportLanguageFiles();
        createCityConfig();

        getCommand("map").setExecutor(new MapCommand(this));
        getServer().getPluginManager().registerEvents(new MapListener(this), this);

        // Hier muss der Update-Checker rein!
        new UpdateChecker(this, 12345).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                getLogger().warning("Update verfügbar! Aktuelle Version: " + version);
                getLogger().warning("Link: https://modrinth.com/plugin/bmaps");
            }
        }); // Hier fehlten );
    }

    private void exportLanguageFiles() {
        File configsFolder = new File(getDataFolder(), "configs");
        if (!configsFolder.exists()) {
            configsFolder.mkdirs();
        }
        saveResource("configs/config-de.yml", false);
        saveResource("configs/config-en.yml", false);
    }

    public void createCityConfig() {
        cityFile = new File(getDataFolder(), "cities.yml");
        if (!cityFile.exists()) saveResource("cities.yml", false);
        cityConfig = YamlConfiguration.loadConfiguration(cityFile);
    }

    public boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregionscheduler.RegionScheduler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public FileConfiguration getCityConfig() { return cityConfig; }

    public void saveCityConfig() {
        try {
            cityConfig.save(cityFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}