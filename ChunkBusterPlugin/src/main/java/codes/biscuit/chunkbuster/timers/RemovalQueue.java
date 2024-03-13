package codes.biscuit.chunkbuster.timers;

import codes.biscuit.chunkbuster.ChunkBuster;
import codes.biscuit.chunkbuster.hooks.ChunkClearQueueProvider;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class RemovalQueue extends BukkitRunnable {

    private ChunkBuster main;
    private LinkedList<Block> blocks = new LinkedList<>();
    private Player p;

    public RemovalQueue(ChunkBuster main, Player p) {
        this.main = main;
        this.p = p;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        for (int count = 0; count < main.getConfigValues().getBlockPerTick(); count++) {
            if (!blocks.isEmpty()) {
                Block b = blocks.getFirst();
                if (!b.getType().equals(Material.AIR)) {
                    main.getHookUtils().logBlock(p, b.getLocation(), b.getType(), b.getData());
                    b.setType(Material.AIR, false);
                } else {
                    count--;
                }
                blocks.removeFirst();
            } else {
                break;
            }
        }
        if (blocks.isEmpty()) {
            cancel();
        }
    }

    public LinkedList<Block> getBlocks() {
        return this.blocks;
    }

    public static class DefaultChunkClearQueueProvider implements ChunkClearQueueProvider {

        private final ChunkBuster main = ChunkBuster.getInstance();

        @Override
        public void clearChunks(@NotNull Player player, @NotNull Chunk minChunk, @NotNull Chunk maxChunk) {
            RemovalQueue removalQueue = new RemovalQueue(main, player);

            final World world = minChunk.getWorld();

            for (int y = world.getMinHeight(); y <= world.getMaxHeight(); y++) {
                for (int chunkX = minChunk.getX(); chunkX < maxChunk.getX(); chunkX++) { // Loop through the area
                    for (int chunkZ = minChunk.getZ(); chunkZ < maxChunk.getZ(); chunkZ++) { // Get the chunk
                        Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                        Location chunkCheckLoc = chunk.getBlock(7, 60, 7).getLocation(); // Check the chunk
                        if (main.getHookUtils().compareLocToPlayer(chunkCheckLoc, player)) {
                            main.getUtils().addWaterChunk(chunk);
                            for (int x = 0; x < 16; x++) { // Clear the chunk
                                for (int z = 0; z < 16; z++) {
                                    Block b = chunk.getBlock(x, y, z);
                                    if (b.getType() != Material.AIR && !main.getConfigValues().getIgnoredBlocks().contains(b.getType())) {
                                        if (!main.getConfigValues().worldborderHookEnabled() || main.getUtils().insideBorder(b, world.getWorldBorder())) {
                                            removalQueue.getBlocks().add(b);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            removalQueue.runTaskTimer(main, 1L, 1L);
        }
    }

}
