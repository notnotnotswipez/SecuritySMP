package me.swipez.securitysmp.runnables;

import me.swipez.securitysmp.stored.StoredStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HomeAssurance extends BukkitRunnable {
    @Override
    public void run() {
        for (Location location : StoredStats.homeCenters.keySet()){
            if (!location.getBlock().getType().equals(Material.SMOOTH_STONE)){
                for (Player player : Bukkit.getOnlinePlayers()){
                    if (player.hasPermission("notification.cell_tower")){
                        player.sendMessage(ChatColor.GREEN+"A Cell tower has been corrected");
                    }
                }
                StoredStats.homeCenters.remove(location);
            }
        }
    }
}
