package codes.biscuit.chunkbuster;

import ltd.lemongaming.lgcore.platform.cooldown.Cooldown;
import ltd.lemongaming.lgcore.platform.cooldown.data.CooldownDataProvider;
import ltd.lemongaming.lgcore.platform.cooldown.data.MemoryCooldownDataProvider;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * Cooldown for ChunkBuster.
 */
public enum ChunkBusterCooldown implements Cooldown {
    PLACE_COOLDOWN(Duration.ofMinutes(5L)),
    ;

    private static final CooldownDataProvider MEMORY_COOLDOWN_DATA_PROVIDER = new MemoryCooldownDataProvider();

    private final Duration duration;

    ChunkBusterCooldown(@NotNull Duration duration) {
        this.duration = Objects.requireNonNull(duration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Duration getDuration(@NotNull UUID uuid) {
        return duration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CooldownDataProvider getDataProvider() {
        return MEMORY_COOLDOWN_DATA_PROVIDER;
    }
}
