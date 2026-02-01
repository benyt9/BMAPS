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
        createCityConfig();
        getCommand("map").setExecutor(new MapCommand(this));
        getServer().getPluginManager().registerEvents(new MapListener(this), this);
    }

    public void createCityConfig() {
        cityFile = new File(getDataFolder(), "cities.yml");
        if (!cityFile.exists()) saveResource("cities.yml", false);
        cityConfig = YamlConfiguration.loadConfiguration(cityFile);
    }

    public FileConfiguration getCityConfig() { return cityConfig; }
    public void saveCityConfig() {
        try { cityConfig.save(cityFile); } catch (IOException e) { e.printStackTrace(); }
    }
}