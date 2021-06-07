package me.swipez.securitysmp.stored;

import me.swipez.securitysmp.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.UUID;

public class CameraManager {

    public static HashMap<UUID, ItemStack[]> inventoryBeforeCam = new HashMap<>();
    public static HashMap<UUID, Location> locationBeforeCam = new HashMap<>();

    public static HashMap<UUID, Location> cameraLocation = new HashMap<>();
    public static HashMap<UUID, Skull> originalCamBlock = new HashMap<>();

    public static boolean isInCam(Player player){
        return inventoryBeforeCam.containsKey(player.getUniqueId());
    }

    public static void putPlayerInCam(Player player, Location camera, boolean firstTime){
        if (firstTime){
            locationBeforeCam.put(player.getUniqueId(), player.getLocation());
            inventoryBeforeCam.put(player.getUniqueId(), player.getInventory().getContents());
            originalCamBlock.put(player.getUniqueId(), (Skull) camera.getBlock().getState());
            player.getInventory().clear();
            player.getEquipment().setHelmet(ItemManager.CAMERA);
            player.getInventory().addItem(ItemManager.BACK_BED);
            player.getInventory().addItem(ItemManager.FLIP_CAMERA);
        }
        camera.getBlock().setType(Material.AIR);
        camera.setDirection(originalCamBlock.get(player.getUniqueId()).getRotation().getDirection().multiply(-1));
        cameraLocation.put(player.getUniqueId(), camera);
    }

    public static void changePlayerCam(Player player, Location camera){
        cameraLocation.get(player.getUniqueId()).getBlock().setBlockData((BlockData) originalCamBlock.get(player.getUniqueId()));
        originalCamBlock.put(player.getUniqueId(), (Skull) camera.getBlock().getState());
        cameraLocation.put(player.getUniqueId(), camera);
        camera.getBlock().setType(Material.AIR);
    }

    public static void removePlayerCam(Player player){
        player.teleport(locationBeforeCam.get(player.getUniqueId()));
        locationBeforeCam.remove(player.getUniqueId());
        player.getInventory().setContents(inventoryBeforeCam.get(player.getUniqueId()));
        player.updateInventory();
        inventoryBeforeCam.remove(player.getUniqueId());
        cameraLocation.get(player.getUniqueId()).getBlock().setBlockData(originalCamBlock.get(player.getUniqueId()).getBlockData());
        Skull skull = (Skull) cameraLocation.get(player.getUniqueId()).getBlock().getState();
        skull.setOwner("spectator__dead");
        skull.update();
        cameraLocation.remove(player.getUniqueId());

    }

    public static Location getPlayerCamLocation(Player player){
        return cameraLocation.get(player.getUniqueId());
    }
}
