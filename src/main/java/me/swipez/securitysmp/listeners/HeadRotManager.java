package me.swipez.securitysmp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeadRotManager implements Listener {
    @EventHandler
    public void onPlayerJoins(PlayerJoinEvent event){
        Player player = event.getPlayer();
        for (int i = 0; i < player.getInventory().getSize(); i++){
            if (player.getInventory().getItem(i) != null){
                ItemStack itemStack = player.getInventory().getItem(i);
                if (itemStack != null) {
                    if (itemStack.getType().equals(Material.PLAYER_HEAD)) {
                        if (!itemStack.getItemMeta().hasLore()) {
                            if (!itemStack.getItemMeta().hasDisplayName()){
                                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                                List<String> lore = new ArrayList<>();
                                Date date = new Date();
                                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                                lore.add(ChatColor.GRAY+"Date Obtained: "+format.format(date));
                                meta.setLore(lore);
                                itemStack.setItemMeta(meta);
                            }
                        }
                        else {
                            List<String> lore = itemStack.getItemMeta().getLore();
                            String dateString = null;
                            for (String string : lore){
                                if (string.toLowerCase().contains("date obtained")){
                                    dateString = string;
                                }
                            }
                            if (dateString != null) {
                                String[] split = dateString.split(": ");
                                String justDate = split[1];

                                String[] dateSplit = justDate.split("/");

                                Date date = new Date();
                                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                                String currentDate = format.format(date);
                                String[] currentDateSplit = currentDate.split("/");

                                if (currentDateSplit[0].equals(dateSplit[0])) {
                                    if (currentDateSplit[2].equals(dateSplit[2])) {

                                        int currentDay = Integer.parseInt(currentDateSplit[1]);
                                        int comparedDay = Integer.parseInt(dateSplit[1]);


                                        if ((currentDay - comparedDay) >= 3) {
                                            itemStack.setType(Material.ZOMBIE_HEAD);
                                            ItemMeta meta = itemStack.getItemMeta();
                                            meta.setDisplayName(ChatColor.GOLD + "Rotted Head");
                                            meta.setLore(new ArrayList<>());

                                            itemStack.setItemMeta(meta);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null){
            if (itemStack.getType().equals(Material.PLAYER_HEAD)){
                if (itemStack.getItemMeta().hasLore()){
                    List<String> lore = itemStack.getItemMeta().getLore();
                    String dateString = null;
                    for (String string : lore){
                        if (string.toLowerCase().contains("date obtained")){
                            dateString = string;
                        }
                    }
                    if (dateString != null){
                        String[] split = dateString.split(": ");
                        String justDate = split[1];

                        String[] dateSplit = justDate.split("/");

                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                        String currentDate = format.format(date);
                        String[] currentDateSplit = currentDate.split("/");

                        if (currentDateSplit[0].equals(dateSplit[0])){
                            if (currentDateSplit[2].equals(dateSplit[2])){

                                int currentDay = Integer.parseInt(currentDateSplit[1]);
                                int comparedDay = Integer.parseInt(dateSplit[1]);


                                if ((currentDay - comparedDay) >= 3){
                                    itemStack.setType(Material.ZOMBIE_HEAD);
                                    ItemMeta meta = itemStack.getItemMeta();
                                    meta.setDisplayName(ChatColor.GOLD+"Rotted Head");
                                    meta.setLore(new ArrayList<>());

                                    itemStack.setItemMeta(meta);
                                }
                            }
                        }
                    }
                }
                else {
                    if (!itemStack.getItemMeta().hasDisplayName()){
                        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                        lore.add(ChatColor.GRAY+"Date Obtained: "+format.format(date));
                        meta.setLore(lore);
                        itemStack.setItemMeta(meta);
                    }
                }
            }
        }
    }
}
