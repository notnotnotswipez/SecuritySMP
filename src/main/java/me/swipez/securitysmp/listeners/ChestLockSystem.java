package me.swipez.securitysmp.listeners;

import me.swipez.securitysmp.SecuritySMP;
import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.stored.StoredStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ChestLockSystem implements Listener {

    Random random = new Random();

    @EventHandler
    public void onChestLock(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        if (!event.hasItem()){
            return;
        }
        ItemStack itemStack = event.getItem();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemMeta meta = itemStack.getItemMeta();
        if (event.hasItem()) {
            if (block.getType().equals(Material.CHEST)) {
                Chest chest = (Chest) block.getState();
                if (itemStack.isSimilar(ItemManager.BLANK_KEY)) {
                    if (!chest.isLocked()) {
                        event.setCancelled(true);
                        String string = String.valueOf(random.nextInt(Integer.MAX_VALUE));
                        chest.setLock(string);
                        chest.update();
                        ItemStack slotItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
                        if (slotItem.getAmount() > 1){
                            addProperly(player, generateKey(chest.getLocation(), "Chest", player, string));
                        }
                        else {
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), generateKey(chest.getLocation(), "Chest", player, string));
                        }
                    }
                }
                if (meta.hasLore()) {
                    List<String> allLore = meta.getLore();
                    boolean hasSerial = false;
                    String serialNumber = null;
                    for (String string : allLore) {
                        if (string.contains("Serial Number")) {
                            hasSerial = true;
                            serialNumber = string.replace("Serial Number: ", "").replace(ChatColor.GRAY.toString(), "");
                            break;
                        }
                    }
                    if (hasSerial) {
                        if (serialNumber.equals(chest.getLock())) {
                            chest.setLock(null);
                            chest.update();
                            String serialClone = serialNumber;
                            BukkitTask task = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    chest.setLock(serialClone);
                                    chest.update();
                                }
                            }.runTaskLater(SecuritySMP.plugin, 1);
                        }
                    }
                }
            }
        }
    }

    public static void addProperly(Player player, ItemStack itemStack){
        final Map<Integer, ItemStack> map = player.getInventory().addItem(itemStack);
        for (final ItemStack item : map.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    public ItemStack generateKey(Location location, String type, Player player, String serialNumber){
        ItemStack itemStack = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = itemStack.getItemMeta();
        List<ChatColor> allChatColors = Arrays.asList(ChatColor.values());
        meta.setDisplayName(allChatColors.get(random.nextInt(allChatColors.size()))+type+" key");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+type+" Key");
        lore.add(ChatColor.GRAY+"Original Owner: "+player.getDisplayName());
        lore.add(ChatColor.GRAY+"Location: "+location.getX()+" "+location.getY()+" "+location.getZ());
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Serial Number: "+serialNumber);

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
