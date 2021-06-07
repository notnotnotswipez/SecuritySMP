package me.swipez.securitysmp.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length == 1){
                switch (args[0]){
                    case "create":
                        if (!TeamManager.hasTeam(player.getUniqueId())){
                            TeamManager.registerPlayerTeam(player.getUniqueId(), new Team(player.getUniqueId()));
                            player.sendMessage(ChatColor.GREEN+"Team created! Do /team add <player> to add someone!");
                        }
                        else {
                            player.sendMessage(ChatColor.RED+"You already have a team! Do /team delete to remove it!");
                        }
                        break;
                    case "delete":
                        if (TeamManager.hasTeam(player.getUniqueId())){
                            TeamManager.removePlayerTeam(player.getUniqueId());
                        }
                        else {
                            player.sendMessage(ChatColor.RED+"You dont have a team! Do /team create to make one!");
                        }
                        break;
                    default:
                        player.sendMessage(ChatColor.RED+"You seem to have done it incorrectly. Usage is as follows:");
                        player.sendMessage(ChatColor.RED+"/team create - Creates a team");
                        player.sendMessage(ChatColor.RED+"/team delete - Deletes your team");
                        player.sendMessage(ChatColor.RED+"/team add <player> - Adds a player to your team");
                        player.sendMessage(ChatColor.RED+"/team remove <player> - Removes a player from your team");
                        break;
                }
            }
            else if (args.length == 2){
                String requestedPlayer = args[1];
                switch (args[0]){
                    case "add":
                        if (TeamManager.hasTeam(player.getUniqueId())){
                            Player addedPlayer = Bukkit.getPlayer(requestedPlayer);
                            if (addedPlayer != null){
                                TeamManager.addPlayerToOwnerTeam(player.getUniqueId(), addedPlayer.getUniqueId());
                                player.sendMessage(ChatColor.GREEN+"You added "+requestedPlayer+" to your team!");
                                player.sendMessage(ChatColor.GREEN+"Do /team remove <player> to remove them!");
                            }
                        }
                        else {
                            player.sendMessage(ChatColor.RED+"You dont have a team! Do /team create to make one!");
                        }
                        break;
                    case "remove":
                        if (TeamManager.hasTeam(player.getUniqueId())){
                            Player addedPlayer = Bukkit.getPlayer(requestedPlayer);
                            if (addedPlayer != null){
                                TeamManager.getPlayerTeam(player.getUniqueId()).removePlayerFromTeam(addedPlayer.getUniqueId());
                                player.sendMessage(ChatColor.GREEN+"You removed "+requestedPlayer+" from your team!");
                                player.sendMessage(ChatColor.GREEN+"Do /team add <player> to add them back!");
                            }
                        }
                        else {
                            player.sendMessage(ChatColor.RED+"You dont have a team! Do /team create to make one!");
                        }
                        break;
                    default:
                        player.sendMessage(ChatColor.RED+"You seem to have done it incorrectly. Usage is as follows:");
                        player.sendMessage(ChatColor.RED+"/team create - Creates a team");
                        player.sendMessage(ChatColor.RED+"/team delete - Deletes your team");
                        player.sendMessage(ChatColor.RED+"/team add <player> - Adds a player to your team");
                        player.sendMessage(ChatColor.RED+"/team remove <player> - Removes a player from your team");
                        break;
                }
            }
            else {
                player.sendMessage(ChatColor.RED+"You seem to have done it incorrectly. Usage is as follows:");
                player.sendMessage(ChatColor.RED+"/team create - Creates a team");
                player.sendMessage(ChatColor.RED+"/team delete - Deletes your team");
                player.sendMessage(ChatColor.RED+"/team add <player> - Adds a player to your team");
                player.sendMessage(ChatColor.RED+"/team remove <player> - Removes a player from your team");
            }
        }
        return true;
    }
}
