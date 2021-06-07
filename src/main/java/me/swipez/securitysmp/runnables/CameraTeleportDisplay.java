package me.swipez.securitysmp.runnables;

import me.swipez.securitysmp.stored.CameraManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CameraTeleportDisplay extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()){
            if (CameraManager.isInCam(player)){
                Location cameraLocation = CameraManager.getPlayerCamLocation(player);
                player.teleport(cameraLocation.clone().subtract(-0.5,1.4,-0.5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2, 1, true, false));
            }
        }
    }
}
