package me.swipez.securitysmp.command;

import me.swipez.securitysmp.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CustomItemsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("items.menu")){
            if (sender instanceof Player){
                Player player = (Player) sender;
                Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.BLUE+"All Security Items");
                inventory.addItem(ItemManager.BLANK_KEY);
                inventory.addItem(ItemManager.EYESCANNER_DOOR);
                inventory.addItem(ItemManager.KEYPAD_DOOR);
                inventory.addItem(ItemManager.CELL_TOWER);
                inventory.addItem(ItemManager.ADVANCED_DOOR);
                inventory.addItem(ItemManager.CAMERA);
                inventory.addItem(ItemManager.COMPUTER);

                player.openInventory(inventory);
            }
        }
        return true;
    }
}
