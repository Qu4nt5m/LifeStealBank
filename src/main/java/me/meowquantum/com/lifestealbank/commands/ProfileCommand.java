package me.meowquantum.com.lifestealbank.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ProfileCommand implements CommandExecutor, Listener {

    private enum MenuPage {
        MAIN,
        COMBAT,
        TEAMS
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /profile <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }

        MenuPage page = MenuPage.MAIN; // Default
        if (args.length > 1) {
            try {
                page = MenuPage.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + "Invalid menu type. Use MAIN, COMBAT, or TEAMS.");
                return true;
            }
        }

        openProfileMenu(player, target, page);
        return true;
    }

    private void openProfileMenu(Player player, Player target, MenuPage page) {
        String title;
        switch (page) {
            case MAIN:
                title = ChatColor.LIGHT_PURPLE + "General Statistic [" + target.getName() + "]";
                break;
            case COMBAT:
                title = ChatColor.LIGHT_PURPLE + "Combat Statistic [" + target.getName() + "]";
                break;
            case TEAMS:
                title = ChatColor.LIGHT_PURPLE + "Team Statistic [" + target.getName() + "]";
                break;
            default:
                title = ChatColor.LIGHT_PURPLE + target.getName() + "'s Profile";
        }
        Inventory profileMenu = Bukkit.createInventory(null, 54, title);

        // Glass pane decoration
        ItemStack glassPane = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassMeta);

        for (int i = 0; i < 9; i++) {
            profileMenu.setItem(i, glassPane); // Top row
            profileMenu.setItem(i + 45, glassPane); // Bottom row
        }
        for (int i = 9; i < 45; i += 9) {
            profileMenu.setItem(i, glassPane); // Left column
            profileMenu.setItem(i + 8, glassPane); // Right column
        }

        switch (page) {
            case MAIN:
                fillMainPage(profileMenu, target);
                break;
            case COMBAT:
                fillCombatPage(profileMenu, target);
                break;
            case TEAMS:
                fillTeamsPage(profileMenu, target);
                break;
        }

        profileMenu.setItem(48, createPageButton(Material.RED_CONCRETE, "&cMain", page == MenuPage.MAIN));
        profileMenu.setItem(49, createPageButton(Material.RED_CONCRETE, "&cCombat", page == MenuPage.COMBAT));
        profileMenu.setItem(50, createPageButton(Material.RED_CONCRETE, "&cTeams", page == MenuPage.TEAMS));

        player.openInventory(profileMenu);
    }

    private void fillMainPage(Inventory profileMenu, Player target) {
        profileMenu.setItem(19, createItem(Material.PAPER, "&d&lCash", "&f" + PlaceholderAPI.setPlaceholders(target, "%vault_eco_balance%")));
        profileMenu.setItem(21, createItem(Material.RED_DYE, "&d&lHearts", "&f" + PlaceholderAPI.setPlaceholders(target, "%lifesteal_hearts%")));
        profileMenu.setItem(23, createItem(Material.CLOCK, "&d&lTime Played", "&f" + PlaceholderAPI.setPlaceholders(target, "%statistic_hours_played%")));
        profileMenu.setItem(25, createItem(Material.SHIELD, "&d&lTeam", "&f" + PlaceholderAPI.setPlaceholders(target, "%statistic_hours_played%")));
    }

    private void fillCombatPage(Inventory profileMenu, Player target) {
        profileMenu.setItem(19, createItem(Material.NETHERITE_SWORD, "&d&lKills", "&f" + PlaceholderAPI.setPlaceholders(target, "%statistic_player_kills%")));
        profileMenu.setItem(21, createItem(Material.BARRIER, "&d&lDeaths", "&f" + PlaceholderAPI.setPlaceholders(target, "%statistic_deaths%")));
        profileMenu.setItem(23, createItem(Material.EXPERIENCE_BOTTLE, "&d&lKill Streak", "&f" + PlaceholderAPI.setPlaceholders(target, "%some_deaths_placeholder%")));
        profileMenu.setItem(25, createItem(Material.NETHERITE_AXE, "&d&lKDR", "&f" + PlaceholderAPI.setPlaceholders(target, "%some_deaths_placeholder%")));
    }

    private void fillTeamsPage(Inventory profileMenu, Player target) {
        profileMenu.setItem(19, createItem(Material.RED_BANNER, "&d&lTeam Kills", "&f" + PlaceholderAPI.setPlaceholders(target, "%some_team_name_placeholder%")));
        profileMenu.setItem(21, createItem(Material.DIAMOND, "&d&lTeam Deaths", "&f" + PlaceholderAPI.setPlaceholders(target, "%some_team_points_placeholder%")));
        profileMenu.setItem(23, createItem(Material.NETHERITE_AXE, "&d&lTeam KDR", "&f" + PlaceholderAPI.setPlaceholders(target, "%some_team_points_placeholder%")));
        profileMenu.setItem(25, createItem(Material.PLAYER_HEAD, "&d&lTeam Members", "&f" + PlaceholderAPI.setPlaceholders(target, "%some_team_points_placeholder%")));
    }

    private ItemStack createPageButton(Material material, String name, boolean isActive) {
        ItemStack item = new ItemStack(isActive ? Material.GREEN_CONCRETE : material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', lore)));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(ChatColor.LIGHT_PURPLE + "General Statistic [") &&
                !title.startsWith(ChatColor.LIGHT_PURPLE + "Combat Statistic [") &&
                !title.startsWith(ChatColor.LIGHT_PURPLE + "Team Statistic [")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();

        // Extraer el nombre del jugador del título
        String targetName = title.split("\\[")[1].split("\\]")[0];

        if (itemName.equals(ChatColor.translateAlternateColorCodes('&', "&cMain"))) {
            player.performCommand("profile " + targetName + " MAIN");
        } else if (itemName.equals(ChatColor.translateAlternateColorCodes('&', "&cCombat"))) {
            player.performCommand("profile " + targetName + " COMBAT");
        } else if (itemName.equals(ChatColor.translateAlternateColorCodes('&', "&cTeams"))) {
            player.performCommand("profile " + targetName + " TEAMS");
        }
    }


}
