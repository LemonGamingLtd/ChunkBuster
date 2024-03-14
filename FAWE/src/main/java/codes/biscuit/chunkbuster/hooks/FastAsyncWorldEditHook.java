package codes.biscuit.chunkbuster.hooks;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FastAsyncWorldEditHook implements ChunkClearQueueProvider {

    private static final BlockState AIR = Objects.requireNonNull(BlockTypes.AIR).getDefaultState();

    @Override
    public void clearChunks(@NotNull Player player, @NotNull Chunk minChunk, @NotNull Chunk maxChunk) {
        final org.bukkit.World bukkitWorld = minChunk.getWorld();
        final World world = BukkitAdapter.adapt(bukkitWorld);

        final int minChunkY = bukkitWorld.getMinHeight();
        final int maxChunkY = bukkitWorld.getMaxHeight();

        final BlockVector3 minChunkVector = BlockVector3.at(
            minChunk.getX() << 4,
            minChunkY,
            minChunk.getZ() << 4
        );

        final BlockVector3 maxChunkVector = BlockVector3.at(
            maxChunk.getX() << 4 | 15,
            maxChunkY,
            maxChunk.getZ() << 4 | 15
        );

        final CuboidRegion cuboidRegion = new CuboidRegion(world, minChunkVector, maxChunkVector, false);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            final Mask mask = new ChunkClearMask(editSession);
            editSession.replaceBlocks(cuboidRegion, mask, AIR);
        }
    }

    /**
     * Chunk clearing mask.
     */
    public static class ChunkClearMask extends AbstractExtentMask {

        private static final BlockType BEDROCK = Objects.requireNonNull(BlockTypes.BEDROCK);

        /**
         * Construct a new mask.
         *
         * @param extent the extent
         */
        protected ChunkClearMask(Extent extent) {
            super(extent);
        }

        @Override
        public boolean test(BlockVector3 vector) {
            return test(getExtent().getBlock(vector).getBlockType());
        }

        @Override
        public boolean test(Extent extent, BlockVector3 vector) {
            return test(extent.getBlock(vector).getBlockType());
        }

        public boolean test(BlockType block) {
            return block != BEDROCK;
        }

        @Override
        public Mask copy() {
            return new ChunkClearMask(getExtent());
        }
    }
}
