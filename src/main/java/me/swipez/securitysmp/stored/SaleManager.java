package me.swipez.securitysmp.stored;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaleManager {

    public static List<ItemStack> shopItems = new ArrayList<>();

    public static void addShopItem(ItemStack itemStack, int price, Player player){
        ItemMeta meta = itemStack.getItemMeta();

        List<String> lore = new ArrayList<>();
        if (meta.hasLore()){
            lore = meta.getLore();
        }
        lore.add(ChatColor.GREEN+"Seller: "+player.getDisplayName());
        lore.add(ChatColor.GREEN+"Price: "+price);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        shopItems.add(itemStack);
    }
}
