package me.swipez.securitysmp.listeners;

import me.swipez.securitysmp.gui.KeyPadGUI;
import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.stored.StoredStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onPlayerClicksInventory(InventoryClickEvent event){
        if (event.getClickedInventory() == null){
            return;
        }
        if (!event.getClickedInventory().contains(ItemManager.BACK_BED)){
            return;
        }
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem().equals(ItemManager.BACK_BED)) {
            if (event.getClickedInventory().getSize() == 54) {
                player.openInventory(KeyPadGUI.generateKeyPadInv("", player, false));
            }
        }
        try {
            if (title.toLowerCase().contains("combination:") && !title.toLowerCase().contains("pick")) {
                String digits = title.substring(17);
                String actualNumber = digits.replace(" _", "");
                int digit = 0;
                String name = event.getCurrentItem().getItemMeta().getDisplayName();
                digit = Integer.parseInt(name.substring(2));
                String string = actualNumber + digit;
                if (string.length() < 4) {
                    player.openInventory(KeyPadGUI.generateKeyPadInv(string, player, false));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                } else {
                    Block block = player.getTargetBlock(null, 6);
                    if (checkForKey(block, string)) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "You got in!");
                        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                        Door door = (Door) block.getBlockData();
                        door.setOpen(!door.isOpen());
                        block.setBlockData(door);
                    } else {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "You didnt get in!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
            }
            else if (title.toLowerCase().contains("pick a combination:")){
                String digits = title.substring(24);
                String actualNumber = digits.replace(" _", "");
                int digit = 0;
                String name = event.getCurrentItem().getItemMeta().getDisplayName();
                digit = Integer.parseInt(name.substring(2));
                String string = actualNumber+digit;
                if (string.length() < 4){
                    player.openInventory(KeyPadGUI.generateKeyPadInv(string, player, true));
                    player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                }
                else {
                    Block block = player.getTargetBlock(null, 6);
                    setDoorKey(block, string);
                    player.openInventory(KeyPadGUI.generateKeyPadInv(string, player, true));
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED+"Combination set to: "+string);
                }
            }
        }
        catch (Exception exception){
            // Ignore
        }
        event.setCancelled(true);
    }

    public boolean checkForKey(Block block, String key){
        String string = null;
        Location originLocation = block.getLocation();
        Location doubleLocation = originLocation.subtract(0,1,0);
        if (StoredStats.doorTypes.containsKey(doubleLocation)){
            String[] split = StoredStats.doorTypes.get(doubleLocation).split(";");
            string = split[2];
            return key.equals(string);
        }
        else if (StoredStats.doorTypes.containsKey(originLocation)){
            String[] split = StoredStats.doorTypes.get(originLocation).split(";");
            string = split[2];
            return key.equals(string);
        }
        return false;
    }

    public void setDoorKey(Block block, String key){
        String string = null;
        Location originLocation = block.getLocation();
        Location doubleLocation = originLocation.subtract(0,1,0);
        if (StoredStats.doorTypes.containsKey(doubleLocation)){
            string = StoredStats.doorTypes.get(doubleLocation);
            StoredStats.doorTypes.put(doubleLocation, string.replace(";0000", ";"+key));

        }
        else if (StoredStats.doorTypes.containsKey(originLocation)){
            string = StoredStats.doorTypes.get(originLocation);
            StoredStats.doorTypes.put(originLocation, string.replace(";0000", ";"+key));
        }
    }
}
