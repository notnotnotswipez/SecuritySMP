package me.swipez.securitysmp.stored;

import me.swipez.securitysmp.configs.ConfigManager;
import me.swipez.securitysmp.teams.Team;
import me.swipez.securitysmp.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import sun.security.krb5.Config;

import java.io.IOException;
import java.util.*;

public class StoredStats {

    public static Set<Location> cellTowerLocations = new HashSet<>();
    public static HashMap<Location, String> homeCenters = new HashMap<>();
    public static HashMap<Location, String> doorTypes = new HashMap<>();
    public static HashMap<Location, String> computers = new HashMap<>();
    public static HashMap<Location, String> cameras = new HashMap<>();

    public static void saveStats(){
        System.out.println("[SecuritySMP] Saving player data to yml.");

        ConfigManager.storedStatsConfig.getConfig().set("home_centers", null);
        ConfigManager.storedStatsConfig.getConfig().set("computers", null);
        ConfigManager.storedStatsConfig.getConfig().set("cameras", null);
        ConfigManager.storedStatsConfig.getConfig().set("computers", null);
        ConfigManager.storedStatsConfig.getConfig().set("doors", null);

        ConfigManager.sellingItems.getConfig().set("items", null);

        ConfigManager.teams.getConfig().set("teams", null);

        List<String> cellTowerStrings = new ArrayList<>();
        for (Location location : cellTowerLocations){
            String determinedString = location.getWorld().getName()+";"+(int)location.getX()+";"+(int)location.getY()+";"+(int)location.getZ();
            cellTowerStrings.add(determinedString.replace(".0", ""));
        }
        ConfigManager.storedStatsConfig.getConfig().set("cell_towers", cellTowerStrings);
        for (Location location : homeCenters.keySet()){
            String determinedString = location.getWorld().getName()+";"+(int)location.getX()+";"+(int)location.getY()+";"+(int)location.getZ();
            ConfigManager.storedStatsConfig.getConfig().set("home_centers."+determinedString.replace(".0", ""), homeCenters.get(location));
        }
        for (Location location : computers.keySet()){
            String determinedString = location.getWorld().getName()+";"+(int)location.getX()+";"+(int)location.getY()+";"+(int)location.getZ();
            ConfigManager.storedStatsConfig.getConfig().set("computers."+determinedString.replace(".0", ""), computers.get(location));
        }
        for (Location location : cameras.keySet()){
            String determinedString = location.getWorld().getName()+";"+(int)location.getX()+";"+(int)location.getY()+";"+(int)location.getZ();
            ConfigManager.storedStatsConfig.getConfig().set("cameras."+determinedString.replace(".0", ""), cameras.get(location));
        }
        for (Location location : doorTypes.keySet()){
            String determinedString = location.getWorld().getName()+";"+(int)location.getX()+";"+(int)location.getY()+";"+(int)location.getZ();
            ConfigManager.storedStatsConfig.getConfig().set("doors."+determinedString.replace(".0", ""), doorTypes.get(location));
        }
        for (UUID uuid : TeamManager.playerTeams.keySet()){
            Set<UUID> team = TeamManager.playerTeams.get(uuid).getTeamMembers();
            List<String> teamMembersString = new ArrayList<>();
            for (UUID teamUuid : team){
                teamMembersString.add(teamUuid.toString());
            }
            ConfigManager.teams.getConfig().set("teams."+uuid.toString(), teamMembersString);
        }
        int index = 0;
        for (ItemStack itemStack : SaleManager.shopItems){
            ConfigManager.sellingItems.getConfig().set("items."+index, itemStack);
            index++;
        }
        try {
            ConfigManager.teams.saveConfig();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            ConfigManager.storedStatsConfig.saveConfig();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            ConfigManager.sellingItems.saveConfig();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void loadStats(){
        System.out.println("[SecuritySMP] Loading player data from yml.");
        // Location String Original
        List<String> cellTowerLocationStrings = ConfigManager.storedStatsConfig.getConfig().getStringList("cell_towers");
        Set<String> itemSubStrings = new HashSet<>();
        Set<String> doorSubStrings = new HashSet<>();
        Set<String> homeCenterSubStrings = new HashSet<>();
        Set<String> computerSubStrings = new HashSet<>();
        Set<String> cameraSubStrings = new HashSet<>();

        Set<String> teamOwners = new HashSet<>();

        // Sub Strings

        try {
            teamOwners = ConfigManager.teams.getConfig().getConfigurationSection("teams").getKeys(false);
        }
        catch (NullPointerException exception){
            // ignore
        }

        try {
            itemSubStrings = ConfigManager.sellingItems.getConfig().getConfigurationSection("items").getKeys(false);
        }
        catch (NullPointerException exception){
            // ignore
        }

        try {
            doorSubStrings = ConfigManager.storedStatsConfig.getConfig().getConfigurationSection("doors").getKeys(false);
            System.out.println("Found Door substrings");
        }
        catch (NullPointerException exception){
            // ignore
        }

        try {
            homeCenterSubStrings = ConfigManager.storedStatsConfig.getConfig().getConfigurationSection("home_centers").getKeys(false);
        }
        catch (NullPointerException exception){
            // ignore
        }

        try {
            computerSubStrings = ConfigManager.storedStatsConfig.getConfig().getConfigurationSection("computers").getKeys(false);
        }
        catch (NullPointerException exception){
            // ignore
        }

        try {
            cameraSubStrings = ConfigManager.storedStatsConfig.getConfig().getConfigurationSection("cameras").getKeys(false);
        }
        catch (NullPointerException exception){
            // ignore
        }


        // Temp location Lists
        Set<Location> cellTowerLocationTemp = new HashSet<>();
        for (String string : itemSubStrings){
            if (!string.contains("*id")){
                ItemStack itemStack = ConfigManager.sellingItems.getConfig().getItemStack("items."+string);
                SaleManager.shopItems.add(itemStack);
            }
            else {
                ConfigManager.sellingItems.getConfig().set("items."+string, null);
            }
        }

        for (String string : cellTowerLocationStrings){
            String[] split = string.split(";");
            Location location = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            cellTowerLocationTemp.add(location);
        }

        for (String string : cameraSubStrings) {
            String[] split = string.split(";");
            Location location = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            cameras.put(location, ConfigManager.storedStatsConfig.getConfig().getString("cameras."+string));
        }

        for (String string : computerSubStrings) {
            String[] split = string.split(";");
            Location location = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            computers.put(location, ConfigManager.storedStatsConfig.getConfig().getString("computers."+string));
        }

        for (String string : doorSubStrings) {
            String[] split = string.split(";");
            Location location = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            doorTypes.put(location, ConfigManager.storedStatsConfig.getConfig().getString("doors."+string));
        }

        for (String string : homeCenterSubStrings) {
            String[] split = string.split(";");
            Location location = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            homeCenters.put(location, ConfigManager.storedStatsConfig.getConfig().getString("home_centers."+string));
        }

        for (String string : teamOwners){
            List<String> members = ConfigManager.teams.getConfig().getStringList("teams."+string);
            UUID owner = UUID.fromString(string);
            Set<UUID> allMembers = new HashSet<>();
            for (String member : members){
                UUID uuid = UUID.fromString(member);
                allMembers.add(uuid);
            }
            TeamManager.playerTeams.put(owner, new Team(owner));
            for (UUID memberAdding : allMembers){
                TeamManager.addPlayerToOwnerTeam(owner, memberAdding);
            }
        }

        // Application
        cellTowerLocations = cellTowerLocationTemp;
    }
}
