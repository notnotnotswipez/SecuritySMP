package me.swipez.securitysmp.listeners;

import me.swipez.securitysmp.gui.ComputerGUI;
import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.stored.CameraManager;
import me.swipez.securitysmp.stored.StoredStats;
import me.swipez.securitysmp.teams.TeamManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class HomeListener implements Listener {

    Random random = new Random();

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        String action = withinRadiusOfHome(event.getEntity().getUniqueId(), event.getEntity().getLocation().getBlock(), false);
        if (action.contains("cancel")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerUseComputerOrWater(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.hasItem()){
            ItemStack itemStack = event.getItem();
            if (itemStack.isSimilar(ItemManager.BACK_BED)){
                CameraManager.removePlayerCam(player);
                event.setCancelled(true);
            }
            if (itemStack.isSimilar(ItemManager.FLIP_CAMERA)){
                CameraManager.cameraLocation.get(player.getUniqueId()).setDirection(CameraManager.cameraLocation.get(player.getUniqueId()).getDirection().multiply(-1));
                event.setCancelled(true);
            }
            if (itemStack.getType().toString().contains("BUCKET")){
                if (event.hasBlock()){
                    String action = withinRadiusOfHome(player, event.getClickedBlock(), false);
                    if (action.contains("cancel") || action.contains("nodrop")){
                        event.setCancelled(true);
                    }
                }
            }
        }
        if (event.hasBlock()){
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                Location location = event.getClickedBlock().getLocation();
                if (StoredStats.computers.containsKey(location)){
                    String action = playerOwnsComputer(player, event.getClickedBlock());
                    String acceptance = withinRadiusOfHome(player, event.getClickedBlock(), false);
                    String string = StoredStats.computers.get(location);
                    String[] split = string.split(";");
                    if (action.contains("allow") && acceptance.contains("within")){
                        boolean isHacking = false;
                        ComputerInterfaceListener.currentPages.put(player.getUniqueId(), 0);
                        if (ComputerInterfaceListener.ddosTimes.containsKey(player.getUniqueId())){
                            isHacking = true;
                            if (ComputerInterfaceListener.ddosTimes.get(player.getUniqueId()) == 0){
                                ComputerInterfaceListener.triggerDDOSS(player, ComputerInterfaceListener.hackerVictim.get(player.getUniqueId()), random);
                                ComputerInterfaceListener.ddosTimes.remove(player.getUniqueId());
                                ComputerInterfaceListener.computerLocations.remove(player.getUniqueId());
                                ComputerInterfaceListener.hackerVictim.remove(player.getUniqueId());
                            }
                        }
                        event.setCancelled(true);
                        setLatestComputer(UUID.fromString(split[0]), event.getClickedBlock());
                        player.openInventory(ComputerGUI.generateComputerInventory(player, isHacking));
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        player.sendMessage(ChatColor.RED+"Unauthorized User.");
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String action = withinRadiusOfHome(player, event.getBlock(), false);
        if (action.contains("allow")){
            if (StoredStats.computers.containsKey(event.getBlock().getLocation())){
                event.setCancelled(true);
            }
            if (StoredStats.cameras.containsKey(event.getBlock().getLocation())){
                event.setDropItems(false);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), ItemManager.CAMERA);
                StoredStats.cameras.remove(event.getBlock().getLocation());
            }
        }
        if (action.contains("obscured")){
            return;
        }
        if (action.contains("cancel")) {
            event.setCancelled(true);
        }
        if (action.equals("destroy;nodrop")) {
            event.setDropItems(false);
            player.sendMessage(ChatColor.RED + "You destroyed someones Cell Tower!");
        }
        if (action.equals("destroy;drop")) {
            event.setDropItems(false);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), ItemManager.CELL_TOWER);
            player.sendMessage(ChatColor.RED + "You destroyed your own Cell Tower!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBreakDoor(BlockBreakEvent event) {
        if (event.getBlock().getType().toString().contains("_DOOR")) {
            Block block = event.getBlock();
            String string = shouldCancelDoorBreak(event.getBlock(), event.getPlayer());
            if (string.contains("cancel")) {
                event.setCancelled(true);
            }
            if (string.contains("ownerBypass")) {
                String[] split = string.split(";");
                String type = split[2];
                if (type.equals("eyescan")) {
                    block.getWorld().dropItemNaturally(block.getLocation(), ItemManager.EYESCANNER_DOOR);
                    event.setDropItems(false);
                }
                if (type.equals("locked")) {
                    block.getWorld().dropItemNaturally(block.getLocation(), ItemManager.KEYPAD_DOOR);
                    event.setDropItems(false);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPlaceKey(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)) {
            if (event.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("key")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String action = withinRadiusOfHome(player, event.getBlock(), true);
        if (action.contains("cancel")) {
            event.setCancelled(true);
        }
    }

    public String shouldCancelDoorBreak(Block block, Player breaker) {
        String string = "allow";
        Location originLocation = block.getLocation();
        Location doubleLocation = originLocation.clone().subtract(0, 1, 0);
        Location aboveLocation = originLocation.clone().add(0, 1, 0);
        if (StoredStats.doorTypes.containsKey(aboveLocation)) {
            string = "cancel";
            if (StoredStats.doorTypes.get(aboveLocation).contains(breaker.getUniqueId().toString())) {
                string = "ownerBypass" + ";" + StoredStats.doorTypes.get(aboveLocation);
            }
        }
        if (StoredStats.doorTypes.containsKey(doubleLocation)) {
            string = "cancel";
            if (StoredStats.doorTypes.get(doubleLocation).contains(breaker.getUniqueId().toString())) {
                string = "ownerBypass" + ";" + StoredStats.doorTypes.get(doubleLocation);
            }
        }
        if (StoredStats.doorTypes.containsKey(originLocation)) {
            string = "cancel";
            if (StoredStats.doorTypes.get(originLocation).contains(breaker.getUniqueId().toString())) {
                string = "ownerBypass" + ";" + StoredStats.doorTypes.get(originLocation);
            }
        }
        return string;
    }

    public String playerOwnsComputer(Player player, Block computer){
        Location location = computer.getLocation();
        String string = StoredStats.computers.get(location);
        String action = "deny";

        if (teamCheck(string, player)){
            action = "allow";
            if (string.contains("true")){
                action = action+";hacking";
            }
        }

        return action;
    }

    public String withinRadiusOfHome(UUID breaker, Block brokenBlock, boolean bypassRemoval) {
        StringBuilder string = new StringBuilder("allow");
        try {
            boolean withinThirty = false;
            Location blockBreakLocation = brokenBlock.getLocation();
            for (Location location : StoredStats.homeCenters.keySet()){
                String locationString = StoredStats.homeCenters.get(location);
                if (location.distance(blockBreakLocation) < 30){
                    withinThirty = true;
                }
                if (locationString.equals(breaker.toString())){
                    string = new StringBuilder("allow");
                    if (!bypassRemoval){
                        if (brokenBlock.getLocation().equals(location)){
                            StoredStats.homeCenters.remove(location);
                            string = new StringBuilder("destroy;drop");
                        }
                        else {
                            if (withinThirty){
                                string = new StringBuilder("allow;within");
                            }
                        }
                    }
                    withinThirty = false;
                }
                else {
                    if (brokenBlock.getLocation().equals(location)){
                        withinThirty = false;
                        StoredStats.homeCenters.remove(location);
                        string = new StringBuilder("destroy;nodrop");
                    }
                }
                if (withinThirty){
                    break;
                }
            }
            if (withinThirty){
                string = new StringBuilder("cancel");
            }
        }
        catch (ConcurrentModificationException exception){
            // lol
        }
        return string.toString();
    }

    public String withinRadiusOfHome(Player breaker, Block brokenBlock, boolean bypassRemoval) {
        StringBuilder string = new StringBuilder("allow");
        try {
            boolean withinThirty = false;
            Location blockBreakLocation = brokenBlock.getLocation();
            for (Location location : StoredStats.homeCenters.keySet()){
                String locationString = StoredStats.homeCenters.get(location);
                if (location.distance(blockBreakLocation) < 30){
                    withinThirty = true;
                }
                if (teamCheck(locationString, breaker)){
                    string = new StringBuilder("allow");
                    if (!bypassRemoval){
                        if (brokenBlock.getLocation().equals(location)){
                            StoredStats.homeCenters.remove(location);
                            string = new StringBuilder("destroy;drop");
                        }
                        else {
                            if (withinThirty){
                                string = new StringBuilder("allow;within");
                            }
                        }
                    }
                    withinThirty = false;
                }
                else {
                    if (brokenBlock.getLocation().equals(location)){
                        withinThirty = false;
                        StoredStats.homeCenters.remove(location);
                        string = new StringBuilder("destroy;nodrop");
                    }
                }
                if (withinThirty){
                    break;
                }
            }
            if (withinThirty){
                string = new StringBuilder("cancel");
            }
        }
        catch (ConcurrentModificationException exception){
            // lol
        }
        return string.toString();
    }

    private static ItemStack generateSkull(String player, String itemName){

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setDisplayName(itemName);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        meta.setOwner(player);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private void setLatestComputer(UUID player, Block block){
        Location location = block.getLocation();
        for (Location allLocations : StoredStats.computers.keySet()){
            String string = StoredStats.computers.get(allLocations);
            if (string.contains(player.toString())){
                string = string.replace("latest", "old");
            }
            StoredStats.computers.put(allLocations, string);
        }
        String string = StoredStats.computers.get(location);
        string = string.replace("old", "latest");
        StoredStats.computers.put(location, string);
    }

    private boolean teamCheck(String baseString, Player player){
        String[] split = baseString.split(";");
        UUID uuid = player.getUniqueId();
        boolean bool = false;
        UUID owner = UUID.fromString(split[0]);
        if (uuid.equals(owner)){
            bool = true;
        }
        if (TeamManager.hasTeam(owner)){
            if (TeamManager.getPlayerTeam(owner).isInTeam(uuid)){
                bool = true;
            }
        }
        return bool;
    }
}
