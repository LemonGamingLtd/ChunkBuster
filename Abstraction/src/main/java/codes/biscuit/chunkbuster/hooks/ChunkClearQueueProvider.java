package codes.biscuit.chunkbuster.hooks;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ChunkClearQueueProvider {

    /**
     * Handle the clearing of the given chunks.
     *
     * @param player Player performing the action.
     * @param minChunk Min chunk.
     * @param maxChunk Max chunk.
     */
    void clearChunks(@NotNull Player player, @NotNull Chunk minChunk, @NotNull Chunk maxChunk);

}
