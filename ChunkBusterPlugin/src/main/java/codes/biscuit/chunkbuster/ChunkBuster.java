package codes.biscuit.chunkbuster;

import codes.biscuit.chunkbuster.commands.ChunkBusterCommand;
import codes.biscuit.chunkbuster.events.OtherEvents;
import codes.biscuit.chunkbuster.events.PlayerEvents;
import codes.biscuit.chunkbuster.hooks.HookUtils;
import codes.biscuit.chunkbuster.hooks.MetricsLite;
import codes.biscuit.chunkbuster.utils.ConfigValues;
import codes.biscuit.chunkbuster.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkBuster extends JavaPlugin {

    public static NamespacedKey CHUNKBUSTER_RADIUS_KEY;

    private ConfigValues configValues = new ConfigValues(this);
    private Utils utils = new Utils(this);
    private HookUtils hookUtils;

    @Override
    public void onEnable() {
        CHUNKBUSTER_RADIUS_KEY = new NamespacedKey(this, "chunkbuster_radius");

        hookUtils = new HookUtils(this);
        saveDefaultConfig();
        utils.updateConfig(this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new OtherEvents(this), this);
        ChunkBusterCommand chunkBusterCommand = new ChunkBusterCommand(this);
        getCommand("chunkbuster").setExecutor(chunkBusterCommand);
        getCommand("chunkbuster").setTabCompleter(chunkBusterCommand);
        new MetricsLite(this);
    }

    public Utils getUtils() { return utils; }

    public ConfigValues getConfigValues() { return configValues; }

    public HookUtils getHookUtils() { return hookUtils; }

}
