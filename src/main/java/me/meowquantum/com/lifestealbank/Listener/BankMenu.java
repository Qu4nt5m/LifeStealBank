package me.meowquantum.com.lifestealbank.Listener;

import dev.norska.lsc.LifestealCore;
import me.meowquantum.com.lifestealbank.LifeStealBank;
import me.meowquantum.com.lifestealbank.manager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BankMenu implements Listener {

    private final LifeStealBank plugin;

    public BankMenu(LifeStealBank plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        Inventory bankMenu = Bukkit.createInventory(null, 6 * 9, ChatColor.DARK_GREEN + "Heart Bank");

        // Border of green stained glass panes
        ItemStack greenGlass = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
        ItemMeta greenGlassMeta = greenGlass.getItemMeta();
        greenGlassMeta.setDisplayName(" ");
        greenGlass.setItemMeta(greenGlassMeta);

        for (int i = 0; i < 9; i++) {
            bankMenu.setItem(i, greenGlass); // Top row
            bankMenu.setItem(i + 45, greenGlass); // Bottom row
        }
        for (int i = 9; i < 45; i += 9) {
            bankMenu.setItem(i, greenGlass); // Left column
            bankMenu.setItem(i + 8, greenGlass); // Right column
        }

        // Update the heart icon
        updateHeartIcon(bankMenu, player);

        // Deposit buttons
        createButton(bankMenu, Material.GREEN_CONCRETE, 29, ChatColor.GREEN + "Deposit 1 Heart");
        createButton(bankMenu, Material.GREEN_CONCRETE, 30, ChatColor.GREEN + "Deposit 2 Hearts");
        createButton(bankMenu, Material.GREEN_CONCRETE, 31, ChatColor.GREEN + "Deposit 3 Hearts");
        createButton(bankMenu, Material.GREEN_CONCRETE, 32, ChatColor.GREEN + "Deposit 4 Hearts");
        createButton(bankMenu, Material.GREEN_CONCRETE, 33, ChatColor.GREEN + "Deposit 5 Hearts");
        createButton(bankMenu, Material.RED_CONCRETE, 38, ChatColor.RED + "Withdraw 1 Heart");
        createButton(bankMenu, Material.RED_CONCRETE, 39, ChatColor.RED + "Withdraw 2 Hearts");
        createButton(bankMenu, Material.RED_CONCRETE, 40, ChatColor.RED + "Withdraw 3 Hearts");
        createButton(bankMenu, Material.RED_CONCRETE, 41, ChatColor.RED + "Withdraw 4 Hearts");
        createButton(bankMenu, Material.RED_CONCRETE, 42, ChatColor.RED + "Withdraw 5 Hearts");


        player.openInventory(bankMenu);
    }

    private void createButton(Inventory inv, Material material, int slot, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private void updateHeartIcon(Inventory bankMenu, Player player) {
        int storedHearts = plugin.getDataManager().getStoredHearts(player.getUniqueId());
        int limit = getHeartLimit(player); // Obtener el límite de corazones para el jugador
        ItemStack heartIndicator = new ItemStack(Material.RED_DYE);
        ItemMeta heartMeta = heartIndicator.getItemMeta();
        heartMeta.setDisplayName(ChatColor.RED + "Hearts in Bank: " + storedHearts + "/" + limit);
        heartIndicator.setItemMeta(heartMeta);
        bankMenu.setItem(13, heartIndicator);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Heart Bank")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();
        DataManager dataManager = plugin.getDataManager();
        int storedHearts = dataManager.getStoredHearts(player.getUniqueId());
        int playerHearts = LifestealCore.getInstance().getAPI().getPlayerHearts(player.getUniqueId());

        if (itemName.equals(ChatColor.GREEN + "Deposit 1 Heart")) {
            depositHearts(player, 1, storedHearts, playerHearts, dataManager);
        } else if (itemName.equals(ChatColor.GREEN + "Deposit 2 Hearts")) {
            depositHearts(player, 2, storedHearts, playerHearts, dataManager);
        } else if (itemName.equals(ChatColor.GREEN + "Deposit 3 Hearts")) {
            depositHearts(player, 3, storedHearts, playerHearts, dataManager);
        } else if (itemName.equals(ChatColor.GREEN + "Deposit 4 Hearts")) {
            depositHearts(player, 4, storedHearts, playerHearts, dataManager);
        } else if (itemName.equals(ChatColor.GREEN + "Deposit 5 Hearts")) {
            depositHearts(player, 5, storedHearts, playerHearts, dataManager);
        } else if (itemName.equals(ChatColor.RED + "Withdraw 1 Heart")) {
            withdrawHearts(player, 1, storedHearts, dataManager);
        } else if (itemName.equals(ChatColor.RED + "Withdraw 2 Hearts")) {
            withdrawHearts(player, 2, storedHearts, dataManager);
        } else if (itemName.equals(ChatColor.RED + "Withdraw 3 Hearts")) {
            withdrawHearts(player, 3, storedHearts, dataManager);
        } else if (itemName.equals(ChatColor.RED + "Withdraw 4 Hearts")) {
            withdrawHearts(player, 4, storedHearts, dataManager);
        } else if (itemName.equals(ChatColor.RED + "Withdraw 5 Hearts")) {
            withdrawHearts(player, 5, storedHearts, dataManager);
        }


        // Update the heart icon after depositing or withdrawing
        updateHeartIcon(event.getInventory(), player);
    }

    private void depositHearts(Player player, int amount, int storedHearts, int playerHearts, DataManager dataManager) {
        int limit = getHeartLimit(player);
        if (playerHearts <= 6) {
            player.sendMessage(ChatColor.RED + "You need more than 6 hearts to deposit.");
            return;
        }
        if (storedHearts + amount > limit) {
            player.sendMessage(ChatColor.RED + "You have reached your heart storage limit of " + limit + " hearts.");
            return;
        }
        if (playerHearts >= amount) {
            LifestealCore.getInstance().getAPI().removePlayerHearts(player.getUniqueId(), amount, false);
            dataManager.setStoredHearts(player.getUniqueId(), storedHearts + amount);
            player.sendMessage(ChatColor.GREEN + "Deposited " + amount + " hearts.");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have enough hearts to deposit.");
        }
    }


    private void withdrawHearts(Player player, int amount, int storedHearts, DataManager dataManager) {
        if (storedHearts >= amount) {
            LifestealCore.getInstance().getAPI().addPlayerHearts(player.getUniqueId(), amount);
            dataManager.setStoredHearts(player.getUniqueId(), storedHearts - amount);
            player.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " hearts.");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have enough stored hearts to withdraw.");
        }
    }

    private int getHeartLimit(Player player) {
        if (player.hasPermission("heartbank.limit.20")) return 20;
        if (player.hasPermission("heartbank.limit.15")) return 15;
        if (player.hasPermission("heartbank.limit.12")) return 12;
        if (player.hasPermission("heartbank.limit.9")) return 9;
        if (player.hasPermission("heartbank.limit.6")) return 6;
        return 3; // default value
    }

}
