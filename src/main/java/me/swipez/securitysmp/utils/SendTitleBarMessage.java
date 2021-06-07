package me.swipez.securitysmp.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SendTitleBarMessage {
    public static void sendMessage(Player player, String name, int seconds) {
        int minutes = 0;
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }
        String time;
        if (String.valueOf(seconds).length() == 1)
            time = ChatColor.GOLD + " [" + minutes + ":0" + seconds + "]";
        else
            time = ChatColor.GOLD + " [" + minutes + ":" + seconds + "]";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(name + time));
    }

    public static void broadcast(String name, int seconds) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMessage(player, name, seconds);
        }
    }
}
