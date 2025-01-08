package mod.pilot.entomophobia.data;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/**
 * Class used for weighted randomized variables for use in randomized data management. Feed in objects and a weight
 * (or don't, and it will auto-assign that block with the defined default weight) and use {@code getRandomObject()}
 * to generate a random Object from the list, using the weights to influence the outcome.
 * <p></p>
 * Note! Weights aren't in the ratio Weight/100, instead it's Weight/CumulativeWeights. Ergo a weight of 50 is NOT 50%
 * (If weight is 40 and total weight is 250, then it would be 40/250 = 0.16, or 16%)
 * */
public class WeightedRandomizer<T> extends ArrayList<T> {
    /**RandomSource solely for generating random doubles for testing weights*/
    private static final RandomSource random = RandomSource.create();
    /**The default weight assigned to any untagged Objects, assign it in the constructor or via {@code AdjustWeight(int, boolean)}*/
    private int defaultWeight;

    /**The Hashmap of all the Objects and their "raw" weights, the integer value assigned when added*/
    protected HashMap<T, Integer> RawWeightHashmap;
    /**The Hashmap of all the Objects and their "true" weights,
     * the double value that is generated and evaluated for generating random Objects*/
    protected HashMap<T, Double> TrueWeightHashmap;

    /**
     * One of the constructors, allows you to feed in a pre-made hashmap
     * @param rawWeightHashmap The hashmap that will serve as the raw weights (MUST be a hashmap of Key:Parameter Object, Value:Integer)
     * @param defaultWeight The default weight to assign to untagged Objects, required by all constructors
     */
    public WeightedRandomizer(HashMap<T, Integer> rawWeightHashmap, int defaultWeight){
        super(rawWeightHashmap.keySet());
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = rawWeightHashmap;
        CalculateTrueWeights();
    }

    /**
     * One of the constructors, allows you to feed in a Collection instead of a hashmap.
     * NOTE! all Objects added this way will be untagged and therefor assigned the default weight (declared in the second argument)
     * @param states The collection of Objects to add to the Packet
     * @param defaultWeight The default weight to assign to untagged Objects, required by all constructors
     */
    public WeightedRandomizer(Collection<? extends T> states, int defaultWeight){
        super();
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>();
        this.addAll(states);
    }

    /**
     * One of the constructors, allows you to feed in a Collection of both Objects and Integers.
     * Indexes will be synced, so an Object with index [5] will get a weight of index [5] in the weights collection, if available
     * (Will default to the default weight if unavailable, weights outside the range of the Object collections will not be used)
     * @param states The collection of Objects to add to the Packet
     * @param weights The collection of corresponding integers to add as weights to the Randomizer
     * @param defaultWeight The default weight to assign to untagged Objects, required by all constructors
     */
    public WeightedRandomizer(Collection<? extends T> states, Collection<Integer> weights, int defaultWeight){
        super();
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>();
        this.addAll(states, weights);
    }

    /**
     * One of the constructors, allows you to define the default capacity of the Hashmaps and ArrayList
     * @param initialCapacity The default capacity
     * @param defaultWeight The default weight to assign to untagged Objects, required by all constructors
     */
    public WeightedRandomizer(int initialCapacity, int defaultWeight){
        super(initialCapacity);
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>(initialCapacity);
    }

    /**
     * One of the constructors, returns an empty Randomizer to be filled with add() or addAll().
     * This or the constructor that requires a Hashmap argument is recommended
     * @param defaultWeight The default weight to assign to untagged Objects, required by all constructors
     */
    public WeightedRandomizer(int defaultWeight){
        super();
        this.defaultWeight = defaultWeight;
        this.RawWeightHashmap = new HashMap<>();
    }

    /**
     * Changes the default weight this Randomizer assigns untagged Objects. Optionally will readjust all untagged elements as well
     * @param newWeight The new default weight to assign
     * @param reassignUntagged Whether to reassign untagged Objects with the new default weight, or leave them unchanged
     *                         (Objects that share the same weight with the new default weight will be marked as untagged for the next shift)
     */
    public void adjustWeight(int newWeight, boolean reassignUntagged){
        if (reassignUntagged){
            for (T t : this){
                if (getRawWeight(t) == defaultWeight){
                    RawWeightHashmap.replace(t, newWeight);
                }
            }
            CalculateTrueWeights();
        }
        defaultWeight = newWeight;
    }

    /**
     * Clears out and replaces the Randomizer's entries with the ones defined in the argument
     * @param replacement The new Hashmap of raw weights
     */
    public void replaceEntriesWith(HashMap<T, Integer> replacement){
        clear();
        this.RawWeightHashmap = replacement;
        super.addAll(replacement.keySet());
        CalculateTrueWeights();
    }

    /**
     * Clears out and replaces the Randomizer's entries with the ones in the Collection
     * (weights for identical objects between old entries and new entries are NOT preserved)
     * @param newEntries A Collection of (untagged) entries to add to the randomizer
     */
    public void replaceEntriesWith(Collection<? extends T> newEntries){
        replaceEntriesWith(newEntries, null);
    }

    /**
     * Clears out and replaces the Randomizer's entries with the ones in the Collection, assigning the defined weight of the same index
     * @param newEntries A Collection of entries to add to the randomizer
     * @param newWeights A Collection of weights to assign to the new entries {@code (Nullable)}
     */
    public void replaceEntriesWith(Collection<? extends T> newEntries, @Nullable Collection<Integer> newWeights){
        clear();
        addAll(newEntries, newWeights);
    }

