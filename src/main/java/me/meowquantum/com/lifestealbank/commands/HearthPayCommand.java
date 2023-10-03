package me.meowquantum.com.lifestealbank.commands;

import dev.norska.lsc.LifestealCore;
import dev.norska.lsc.api.LifestealCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HearthPayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hearthpay")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage. Use: /hearthpay (player) (amount of hearts)");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                return true;
            }

            Player payer = (Player) sender;
            Player receiver = Bukkit.getPlayer(args[0]);
            if (receiver == null) {
                payer.sendMessage(ChatColor.RED + "The specified player is not online or does not exist.");
                return true;
            }

            int heartAmount;
            try {
                heartAmount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                payer.sendMessage(ChatColor.RED + "Please enter a valid amount of hearts.");
                return true;
            }

            if (getPlayerHearts(payer.getUniqueId()) <= 15) {
                payer.sendMessage(ChatColor.RED + "You must have more than 15 hearts to use this command.");
                return true;
            }

            if (getPlayerHearts(payer.getUniqueId()) < heartAmount) {
                payer.sendMessage(ChatColor.RED + "You don't have enough hearts to pay.");
                return true;
            }

            removePlayerHearts(payer.getUniqueId(), heartAmount, false);
            addPlayerHearts(receiver.getUniqueId(), heartAmount);

            payer.sendMessage(ChatColor.GREEN + "You have paid " + heartAmount + " hearts to " + receiver.getName() + ".");
            receiver.sendMessage(ChatColor.GREEN + payer.getName() + " has paid you " + heartAmount + " hearts.");

            return true;
        }
        return false;
    }

    public void addPlayerHearts(UUID uuid, int amount) {
        LifestealCore.getInstance().getAPI().addPlayerHearts(uuid, amount);
    }

    public void removePlayerHearts(UUID uuid, int amount, Boolean silent) {
        LifestealCore.getInstance().getAPI().removePlayerHearts(uuid, amount, silent);
    }

    public int getPlayerHearts(UUID uuid) {
        return LifestealCore.getInstance().getAPI().getPlayerHearts(uuid);
    }
}
