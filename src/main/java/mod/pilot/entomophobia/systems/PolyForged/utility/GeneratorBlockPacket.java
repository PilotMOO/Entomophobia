package mod.pilot.entomophobia.systems.PolyForged.utility;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Class used for weighted random BlockStates for use in the PolyForged system. Feed in blocks and a weight
 * (or don't, and it will auto-assign that block with the defined default weight) and use getRandomState()
 * to generate a random BlockState from the list, using the weights to influence the outcome.
 * Note! Weights aren't in the ratio Weight/100, instead it's Weight/CumulativeWeights. Ergo a weight of 50 is NOT 50%
 * (If weight is 40 and total weight is 300, then it would be 40/300 = 0.13...3, or roughly 13.333...%)
 * */
public class GeneratorBlockPacket extends ArrayList<BlockState> {
    /**RandomSource solely for generating random doubles for testing weights*/
    private static final RandomSource random = RandomSource.create();
    /**The default weight assigned to any untagged BlockStates, assign it in the constructor*/
    private final int defaultWeight;

    /**The Hashmap of all the BlockStates and their "raw" weights, the integer value assigned when added*/
    protected final HashMap<BlockState, Integer> RawWeightHashmap;
    /**The Hashmap of all the BlockStates and their "true" weights,
     * the double value that is generated and evaluated for generating random BlockStates*/
    protected HashMap<BlockState, Double> TrueWeightHashmap;

    /**
     * One of the constructors, allows you to feed in a pre-made hashmap to be the raw weights
     * @param rawWeightHashmap The hashmap that will serve as the raw weights (MUST be a hashmap of Key:BlockState, Value:Integer)
     * @param defaultWeight The default weight to assign to untagged BlockStates, required by all constructors
     */
    public GeneratorBlockPacket(HashMap<BlockState, Integer> rawWeightHashmap, int defaultWeight){
        super(rawWeightHashmap.keySet());
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = rawWeightHashmap;
        CalculateTrueWeights();
    }

    /**
     * One of the constructors, allows you to feed in a Collection instead of a hashmap.
     * NOTE! all states added this way will be tagged with the default weight (assigned in the second argument)
     * @param states The collection of BlockStates to add to the Packet
     * @param defaultWeight The default weight to assign to untagged BlockStates, required by all constructors
     */
    public GeneratorBlockPacket(Collection<? extends BlockState> states, int defaultWeight){
        super();
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>();
        this.addAll(states);
    }

    /**
     * One of the constructors, allows you to feed in a Collection of both BlockStates and Integers.
     * Indexes will be synced, so a state with index [5] will get a weight of index [5] in the weights collection, if available
     * (Will default to the default weight if unavailable, weights outside the range of the states collections will not be used)
     * @param states The collection of BlockStates to add to the Packet
     * @param weights The collection of corresponding integers to add as weights to the Packet
     * @param defaultWeight The default weight to assign to untagged BlockStates, required by all constructors
     */
    public GeneratorBlockPacket(Collection<? extends BlockState> states, Collection<Integer> weights, int defaultWeight){
        super();
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>();
        this.addAll(states, weights);
    }

    /**
     * One of the constructors, allows you to define the default capacity of the Hashmaps and ArrayList
     * @param initialCapacity The default capacity
     * @param defaultWeight The default weight to assign to untagged BlockStates, required by all constructors
     */
    public GeneratorBlockPacket(int initialCapacity, int defaultWeight){
        super(initialCapacity);
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>(initialCapacity);
    }

    /**
     * One of the constructors, returns an empty Packet to be filled with add() or addAll().
     * This or the one that requires a Hashmap argument is recommended
     * @param defaultWeight The default weight to assign to untagged BlockStates, required by all constructors
     */
    public GeneratorBlockPacket(int defaultWeight){
        super();
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>();
    }

    /**
     * Adds a BlockState to the Packet, assigning the default weight
     * @param blockState The BlockState to be added to the Packet
     * @return Always true, as this class extends from ArrayList
     */
    @Override
    public boolean add(BlockState blockState) {
        return add(blockState, defaultWeight);
    }

    /**
     * Adds a BlockState to the Packet, assigning the provided weight
     * @param blockState The BlockState to be added to the Packet
     * @param weight The weight of the BlockState
     *               (See comment about the GeneratorBlockPacket as a whole for a debrief how weights work)
     * @return Always true, as this class extends from ArrayList
     */
    public boolean add(BlockState blockState, int weight){
        RawWeightHashmap.put(blockState, weight);
        boolean flag = super.add(blockState);
        CalculateTrueWeights();
        return flag;
    }

    /**
     * Adds a collection of BlockStates to the Packet, assigning the default weight
     * @param c The collection of BlockStates to be added to the Packet
     * @return true if the ArrayList was changed
     */
    @Override
    public boolean addAll(Collection<? extends BlockState> c) {
        return addAll(c, null);
    }

    /**
     * Adds a collection of BlockStates to the Packet, assigning the defined weight of the same index
     * @param c The collection of BlockStates to be added to the Packet
     * @param w The collection of Weights to be assigned to the BlockState of the same index
     * @return
     */
    public boolean addAll(Collection<? extends BlockState> c, @Nullable Collection<Integer> w) {
        int i = 0;
        List<Integer> weights = w != null ? new ArrayList<>(w) : null;
        for (BlockState bState : c){
            add(bState, weights != null && weights.size() > i ? weights.get(i) : defaultWeight);
            i++;
        }
        return super.addAll(c);
    }

    @Override
    public BlockState remove(int index) {
        BlockState toRemove = this.get(index);
        RawWeightHashmap.remove(toRemove);
        TrueWeightHashmap.remove(toRemove);
        return super.remove(index);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i <= toIndex; i++){
            BlockState toRemove = this.get(i);
            RawWeightHashmap.remove(toRemove);
            TrueWeightHashmap.remove(toRemove);
        }
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * Gets the "raw" weight of a BlockState defined in the Packet
     * ("Raw" weights are defined when they are added to the Packet)
     * @param bState The BlockState to get the weight of
     * @return The "raw" weight of the BlockState, or -1 if not present
     */
    public int getRawWeight(BlockState bState){
        return RawWeightHashmap.getOrDefault(bState, -1);
    }

    /**
     * Gets the "true" weight of a BlockState defined in the Packet
     * ("True" weights are positive doubles less than or equal to 1 used in evaluating chances.
     * They are in decimal percent format [ergo 0.25 is 25%, vise versa])
     * @param bState The BlockState to get the weight of
     * @return The "true" weight of the BlockState, or 0 if not present
     */
    public double getTrueWeight(BlockState bState){
        return TrueWeightHashmap.getOrDefault(bState, 0d);
    }

    /**
     * Randomly selects a BlockState in this Packet, using weights. BlockStates with a higher assigned weight has a higher chance of returning
     * @return A random weighted BlockState, or null if calculations fail or the Hashmap is empty
     */
    public @Nullable BlockState getRandomState(){
        if (RawWeightHashmap.isEmpty()) return null;

        double randomSeed = random.nextDouble();
        for (BlockState bState : this){
            if ((randomSeed -= getTrueWeight(bState)) <= 0){
                return bState;
            }
        }
        return null;
    }

    /**
     * Calculates the true weight of each BlockState present based off of [(1 / cumulativeWeight) * rawWeight]
     * before assigning them all to the true weight Hashmap
     */
    private void CalculateTrueWeights() {
        HashMap<BlockState, Double> toSet = new HashMap<>();
        int totalWeight = 0;
        for (BlockState bState : this){
            int raw = getRawWeight(bState);
            if (raw != -1) totalWeight += raw;
        }
        double trueWeightRatio = (double) 1 / totalWeight;
        for (BlockState bState : this){
            int raw = getRawWeight(bState);
            if (raw != -1) toSet.put(bState, trueWeightRatio * raw);
        }
        this.TrueWeightHashmap = toSet;
    }
}
