package me.swipez.securitysmp;

import me.swipez.securitysmp.command.BalanceCommand;
import me.swipez.securitysmp.command.CustomItemsCommand;
import me.swipez.securitysmp.command.GiveMoney;
import me.swipez.securitysmp.items.ItemManager;
import me.swipez.securitysmp.listeners.*;
import me.swipez.securitysmp.runnables.CameraTeleportDisplay;
import me.swipez.securitysmp.runnables.HomeAssurance;
import me.swipez.securitysmp.runnables.LockPickDisplay;
import me.swipez.securitysmp.stored.StoredStats;
import me.swipez.securitysmp.teams.TeamCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class SecuritySMP extends JavaPlugin {

    public static SecuritySMP plugin;

    @Override
    public void onEnable() {
        plugin = this;
        File mainFolder = new File(getDataFolder().getPath());
        if (!mainFolder.exists()){
            mainFolder.mkdir();
        }
        File storageConfig = new File(getDataFolder().getPath(), "stored_blocks.yml");
        if (storageConfig.exists()){
            StoredStats.loadStats();
        }

        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new ChestLockSystem(), this);
        getServer().getPluginManager().registerEvents(new DoorLockSystem(), this);
        getServer().getPluginManager().registerEvents(new HomeListener(), this);
        getServer().getPluginManager().registerEvents(new ComputerInterfaceListener(), this);
        getServer().getPluginManager().registerEvents(new CameraCancelListener(), this);
        getServer().getPluginManager().registerEvents(new HeadRotManager(), this);
        BukkitTask task = new LockPickDisplay().runTaskTimer(this, 20, 20);
        BukkitTask cameraTeleport = new CameraTeleportDisplay().runTaskTimer(this, 1, 1);
        BukkitTask homeInsurance = new HomeAssurance().runTaskTimer(this, 1, 1);

        getCommand("bal").setExecutor(new BalanceCommand());
        getCommand("givemoney").setExecutor(new GiveMoney());
        getCommand("itemsmenu").setExecutor(new CustomItemsCommand());
        getCommand("team").setExecutor(new TeamCommand());

        ItemManager.initRecipes();
    }

    @Override
    public void onDisable() {
        StoredStats.saveStats();

    }
}
