package me.swipez.securitysmp.listeners;

import me.swipez.securitysmp.SecuritySMP;
import me.swipez.securitysmp.gui.KeyPadGUI;
import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.stored.StoredStats;
import me.swipez.securitysmp.teams.TeamManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.*;

public class BlockPlaceListener implements Listener {

    boolean checking = false;

    @EventHandler
    public void onPlayerDies(PlayerDeathEvent event){
        if (event.getEntity().getKiller() == null){
            return;
        }
        Player killer = event.getEntity().getKiller();
        if (killer.getInventory().getItemInMainHand().getType().toString().contains("_SWORD")){
            Player player = event.getEntity();
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (player.isSneaking()){
                List<String> lore = new ArrayList<>();
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                lore.add(ChatColor.GRAY+"Date Obtained: "+format.format(date));
                meta.setLore(lore);
            }
            meta.setOwningPlayer(player);
            skull.setItemMeta(meta);
            player.getWorld().dropItemNaturally(player.getLocation(), skull);
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Block block = event.getBlock();
        if (event.getBlock().getType().equals(Material.IRON_DOOR)){
            if (event.getItemInHand().isSimilar(ItemManager.EYESCANNER_DOOR)){
                StoredStats.doorTypes.put(block.getLocation(), uuid.toString()+";eyescan");
            }
            else if (event.getItemInHand().isSimilar(ItemManager.KEYPAD_DOOR)){
                StoredStats.doorTypes.put(block.getLocation(), uuid.toString()+";locked;0000");
            }
        }
        if (event.getItemInHand().isSimilar(ItemManager.CELL_TOWER)){
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!event.getBlock().getType().isAir()){
                        StoredStats.homeCenters.put(event.getBlock().getLocation(), event.getPlayer().getUniqueId().toString());
                    }
                }
            }.runTaskLater(SecuritySMP.plugin, (long) 0.5);
        }
        if (event.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.YELLOW+"Camera")){
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!event.getBlock().getType().isAir()){
                        StoredStats.cameras.put(event.getBlock().getLocation(), event.getPlayer().getUniqueId().toString());
                    }
                }
            }.runTaskLater(SecuritySMP.plugin, (long) 0.5);
        }
        if (event.getItemInHand().getType().equals(Material.PLAYER_HEAD)){
            SkullMeta meta = (SkullMeta) event.getItemInHand().getItemMeta();
            if (meta.getDisplayName().toLowerCase().contains("computer")){
                Location location = block.getLocation();
                StoredStats.computers.put(location, player.getUniqueId()+";false;latest");
            }
            if (!meta.hasDisplayName()){
                if (meta.hasLore()){
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerInteractWithDoor(PlayerInteractEvent event){
        if (!event.hasBlock()){
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            return;
        }
        if (checking){
            return;
        }
        checking = true;
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        String string = playerOwnsDoor(player, block);
        if (string != null){
            String[] split = string.split(";");
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
            if (bool){
                if (split[1].equals("locked")){
                    if (split[2].equals("0000")){
                        player.openInventory(KeyPadGUI.generateKeyPadInv("", player, true));
                    }
                    else {
                        player.openInventory(KeyPadGUI.generateKeyPadInv("", player, false));
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                if (split[1].equals("eyescan")){
                    player.sendMessage(ChatColor.RED+"Scanning...");
                    player.sendMessage(ChatColor.GREEN+player.getDisplayName()+" Detected.");
                    Door door = (Door) block.getBlockData();
                    door.setOpen(!door.isOpen());
                    block.setBlockData(door);
                    player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                }
            }
            else {
                if (split[1].equals("locked")){
                    player.openInventory(KeyPadGUI.generateKeyPadInv("", player, false));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                if (split[1].equals("eyescan")){
                    player.sendMessage(ChatColor.RED+"Scanning...");
                    if (playerWearingHead(player, UUID.fromString(split[0]))){
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(split[0]));
                        player.sendMessage(ChatColor.GREEN+offlinePlayer.getName()+" Detected.");
                        Door door = (Door) block.getBlockData();
                        door.setOpen(!door.isOpen());
                        block.setBlockData(door);
                        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                    }
                    else {
                        player.sendMessage(ChatColor.RED+"Incorrrect User.");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
            }
        }
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                checking = false;
            }
        }.runTaskLater(SecuritySMP.plugin, (long) 0.2);
    }

    public boolean playerWearingHead(Player player, UUID uuid){
        try {
            ItemStack skullItem = Objects.requireNonNull(player.getEquipment()).getItem(EquipmentSlot.HEAD);
            if (skullItem.getType().equals(Material.PLAYER_HEAD)){
                if (skullItem.getItemMeta().hasLore()){
                    SkullMeta meta = (SkullMeta) skullItem.getItemMeta();
                    return meta.getOwningPlayer().getUniqueId().equals(uuid);
                }
            }
        }
        catch (NullPointerException exception){
            return false;
        }
        return false;
    }

    public String playerOwnsDoor(Player owner, Block block){
        String string = null;
        Location originLocation = block.getLocation();
        Location doubleLocation = originLocation.subtract(0,1,0);
        if (StoredStats.doorTypes.containsKey(doubleLocation)){
            string = StoredStats.doorTypes.get(doubleLocation);
        }
        if (StoredStats.doorTypes.containsKey(originLocation)){
            string = StoredStats.doorTypes.get(originLocation);
        }
        return string;
    }
}
