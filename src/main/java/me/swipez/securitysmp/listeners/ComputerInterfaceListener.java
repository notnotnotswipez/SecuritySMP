package me.swipez.securitysmp.listeners;

import me.swipez.securitysmp.gui.ComputerGUI;
import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.stored.CameraManager;
import me.swipez.securitysmp.stored.SaleManager;
import me.swipez.securitysmp.stored.StoredStats;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ComputerInterfaceListener implements Listener {

    public static HashMap<UUID, Integer> ddosTimes = new HashMap<>();
    public static HashMap<UUID, UUID> hackerVictim = new HashMap<>();
    public static HashMap<UUID, Location> computerLocations = new HashMap<>();

    public static HashMap<UUID, Integer> currentPages = new HashMap<>();
    public static HashMap<String, Integer> moneyOwed = new HashMap<>();

    int hackTime = 1800;

    @EventHandler
    public void onPlayerOwedMoney(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (moneyOwed.containsKey(player.getDisplayName())){
            player.sendMessage(ChatColor.GREEN+"Someone bought an item for $"+moneyOwed.get(player.getDisplayName())+" while you we're offline!");
            addMoney(player, moneyOwed.get(player.getDisplayName()));
            moneyOwed.remove(player.getDisplayName());
        }
    }

    @EventHandler
    public void onPlayerUsesMain(InventoryClickEvent event){
        if (event.getClickedInventory() == null){
            return;
        }
        if (!event.getView().getTitle().toLowerCase().contains("computer") && !event.getView().getTitle().toLowerCase().contains("$")){
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        String computerString = StoredStats.computers.get(player.getTargetBlock(null, 5).getLocation());
        String[] computerSplit = computerString.split(";");
        UUID ownerUuid = UUID.fromString(computerSplit[0]);
        if (itemStack.isSimilar(ItemManager.BACK_BED)){
            player.openInventory(ComputerGUI.generateComputerInventory(player, ComputerInterfaceListener.ddosTimes.containsKey(player.getUniqueId())));
        }
        if (event.getClickedInventory().contains(ItemManager.SALE_CONFIRM)){
            if (event.getSlot() != 13){
                if (itemStack.getType().equals(Material.LIME_STAINED_GLASS_PANE) || itemStack.getType().equals(Material.RED_STAINED_GLASS_PANE)){
                    ItemStack itemStack1 = event.getClickedInventory().getItem(13);
                    String name = itemStack.getItemMeta().getDisplayName();
                    int number = Integer.parseInt(name.replace(ChatColor.GREEN.toString(), "").replace("$", "").replace(ChatColor.RED.toString(), ""));
                    int sellingPrice = Integer.parseInt(event.getView().getTitle().replace(ChatColor.GREEN+"$", ""));
                    int determinedPrice = sellingPrice+number;
                    if (determinedPrice > 0){
                        player.openInventory(ComputerGUI.generateBuyerInventory(determinedPrice, player, itemStack1));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                    event.setCancelled(true);
                }
                if (itemStack.isSimilar(ItemManager.SALE_CONFIRM)){
                    int sellingPrice = Integer.parseInt(event.getView().getTitle().replace(ChatColor.GREEN+"$", ""));
                    ItemStack itemStack1 = event.getClickedInventory().getItem(13);
                    if (itemStack1 != null){
                        SaleManager.addShopItem(itemStack1, sellingPrice, player);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        player.openInventory(ComputerGUI.generateShopInventory(player, currentPages.get(player.getUniqueId())));
                    }
                }
                event.setCancelled(true);
            }
        }
        if (event.getClickedInventory().contains(ItemManager.SALE_ITEM)){
            if (itemStack.isSimilar(ItemManager.SALE_ITEM)){
                player.openInventory(ComputerGUI.generateBuyerInventory(0, player, null));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
            if (itemStack.isSimilar(ItemManager.NEXT_PAGE)){
                int nextPage = currentPages.get(player.getUniqueId())+1;
                currentPages.put(player.getUniqueId(), nextPage);
                player.openInventory(ComputerGUI.generateShopInventory(player, nextPage));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
            if (itemStack.isSimilar(ItemManager.PREV_PAGE)){
                int nextPage = currentPages.get(player.getUniqueId())-1;
                currentPages.put(player.getUniqueId(), nextPage);
                player.openInventory(ComputerGUI.generateShopInventory(player, nextPage));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
            }
            if (itemStack.getItemMeta().hasLore()){
                boolean isPriced = false;
                int price = 0;
                String seller = null;
                for (String string : itemStack.getItemMeta().getLore()){
                    if (string.toLowerCase().contains("price: ")){
                        isPriced = true;
                        price = Integer.parseInt(string.replace(ChatColor.GREEN+"Price: ", ""));
                    }
                    if (string.toLowerCase().contains("seller")){
                        seller = string.replace(ChatColor.GREEN+"Seller: ", "");
                    }
                }
                if (isPriced){
                    if (!takeMoney(price, player)){
                        player.sendMessage(ChatColor.RED+"You do not have enough money in your inventory to buy this item!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                    else {
                        Player sellerPlayer = null;
                        boolean foundPerson = false;
                        for (Player loopPlayers : Bukkit.getOnlinePlayers()){
                            if (loopPlayers.getDisplayName().equals(seller)){
                                sellerPlayer = loopPlayers;
                                foundPerson = true;
                                break;
                            }
                        }
                        if (foundPerson){
                            addMoney(sellerPlayer, price);
                            sellerPlayer.sendMessage(ChatColor.GREEN+"Someone bought one of your items for $"+price);
                        }
                        else {
                            moneyOwed.putIfAbsent(seller, 0);
                            moneyOwed.put(seller, moneyOwed.get(seller)+price);
                        }
                        ItemStack resultStack = itemStack.clone();
                        ItemMeta meta = resultStack.getItemMeta();
                        List<String> lore = meta.getLore();
                        lore.remove(lore.get(lore.size()-1));
                        lore.remove(lore.get(lore.size()-1));
                        meta.setLore(lore);
                        resultStack.setItemMeta(meta);
                        addProperly(player, resultStack);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        player.sendMessage(ChatColor.GREEN+"You just bought an item for $"+price);
                        SaleManager.shopItems.remove(itemStack);
                        itemStack.setAmount(0);
                    }
                }
            }
            event.setCancelled(true);
        }
        if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains("ddoss")){
            if (itemStack.getType().equals(Material.PLAYER_HEAD)){

                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

                ddosTimes.put(player.getUniqueId(), hackTime);
                player.closeInventory();
                hackerVictim.put(player.getUniqueId(), meta.getOwningPlayer().getUniqueId());
                player.sendMessage(ChatColor.RED+"DDoSSing "+meta.getOwningPlayer().getName());
                computerLocations.put(player.getUniqueId(), player.getTargetBlock(null, 5).getLocation());

            }
        }
        if (itemStack.getItemMeta().getDisplayName().toLowerCase().contains(ChatColor.RED+"camera")){
            if (itemStack.getType().equals(Material.PLAYER_HEAD)){

                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

                String[] split = meta.getDisplayName().split("#");

                int camNumber = Integer.parseInt(split[1])-1;

                sendPlayerToCam(ownerUuid, camNumber, player);

            }
        }
        if (headCheck(event.getClickedInventory(), ChatColor.YELLOW+"Terminal")){
            if (itemStack.getItemMeta().getDisplayName().contains(ChatColor.YELLOW+"Browser")){
                player.openInventory(ComputerGUI.generateShopInventory(player, 0));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F);
            }
            if (itemStack.getItemMeta().getDisplayName().contains(ChatColor.YELLOW+"Cameras")){
                player.openInventory(ComputerGUI.generateCameraInventory(player, ownerUuid));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F);
            }
            if (event.getView().getTitle().toLowerCase().contains("ddoss")){
                if (itemStack.getItemMeta().getDisplayName().contains(ChatColor.YELLOW+"Terminal")){
                    player.sendMessage(ChatColor.RED+"You are already DDoSSing someone! Wait it out.");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.4F);
                }
            }
            else {
                if (itemStack.getItemMeta().getDisplayName().contains(ChatColor.YELLOW+"Terminal")){
                    player.openInventory(ComputerGUI.generateHackingInventory(player));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F);
                }
            }
            event.setCancelled(true);
        }
    }

    private void addMoney(Player player, int amount){
        for (int i = 0; i < amount; i++){
            addProperly(player, ItemManager.ONE_DOLLAR);
        }
    }

    public static void addProperly(Player player, ItemStack itemStack){
        final Map<Integer, ItemStack> map = player.getInventory().addItem(itemStack);
        for (final ItemStack item : map.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    private boolean takeMoney(int moneyToTake, Player player){
        int playerMoney = 0;
        boolean bool = false;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack != null){
                if (itemStack.hasItemMeta()) {
                    if (itemStack.getItemMeta().hasDisplayName()) {
                        if (itemStack.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "$1")) {
                            if (itemStack.getType().equals(Material.PAPER)) {
                                playerMoney += itemStack.getAmount();
                            }
                        }
                    }
                }
            }
        }
        if (playerMoney >= moneyToTake){
            bool = true;
        }
        if (bool){
            int takenMoney = 0;
            while (takenMoney < moneyToTake) {
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack != null){
                        if (itemStack.hasItemMeta()) {
                            if (itemStack.getItemMeta().hasDisplayName()) {
                                if (itemStack.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "$1")) {
                                    if (itemStack.getType().equals(Material.PAPER)) {
                                        takenMoney++;
                                        itemStack.setAmount(itemStack.getAmount()-1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            player.updateInventory();
        }
        return bool;
    }

    public static void triggerDDOSS(Player hacker, UUID victim, Random random){
        Location location = getPlayersRecentComputer(victim).clone();
        int randomX = random.nextInt(100);
        int randomZ = random.nextInt(100);
        if (random.nextBoolean()){
            randomX *= -1;
        }
        if (random.nextBoolean()){
            randomZ *= -1;
        }

        location.add(randomX, 0, randomZ);

        hacker.sendMessage(ChatColor.GRAY+"DDoSS Attack Complete:");
        hacker.sendMessage(ChatColor.GRAY+"Victims Most Recent Log-in is 200 blocks within:");
        hacker.sendMessage(ChatColor.GRAY+" ");
        hacker.sendMessage(ChatColor.GRAY+"X: "+location.getX());
        hacker.sendMessage(ChatColor.GRAY+"Z: "+location.getZ());
    }

    public static Location getPlayersRecentComputer(UUID player){
        Location location = null;
        for (Location allLocations : StoredStats.computers.keySet()){
            String string = StoredStats.computers.get(allLocations);
            if (string.contains(player.toString())){
                if (string.contains("latest")){
                    location = allLocations;
                    break;
                }
            }
        }
        return location;
    }
    public boolean headCheck(Inventory inventory, String name){
        for (int i = 0; i < inventory.getSize(); i++){
            if (inventory.getItem(i) != null){
                if (inventory.getItem(i).getType().equals(Material.PLAYER_HEAD)){
                    if (inventory.getItem(i).getItemMeta().getDisplayName().contains(name)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static ItemStack generateSkull(String player, String itemName){

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setOwner(player);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private void sendPlayerToCam(UUID camOwner, int camNumber, Player player){
        List<Location> camLocations = new ArrayList<>();
        for (Location location : StoredStats.cameras.keySet()){
            if (StoredStats.cameras.get(location).contains(camOwner.toString())){
                camLocations.add(location.clone());
            }
        }
        CameraManager.putPlayerInCam(player, camLocations.get(camNumber), true);
    }
}
