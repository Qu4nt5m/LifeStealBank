package me.meowquantum.com.lifestealbank;

import me.meowquantum.com.lifestealbank.Listener.BankMenu;
import me.meowquantum.com.lifestealbank.commands.HeartBankCommand;
import me.meowquantum.com.lifestealbank.commands.HearthPayCommand;
import me.meowquantum.com.lifestealbank.commands.ProfileCommand;
import me.meowquantum.com.lifestealbank.manager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifeStealBank extends JavaPlugin {

    private DataManager dataManager;
    private static LifeStealBank instance;

    @Override
    public void onEnable() {
        instance = this;
        HearthPayCommand hearthPayCommand = new HearthPayCommand();
        getCommand("hearthpay").setExecutor(hearthPayCommand);
        getCommand("profile").setExecutor(new ProfileCommand());
        getServer().getPluginManager().registerEvents(new ProfileCommand(), this);

        this.getCommand("heartbank").setExecutor(new HeartBankCommand());
        Bukkit.getPluginManager().registerEvents(new BankMenu(this), this);
        dataManager = new DataManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public static LifeStealBank getInstance() {
        return instance;
    }

}
