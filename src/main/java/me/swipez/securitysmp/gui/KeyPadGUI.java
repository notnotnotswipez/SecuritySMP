package me.swipez.securitysmp.gui;

import me.swipez.securitysmp.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KeyPadGUI {

    public static Inventory generateKeyPadInv(String digit, Player owner, boolean forFinal){
        StringBuilder actualName = new StringBuilder(digit);
        if (digit.length() <= 4){
            int difference = 4 - digit.length();
            for (int i = 0; i < difference; i++){
                actualName.append(" _");
            }
        }
        Inventory inventory;
        if (!forFinal){
            inventory = Bukkit.createInventory(owner, 54, ChatColor.BLACK.toString()+ChatColor.BOLD+"Combination: "+actualName);
        }
        else {
            inventory = Bukkit.createInventory(owner, 54, ChatColor.RED.toString()+ChatColor.BOLD+"Pick a Combination: "+actualName);
        }

        inventory.setItem(12, generateDigitIcon(1));
        inventory.setItem(13, generateDigitIcon(2));
        inventory.setItem(14, generateDigitIcon(3));
        inventory.setItem(21, generateDigitIcon(4));
        inventory.setItem(22, generateDigitIcon(5));
        inventory.setItem(23, generateDigitIcon(6));
        inventory.setItem(30, generateDigitIcon(7));
        inventory.setItem(31, generateDigitIcon(8));
        inventory.setItem(32, generateDigitIcon(9));
        inventory.setItem(40, generateDigitIcon(0));

        for (int i = 0; i < inventory.getSize(); i++){
            if (inventory.getItem(i) == null){
                inventory.setItem(i, generateItem(Material.BLACK_STAINED_GLASS_PANE, " "));
            }
        }
        inventory.setItem(53, ItemManager.BACK_BED);
        return inventory;
    }

    private static ItemStack generateDigitIcon(int digit){
        ItemStack itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN.toString()+digit+"");
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private static ItemStack generateItem(Material material, String name){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