    /**
     * Adds an Object to the Randomizer, assigning the default weight
     * @param toAdd The Object to be added to the Randomizer
     * @return Always true, as this class extends from ArrayList
     */
    @Override
    public boolean add(T toAdd) {
        return add(toAdd, defaultWeight);
    }

    /**
     * Adds an Object to the Randomizer, assigning the provided weight
     * @param toAdd The Object to be added to the Randomizer
     * @param weight The weight of the Object
     * @return Always true, as this class extends from ArrayList
     */
    public boolean add(T toAdd, int weight){
        RawWeightHashmap.put(toAdd, weight);
        boolean flag = super.add(toAdd);
        CalculateTrueWeights();
        return flag;
    }

    /**
     * Adds a collection of Objects to the Randomizer, assigning the default weight
     * @param c The collection of Objects to be added to the Randomizer
     * @return {@code true} if the ArrayList was changed
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        return addAll(c, null);
    }

    /**
     * Adds a collection of Objects to the Randomizer, assigning the defined weight of the same index
     * @param c The collection of Objects to be added to the Randomizer
     * @param w The collection of Weights to be assigned to the Object of the same index
     * @return {@code true} if the ArrayList was changed
     */
    public boolean addAll(Collection<? extends T> c, @Nullable Collection<Integer> w) {
        int i = 0;
        List<Integer> weights = w != null ? new ArrayList<>(w) : null;
        for (T t : c){
            add(t, weights != null && weights.size() > i ? weights.get(i) : defaultWeight);
            i++;
        }
        return super.addAll(c);
    }

    /**
     * Removes a specific Object at the given index
     * @param index The index of the element to be removed
     * @return The removed Object
     */
    @Override
    public T remove(int index) {
        T toRemove = this.get(index);
        RawWeightHashmap.remove(toRemove);
        TrueWeightHashmap.remove(toRemove);
        return super.remove(index);
    }

    /**
     * Removes a specified Object from the Randomizer
     * @param o Object to be removed from this list, if present
     * @return {@code true} if the element was successfully removed
     */
    @Override
    public boolean remove(Object o) {
        RawWeightHashmap.remove(o);
        TrueWeightHashmap.remove(o);
        return super.remove(o);
    }

    /**
     * Removes all the Objects from a set range
     * @param fromIndex Index of first element to be removed
     * @param toIndex Index after last element to be removed
     */
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = fromIndex; i <= toIndex; i++){
            T toRemove = this.get(i);
            RawWeightHashmap.remove(toRemove);
            TrueWeightHashmap.remove(toRemove);
        }
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * Removes all the Objects from the Randomizer that match with the elements in the given collection
     * @param c Collection containing Objects to be removed from this list
     * @return {@code true} if the list was modified
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c){
            RawWeightHashmap.remove(o);
            TrueWeightHashmap.remove(o);
        }
        return super.removeAll(c);
    }

    /**
     * Removes all the Objects inside the Randomizer that return true when tested by the Predicate
     * @param filter A predicate which returns {@code true} for Objects to be removed
     */
    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        for (T t : this){
            if (filter.test(t)){
                RawWeightHashmap.remove(t);
                TrueWeightHashmap.remove(t);
            }
        }

        return super.removeIf(filter);
    }

    /**
     * Clears out the Randomizer of all entries
     */
    @Override
    public void clear() {
        RawWeightHashmap.clear();
        if (TrueWeightHashmap != null){
            TrueWeightHashmap.clear();
        }
        super.clear();
    }

    /**
     * Gets the "raw" weight of an Object defined in the Randomizer
     * ("Raw" weights are defined when they are added to the Randomizer)
     * @param obj The Object to get the weight of
     * @return The "raw" weight of the Object, or -1 if not present
     */
    public int getRawWeight(T obj){
        return RawWeightHashmap.getOrDefault(obj, -1);
    }

    /**
     * Gets the "true" weight of an Object defined in the Randomizer
     * <p></p>
     * "True" weights are positive doubles less than or equal to 1 used in evaluating chances.
     * They are in decimal percent format (ergo 0.25 is 25%, vise versa)
     * @param obj The Object to get the weight of
     * @return The "true" weight of the Object, or 0 if not present
     */
    public double getTrueWeight(T obj){
        return TrueWeightHashmap.getOrDefault(obj, 0d);
    }

    /**
     * Randomly selects an Object in this Randomizer, using weights. Objects with a higher assigned weight has a higher chance of returning
     * @return A random weighted Object, or null if calculations fail or the Hashmap is empty
     */
    public @Nullable T getRandomWeightedObject(){
        if (RawWeightHashmap.isEmpty()) return null;

        double randomSeed = random.nextDouble();
        for (T t : this){
            if ((randomSeed -= getTrueWeight(t)) <= 0){
                return t;
            }
        }
        return null;
    }

    /**
     * Calculates the true weight of each Object present based off of [(1 / cumulativeWeight) * rawWeight]
     * before assigning them all to the true weight Hashmap
     */
    private void CalculateTrueWeights() {
        HashMap<T, Double> toSet = new HashMap<>();
        int totalWeight = 0;
        for (T t : this){
            int raw = getRawWeight(t);
            if (raw != -1) totalWeight += raw;
        }
        double trueWeightRatio = (double) 1 / totalWeight;
        for (T t : this){
            int raw = getRawWeight(t);
            if (raw != -1) toSet.put(t, trueWeightRatio * raw);
        }
        this.TrueWeightHashmap = toSet;
    }
}
