package me.swipez.securitysmp.configs;

import me.swipez.securitysmp.SecuritySMP;
import me.swipez.securitysmp.utils.ConfigGenerator;

public class ConfigManager {

    public static ConfigGenerator storedStatsConfig = new ConfigGenerator(SecuritySMP.plugin.getDataFolder(), "stored_blocks");
    public static ConfigGenerator sellingItems = new ConfigGenerator(SecuritySMP.plugin.getDataFolder(), "selling_items");
    public static ConfigGenerator teams = new ConfigGenerator(SecuritySMP.plugin.getDataFolder(), "teams");

}
