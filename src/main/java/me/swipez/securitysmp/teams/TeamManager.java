package me.swipez.securitysmp.teams;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TeamManager {

    public static HashMap<UUID, Team> playerTeams = new HashMap<>();

    public static boolean hasTeam(UUID player){
        return playerTeams.containsKey(player);
    }

    public static Team getPlayerTeam(UUID player){
        return playerTeams.get(player);
    }

    public static void addPlayerToOwnerTeam(UUID owner, UUID teamMember){
        getPlayerTeam(owner).addPlayerToTeam(teamMember);
    }

    public static void registerPlayerTeam(UUID player, Team instance){
        playerTeams.put(player, instance);
    }

    public static void removePlayerTeam(UUID owner){
        playerTeams.remove(owner);
    }

}
