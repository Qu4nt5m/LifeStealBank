package me.meowquantum.com.lifestealbank.commands;

import me.meowquantum.com.lifestealbank.LifeStealBank;
import me.meowquantum.com.lifestealbank.Listener.BankMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class HeartBankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        LifeStealBank plugin = LifeStealBank.getInstance();
        BankMenu bankMenu = new BankMenu(plugin);
        bankMenu.openMenu(player);
        return true;
    }
}
