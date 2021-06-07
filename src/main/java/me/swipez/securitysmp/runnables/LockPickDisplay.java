package me.swipez.securitysmp.runnables;

import me.swipez.securitysmp.listeners.ComputerInterfaceListener;
import me.swipez.securitysmp.listeners.DoorLockSystem;
import me.swipez.securitysmp.utils.SendTitleBarMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LockPickDisplay extends BukkitRunnable {
    @Override
    public void run() {
        for (UUID uuid : ComputerInterfaceListener.ddosTimes.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            if (ComputerInterfaceListener.ddosTimes.get(uuid) > 0){
                ComputerInterfaceListener.ddosTimes.put(uuid, ComputerInterfaceListener.ddosTimes.get(uuid)-1);
                if (player.getLocation().distance(ComputerInterfaceListener.computerLocations.get(uuid)) < 4){
                    SendTitleBarMessage.sendMessage(player, ChatColor.GREEN+"DDoSS Time:", ComputerInterfaceListener.ddosTimes.get(uuid));
                }
            }
        }
        for (UUID uuid : DoorLockSystem.lockpickingPlayers.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            if (DoorLockSystem.lockPickTime.get(uuid) > 0){
                DoorLockSystem.lockPickTime.put(uuid, DoorLockSystem.lockPickTime.get(uuid)-1);
                SendTitleBarMessage.sendMessage(player, ChatColor.YELLOW+"Lockpick Time:", DoorLockSystem.lockPickTime.get(uuid));
                if (DoorLockSystem.lockPickTime.get(uuid) == 0){
                    Door door = (Door) DoorLockSystem.lockpickingPlayers.get(uuid).getBlock().getBlockData();
                    door.setOpen(true);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F);
                    player.sendMessage(ChatColor.GREEN+"Picked lock!");
                    DoorLockSystem.lockpickingPlayers.get(uuid).getBlock().setBlockData(door);
                    DoorLockSystem.lockpickingPlayers.remove(uuid);
                }
            }
        }
    }
}
