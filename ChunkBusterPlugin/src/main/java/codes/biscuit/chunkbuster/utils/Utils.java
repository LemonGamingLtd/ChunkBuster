package codes.biscuit.chunkbuster.utils;

import codes.biscuit.chunkbuster.ChunkBuster;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static codes.biscuit.chunkbuster.ChunkBuster.CHUNKBUSTER_RADIUS_KEY;

public class Utils {

    private ChunkBuster main;
    private Set<Chunk> waterChunks = new HashSet<>(); // The chunks that water should not flow in

    public Utils(ChunkBuster main) {
        this.main = main;
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    List<String> colorLore(List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, color(lore.get(i)));
        }
        return lore; // For convenience
    }

    ItemStack itemFromString(String rawItem) {
        Material material;
        String[] rawSplit;
        if (rawItem.contains(":")) {
            rawSplit = rawItem.split(":");
        } else {
            rawSplit = new String[]{rawItem};
        }
        try {
            material = Material.valueOf(rawSplit[0]);
        } catch (IllegalArgumentException ex) {
            material = Material.DIRT;
        }
//        short damage = 0;
//        if (rawSplit.length > 1) {
//            try {
//                damage = Short.valueOf(rawSplit[1]);
//            } catch (IllegalArgumentException ignored) {}
//        }
        return new ItemStack(material);
    }

    public void sendMessage(CommandSender p, ConfigValues.Message message, Object... params) {
        if (!main.getConfigValues().getMessage(message, params).equals("")) {
            p.sendMessage(main.getConfigValues().getMessage(message, params));
        }
    }

    private void addGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.LURE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    public void clearChunks(int chunkBusterArea, Location chunkBusterLocation, Player p) {
        if (chunkBusterArea % 2 != 0) {
            World world = chunkBusterLocation.getWorld();
            // Variables for the area to loop through
            int upperBound = ((chunkBusterArea - 1) / 2);
            int lowerBound = (chunkBusterArea - 1) / -2;

            final Chunk originalChunk = chunkBusterLocation.getChunk();
            final Chunk minChunk = world.getChunkAt(originalChunk.getX() + lowerBound, originalChunk.getZ() + lowerBound);
            final Chunk maxChunk = world.getChunkAt(originalChunk.getX() + upperBound, originalChunk.getZ() + upperBound);

            main.getHookUtils().getChunkClearQueueProvider().clearChunks(p, minChunk, maxChunk);
            main.getUtils().sendMessage(p, ConfigValues.Message.CLEARING_CHUNKS);
        } else {
            p.sendMessage(color("&cInvalid chunk buster!"));
        }
    }

    public boolean insideBorder(Block block, WorldBorder border) {
        return border.isInside(block.getLocation());
    }

    public void updateConfig(ChunkBuster main) { // Basic config updater that saves the old config, loads the new one, and inserts the old keys
        if (main.getConfigValues().getConfigVersion() < 2.2) {
            Map<String, Object> oldValues = new HashMap<>();
            for (String oldKey : main.getConfig().getKeys(true)) {
                oldValues.put(oldKey, main.getConfig().get(oldKey));
            }
            main.saveResource("config.yml", true);
            main.reloadConfig();
            for (String newKey : main.getConfig().getKeys(true)) {
                if (oldValues.containsKey(newKey) && !oldValues.get(newKey).equals(main.getConfig().get(newKey))) {
                    main.getConfig().set(newKey, oldValues.get(newKey));
                }
            }
            main.getConfig().set("config-version", 2.2);
            main.saveConfig();
        }
    }

    public void checkUpdates(Player p) { // Grabs the version from the spigot api and checks it!
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=60057");
                    URLConnection connection = url.openConnection();
                    connection.setReadTimeout(5000);
                    connection.addRequestProperty("User-Agent", "ChunkBuster update checker");
                    connection.setDoOutput(true);
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String newestVersion = reader.readLine();
                    reader.close();
                    List<Integer> newestVersionNumbers = new ArrayList<>();
                    List<Integer> thisVersionNumbers = new ArrayList<>();
                    try {
                        for (String s : newestVersion.split(Pattern.quote("."))) {
                            newestVersionNumbers.add(Integer.parseInt(s));
                        }
                        for (String s : main.getDescription().getVersion().split(Pattern.quote("."))) {
                            thisVersionNumbers.add(Integer.parseInt(s));
                        }
                    } catch (Exception ex) {
                        return;
                    }
                    for (int i = 0; i < 3; i++) {
                        if (newestVersionNumbers.get(i) != null && thisVersionNumbers.get(i) != null) {
                            if (newestVersionNumbers.get(i) > thisVersionNumbers.get(i)) {
                                TextComponent newVersion = new TextComponent("A new version of ChunkBuster, " + newestVersion + " is available. Download it by clicking here.");
                                newVersion.setColor(ChatColor.RED);
                                newVersion.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/chunkbuster-1-8-1-13-clear-any-area-of-land.60057/"));
                                p.spigot().sendMessage(newVersion);
                                return;
                            } else if (thisVersionNumbers.get(i) > newestVersionNumbers.get(i)) {
                                p.sendMessage(color("&cYou are running a development version of ChunkBuster, " + main.getDescription().getVersion() + ". The latest online version is " + newestVersion + "."));
                                return;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }.runTaskAsynchronously(main);
    }

    public void addWaterChunk(@NotNull Chunk chunk) {
        this.waterChunks.add(chunk);
    }

    public Set<Chunk> getWaterChunks() {
        return waterChunks;
    }

    public ItemStack getChunkBusterItem(int giveAmount, int chunkArea) {
        ItemStack item = new ItemStack(main.getConfigValues().getChunkBusterMaterial(), giveAmount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(main.getConfigValues().getChunkBusterName());
        itemMeta.setLore(main.getConfigValues().getChunkBusterLore(chunkArea));
        item.setItemMeta(itemMeta);
        if (main.getConfigValues().itemShouldGlow()) {
            addGlow(item);
        }

        item.editMeta(meta -> meta.getPersistentDataContainer().set(CHUNKBUSTER_RADIUS_KEY, PersistentDataType.INTEGER, chunkArea));
        return item;
    }
}
