package me.swipez.securitysmp.listeners;

import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.stored.StoredStats;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.swipez.securitysmp.listeners.ChestLockSystem.addProperly;

public class DoorLockSystem implements Listener {

    Random random = new Random();
    public static HashMap<UUID, Location> lockpickingPlayers = new HashMap<>();
    public static HashMap<UUID, Integer> lockPickTime = new HashMap<>();

    @EventHandler
    public void onBlockPowered(BlockRedstoneEvent event){
        Block block = event.getBlock();
        String string = playerOwnsDoor(null, block);
        if (string != null){
            event.setNewCurrent(0);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if (lockpickingPlayers.containsKey(player.getUniqueId())){
            if (player.getLocation().distance(lockpickingPlayers.get(player.getUniqueId())) > 3){
                lockpickingPlayers.remove(player.getUniqueId());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F);
                player.sendMessage(ChatColor.RED+"Lock pick cancelled.");
                player.getInventory().addItem(ItemManager.LOCKPICKS);
            }
        }
    }

    @EventHandler
    public void onPlayerLocksDoor(PlayerInteractEvent event){
        if (!event.hasBlock()){
            return;
        }
        ItemStack itemStack = event.getItem();
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (block.getType().toString().contains("_DOOR")){
            if (!event.hasItem()){
                if (getDoorSerial(block) != null){
                    event.setCancelled(true);
                    return;
                }
            }
            if (itemStack != null){
                if (itemStack.isSimilar(ItemManager.LOCKPICKS)){
                    itemStack.setAmount(0);
                    if (block.getType().toString().contains("_DOOR")){
                        String string = playerOwnsDoor(player, block);
                        if (string != null){
                            String[] split = string.split(";");
                            UUID uuid = player.getUniqueId();
                            if (split[1].equals("keylocked")){
                                lockpickingPlayers.put(uuid, block.getLocation());
                                lockPickTime.put(uuid, 120);
                            }
                            if (split[1].equals("locked")){
                                lockpickingPlayers.put(uuid, block.getLocation());
                                lockPickTime.put(uuid, 600);
                            }
                        }
                    }
                }
                if (itemStack.isSimilar(ItemManager.BLANK_KEY)) {
                    if (getDoorSerial(block) == null) {
                        event.setCancelled(true);
                        String string = String.valueOf(random.nextInt(Integer.MAX_VALUE));
                        StoredStats.doorTypes.put(block.getLocation(), player.getUniqueId()+";keylocked;"+"0000;"+string);
                        ItemStack slotItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
                        if (slotItem.getAmount() > 1){
                            addProperly(player, generateKey(block.getLocation(), "Door", player, string));
                        }
                        else {
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), generateKey(block.getLocation(), "Door", player, string));
                        }
                    }
                }
            }
            boolean hasSerial = getDoorSerial(block) != null;
            if (hasSerial){
                String doorSerial = getDoorSerial(block);
                if (itemStack.getItemMeta().hasLore()){
                    List<String> allLore = itemStack.getItemMeta().getLore();
                    boolean loreSerial = false;
                    String serialNumber = null;
                    for (String string : allLore) {
                        if (string.contains("Serial Number")) {
                            loreSerial = true;
                            serialNumber = string.replace("Serial Number: ", "").replace(ChatColor.GRAY.toString(), "");
                            break;
                        }
                    }

                    if (loreSerial){
                        if (serialNumber.equals(doorSerial)){
                            Door door = (Door) block.getBlockData();
                            door.setOpen(!door.isOpen());
                            block.setBlockData(door);
                        }
                        else {
                            event.setCancelled(true);
                        }
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }
        }
    }

    public String playerOwnsDoor(Player owner, Block block){
        String string = null;
        Location originLocation = block.getLocation();
        Location doubleLocation = originLocation.clone().subtract(0,1,0);
        Location aboveLocation = originLocation.clone().add(0,1,0);
        if (StoredStats.doorTypes.containsKey(doubleLocation)){
            string = StoredStats.doorTypes.get(doubleLocation);
        }
        if (StoredStats.doorTypes.containsKey(originLocation)){
            string = StoredStats.doorTypes.get(originLocation);
        }
        if (StoredStats.doorTypes.containsKey(aboveLocation)){
            string = StoredStats.doorTypes.get(aboveLocation);
        }
        return string;
    }

    public String getDoorSerial(Block block){
        String string = null;
        Location originLocation = block.getLocation();
        Location doubleLocation = originLocation.clone().subtract(0,1,0);
        Location aboveLocation = originLocation.clone().add(0,1,0);
        if (StoredStats.doorTypes.containsKey(aboveLocation)){
            string = stringFromLocation(aboveLocation);
        }
        if (StoredStats.doorTypes.containsKey(doubleLocation)){
            string = stringFromLocation(doubleLocation);
        }
        if (StoredStats.doorTypes.containsKey(originLocation)){
            string = stringFromLocation(originLocation);
        }
        return string;
    }

    public String stringFromLocation(Location location){
        String string;
        string = StoredStats.doorTypes.get(location);
        String[] split = string.split(";");
        if (split.length == 4){
            return split[3];
        }

        return string;
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
