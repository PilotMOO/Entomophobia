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
     * If the given plant is "alive", ergo actively ticking and attempting to grow. For use in performance so dead-end plants wont continue to tick
     */
    public static final BooleanProperty ALIVE = BooleanProperty.create("alive");
}
