package me.swipez.securitysmp.teams;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Team {

    private final UUID ownerUUID;
    Set<UUID> teamMemberUUIDs = new HashSet<>();

    public Team(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public boolean isInTeam(UUID checked){
        boolean bool = false;
        if (teamMemberUUIDs.contains(checked)){
            bool = true;
        }
        return bool;
    }

    public void addPlayerToTeam(UUID player){
        teamMemberUUIDs.add(player);
    }

    public void removePlayerFromTeam(UUID player){
        teamMemberUUIDs.remove(player);
    }

    public Set<UUID> getTeamMembers(){
        return teamMemberUUIDs;
    }
}
