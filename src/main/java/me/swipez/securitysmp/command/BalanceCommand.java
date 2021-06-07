package me.swipez.securitysmp.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            int playerMoney = 0;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if (itemStack != null){
                    if (itemStack.hasItemMeta()) {
                        if (itemStack.getItemMeta().hasDisplayName()) {
                            if (itemStack.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "$1")) {
                                if (itemStack.getType().equals(Material.PAPER)) {
                                    playerMoney += itemStack.getAmount();
                                }
                            }
                        }
                    }
                }
            }
            player.sendMessage(ChatColor.GREEN+"You have: $"+playerMoney);
        }
        return true;
    }
}
