package mod.pilot.entomophobia.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * A Record for all the BlockStateProperties in the mod
 */
public record EntomoBlockStateProperties() {
    /**
     * If the block is "mirrored", ergo the model is flipped. Purely visual.
     */
    public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");
    /**
     * If the given block is "alive", ergo actively ticking and attempting to grow. For use in performance so dead-end plants won't continue to tick
     */
    public static final BooleanProperty ALIVE = BooleanProperty.create("alive");
    /**
     * If the given block is "bloody". For use in Luminous Flesh Bulbs to spawn blood particles and congealed blood
     */
    public static final BooleanProperty BLOODY = BooleanProperty.create("bloody");
}
