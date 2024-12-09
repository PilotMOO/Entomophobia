package mod.pilot.entomophobia.systems.PolyForged.utility;

import mod.pilot.entomophobia.data.WeightedRandomizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GeneratorBlockPacket extends WeightedRandomizer<BlockState> {
    public GeneratorBlockPacket(HashMap<BlockState, Integer> rawWeightHashmap, int defaultWeight){
        super(rawWeightHashmap, defaultWeight);
    }
    public GeneratorBlockPacket(Collection<? extends BlockState> states, int defaultWeight){
        super(states, defaultWeight);
    }
    public GeneratorBlockPacket(Collection<? extends BlockState> states, Collection<Integer> weights, int defaultWeight){
        super(states, weights, defaultWeight);
    }

    public GeneratorBlockPacket(int initialCapacity, int defaultWeight){
        super(initialCapacity, defaultWeight);
    }
    public GeneratorBlockPacket(int defaultWeight){
        super(defaultWeight);
    }
}
