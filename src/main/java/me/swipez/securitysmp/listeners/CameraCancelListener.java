package me.swipez.securitysmp.listeners;

import me.swipez.securitysmp.stored.CameraManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CameraCancelListener implements Listener {
    @EventHandler
    public void onPlayerPicksUpItem(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if (CameraManager.isInCam(player)){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (CameraManager.isInCam(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerDropsItem(PlayerDropItemEvent event){
        if (CameraManager.isInCam(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerHits(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player){
            if (event.getDamager() instanceof Player){
                Player damaged = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();
                if (CameraManager.isInCam(damaged) || CameraManager.isInCam(damager)){
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onCameraGetsTargeted(EntityTargetLivingEntityEvent event){
        if (event.getTarget() instanceof Player){
           Player player = (Player) event.getTarget();
           if (CameraManager.isInCam(player)){
               event.setCancelled(true);
           }
        }
    }
}
