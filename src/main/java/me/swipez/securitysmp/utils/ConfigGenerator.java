package me.swipez.securitysmp.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigGenerator {

    private final YamlConfiguration config;

    private final File file;

    public ConfigGenerator(File path, String name) {
        file = new File(path.getPath(), name+".yml");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public ConfigGenerator addDefaultToConfig(String configPath, Object value){
        config.addDefault(configPath, value);

        return this;
    }

    public YamlConfiguration getConfig(){
        return config;
    }

    public void saveConfig() throws IOException {
        config.save(file);
    }

    public void reloadConfig() throws IOException, InvalidConfigurationException {
        config.load(file);
    }

    public ConfigGenerator buildConfig(){
        getConfig().options().copyDefaults(true);
        try {
            saveConfig();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return this;
    }
}
