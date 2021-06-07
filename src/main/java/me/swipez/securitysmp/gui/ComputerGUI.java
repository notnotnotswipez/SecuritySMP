package me.swipez.securitysmp.gui;

import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.stored.SaleManager;
import me.swipez.securitysmp.stored.StoredStats;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ComputerGUI {

    public static Inventory generateCameraInventory(Player player, UUID computerOwnerID){
        Inventory inventory = Bukkit.createInventory(player, 45, ChatColor.BLACK+"Computer: Cameras");
        fillInventoryWithCameras(inventory, computerOwnerID);
        inventory.setItem(44, ItemManager.BACK_BED);

        for (int i = 0; i < inventory.getSize(); i++){
            if (inventory.getItem(i) == null){
                inventory.setItem(i, generateItem(Material.BLACK_STAINED_GLASS_PANE, " "));
            }
        }

        return inventory;
    }

    public static Inventory generateBuyerInventory(int cost, Player player, ItemStack centerItem){
        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.GREEN+"$"+cost);
        inventory.setItem(10, generateItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN+"+$10"));
        inventory.setItem(11, generateItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN+"+$100"));

        inventory.setItem(15, generateItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED+"-$100"));
        inventory.setItem(16, generateItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED+"-$10"));

        for (int i = 0; i < inventory.getSize(); i++){
            if (inventory.getItem(i) == null){
                inventory.setItem(i, generateItem(Material.BLACK_STAINED_GLASS_PANE, " "));
            }
        }

        inventory.clear(13);
        inventory.setItem(13, centerItem);
        inventory.setItem(22, ItemManager.SALE_CONFIRM);

        return inventory;
    }

    public static Inventory generateShopInventory(Player player, int page){
        Inventory inventory = Bukkit.createInventory(player, 36, ChatColor.BLACK+"Computer: "+ChatColor.YELLOW+"Mine"+ChatColor.RED+"Bay");
        fillShop(inventory, page);
        if (shouldMakeNextIcon(page)){
            inventory.setItem(35, ItemManager.NEXT_PAGE);
        }
        if (page > 0){
            inventory.setItem(34, ItemManager.PREV_PAGE);
        }
        inventory.setItem(33, ItemManager.SALE_ITEM);
        for (int i = 0; i < inventory.getSize(); i++){
            if (inventory.getItem(i) == null){
                inventory.setItem(i, generateItem(Material.BLACK_STAINED_GLASS_PANE, " "));
            }
        }
        return inventory;
    }

    public static Inventory generateHackingInventory(Player player){
        Inventory inventory = Bukkit.createInventory(player, 18, ChatColor.BLACK+"Computer: Terminal");
        fillInventoryWithPlayers(inventory, player.getUniqueId());

        for (int i = 0; i < inventory.getSize(); i++){
            if (inventory.getItem(i) == null){
                inventory.setItem(i, generateItem(Material.BLACK_STAINED_GLASS_PANE, " "));
            }
        }
        inventory.setItem(17, ItemManager.BACK_BED);
        return inventory;
    }

    public static Inventory generateComputerInventory(Player player, boolean isHacking){
        String title = ChatColor.BLACK+"Computer";
        if (isHacking){
            title = title + ChatColor.RED+" [DDoSSing]";
        }
        Inventory inventory = Bukkit.createInventory(player, 36, title);

        for (int i = 0; i < 27; i++){
            inventory.setItem(i, generateItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " "));
        }

        for (int i = 27; i < 36; i++){
            inventory.setItem(i, generateItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        inventory.setItem(0, generateSkull("Constantium", ChatColor.YELLOW+"Terminal"));
        inventory.setItem(9, generateSkull("Wrelks", ChatColor.YELLOW+"Browser"));
        inventory.setItem(18, generateSkull("spectator__dead", ChatColor.YELLOW+"Cameras"));

        return inventory;
    }
    private static boolean shouldMakeNextIcon(int page){
        int pageCheck = (page+1)*33;
        return (SaleManager.shopItems.size() - pageCheck) > 0;
    }

    private static ItemStack generateSkull(String player, String itemName){

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setOwner(player);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private static ItemStack generateItem(Material material, String name){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private static void fillShop(Inventory inventory, int page){
        int pageStarter = (inventory.getSize()-3)*page;
        int placedItems = 0;
        for (int i = pageStarter; i < SaleManager.shopItems.size(); i++){
            if (placedItems != inventory.getSize()-3){
                inventory.addItem(SaleManager.shopItems.get(i));
                placedItems++;
            }
        }
    }

    private static void fillInventoryWithPlayers(Inventory inventory, UUID ignored){
        Set<UUID> playersUUIDs = new HashSet<>();
        for (Location location : StoredStats.computers.keySet()){
            String string = StoredStats.computers.get(location);
            String[] split = string.split(";");
            playersUUIDs.add(UUID.fromString(split[0]));
        }
        playersUUIDs.remove(ignored);
        for (UUID uuid : playersUUIDs){
            try {
                OfflinePlayer player = Bukkit.getPlayer(uuid);
                inventory.addItem(generateSkull(player.getName(), ChatColor.RED+"DDoSS "+player.getName()));
            }
            catch (NullPointerException exception){
                inventory.addItem(generateItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED+"Player Offline"));
            }
        }
    }

    private static void fillInventoryWithCameras(Inventory inventory, UUID playerCams){
        int amountOfCams = 0;
        for (Location location : StoredStats.cameras.keySet()){
            String string = StoredStats.cameras.get(location);
            if (string.contains(playerCams.toString())){
                amountOfCams++;
            }
        }
        for (int i = 0; i < amountOfCams; i++){
            inventory.addItem(generateCamera(i+1));
        }
    }

    private static ItemStack generateCamera(int number){
        return generateSkull("spectator__dead", ChatColor.RED+"Camera #"+number);
    }
}
