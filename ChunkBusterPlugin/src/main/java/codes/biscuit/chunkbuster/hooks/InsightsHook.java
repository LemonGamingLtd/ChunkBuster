package codes.biscuit.chunkbuster.hooks;

import dev.frankheijden.insights.api.InsightsPlugin;
import dev.frankheijden.insights.api.concurrent.ScanOptions;
import dev.frankheijden.insights.api.tasks.ScanTask;
import dev.frankheijden.insights.api.util.LazyChunkPartRadiusIterator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class InsightsHook {

    private static final ScanOptions SCAN_OPTIONS = ScanOptions.all();

    private final InsightsPlugin plugin;

    public InsightsHook() {
        this.plugin = InsightsPlugin.getInstance();
    }

    public void scan(@NotNull Chunk chunk, int radius) {
        final World world = chunk.getWorld();
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();

        final LazyChunkPartRadiusIterator it = new LazyChunkPartRadiusIterator(world, chunkX, chunkZ, radius);
        ScanTask.scan(plugin, it, it.getChunkCount(), SCAN_OPTIONS, __ -> {}, __ -> {});
    }
}
