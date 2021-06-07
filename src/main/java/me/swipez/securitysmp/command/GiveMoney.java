package me.swipez.securitysmp.command;

import me.swipez.securitysmp.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GiveMoney implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("moneygrant.all")){
            if (args.length == 2){
                String player = args[0];
                String amount = args[1];
                addMoney(Bukkit.getPlayer(player), Integer.parseInt(amount));
                sender.sendMessage(ChatColor.GREEN+"Gave "+player+" "+amount+" dollar(s).");
            }
        }
        return true;
    }

    private void addMoney(Player player, int amount){
        for (int i = 0; i < amount; i++){
            addProperly(player, ItemManager.ONE_DOLLAR);
        }
    }

    public static void addProperly(Player player, ItemStack itemStack){
        final Map<Integer, ItemStack> map = player.getInventory().addItem(itemStack);
        for (final ItemStack item : map.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }
}
