package codes.biscuit.chunkbuster.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import codes.biscuit.chunkbuster.ChunkBuster;

public class OtherEvents implements Listener {

    private ChunkBuster main;

    public OtherEvents(ChunkBuster main) {
        this.main = main;
    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent e) {
        if (isLiquid(e.getBlock().getType())) {
            if (!main.getUtils().getWaterChunks().contains(e.getBlock().getChunk()) && main.getUtils().getWaterChunks().contains(e.getToBlock().getChunk())) {
                e.setCancelled(true);
            }
        }
    }

    private boolean isLiquid(Material material) {
        return material == Material.WATER
                || material == Material.LAVA
                || material.name().equals("STATIONARY_WATER")
                || material.name().equals("STATIONARY_LAVA");
    }
}
