package mod.pilot.entomophobia.systems.nest;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.data.worlddata.NestSaveData;
import mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes.ShapeGenerator;
import mod.pilot.entomophobia.systems.PolyForged.shapes.ChamberGenerator;
import mod.pilot.entomophobia.systems.PolyForged.shapes.HollowSphereGenerator;
import mod.pilot.entomophobia.systems.PolyForged.shapes.TunnelGenerator;
import mod.pilot.entomophobia.systems.PolyForged.utility.GhostSphere;
import mod.pilot.entomophobia.systems.PolyForged.utility.WorldShapeManager;
import mod.pilot.entomophobia.systems.nest.features.Feature;
import mod.pilot.entomophobia.systems.nest.features.FeatureManager;
import mod.pilot.entomophobia.systems.nest.features.FeatureVariantPackage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class Nest {
    public Nest(ServerLevel server, Vec3 start){
        this.server = server;
        origin = start;
        MainChamber = CreateMainChamber();
        Enable();
        NestSaveData.Dirty();
    }
    private Nest(ServerLevel server, Vec3 start, byte state, Chamber mainChamber){
        this.server = server;
        origin = start;
        NestState = state;
        MainChamber = mainChamber;
    }
    public static Nest ConstructFromBlueprint(ServerLevel server, Vec3 start, byte state, Chamber mainChamber){
        return new Nest(server, start, state, mainChamber.DenoteAsMain());
    }

    public ServerLevel server;
    public static final RandomSource random = RandomSource.create();

    public final Vec3 origin;

    private byte NestState;
    public byte getNestState(){
        return NestState;
    }
    protected void setNestState(byte state){
        NestState = state;
        NestSaveData.Dirty();
    }
    protected void setNestState(NestManager.NestStates state){
        setNestState((byte)state.ordinal());
    }

    public void Disable(){
        setNestState((byte)0);
    }
    public void Enable(){
        setNestState((byte)1);
    }
    public void Finish(){
        setNestState((byte)2);
    }
    public void Kill(boolean killAll){
        setNestState((byte)3);
        MainChamber.Kill(killAll);
    }
    public boolean Alive(){
        return MainChamber.Alive() && getNestState() == 1;
    }
    public boolean Dead(){
        return MainChamber.Dead() || getNestState() == 3;
    }

    public ArrayList<Offshoot> Offshoots(){
        return MainChamber.children;
    }
    public final Chamber MainChamber;
    private Chamber CreateMainChamber(){
        return new Chamber(server, null, origin,
                Config.NEST.large_chamber_max_size.get(), Config.NEST.large_chamber_thickness.get()).DenoteAsMain();
    }

    public void NestTick(){
        if (getNestState() != 1){
            return;
        }
        if (!MainChamber.Dead()){
            MainChamber.OffshootTick(true, true, -1);
        }
    }

    public abstract static class Offshoot{
        protected Offshoot(ServerLevel server, byte type, @Nullable Offshoot parent, Vec3 position){
            OffshootType = type;
            this.server = server;
            this.parent = parent;
            this.position = position;
            if (this instanceof Corridor){
                DeadEnd = !shouldThisBecomeAParent();
            }

            Enable();
            NestSaveData.Dirty();
        }
        protected Offshoot(ServerLevel server, byte type, @Nullable Offshoot parent, Vec3 position, byte state, boolean deadEnd){
            OffshootType = type;
            this.OffshootState = state;
            this.server = server;
            this.parent = parent;
            this.position = position;
            DeadEnd = deadEnd;
        }
        protected ServerLevel server;
        public ServerLevel server(){
            return server;
        }
        protected final Vec3 position;
        public Vec3 getPosition(){
            return position;
        }

        @Nullable
        public final Offshoot parent;
        public int getChildIndex(){
            if (parent == null || parent.children == null) return -1;
            for (int i = 0; i < parent.children.size(); i++){
                if (parent.children.get(i) == this){
                    return i;
                }
            }
            return -1;
        }

        @Nullable
        public ArrayList<Offshoot> children;
        public boolean AddToChildren(Offshoot child){
            if (children == null){
                children = new ArrayList<>();
            }
            if (child != null){
                NestSaveData.Dirty();
                return children.add(child);
            }
            return false;
        }

        protected ShapeGenerator generator;
        public ShapeGenerator getGenerator(){
            return generator;
        }
        protected final void setGenerator(ShapeGenerator newGenerator){
            generator = newGenerator;
        }

        public boolean DeadEnd;
        public int MaxChildCount = 0;
        public int getMaxChildCount(){
            if (this instanceof Chamber c && c.isMainChamber()){
                return MaxChildCount + 1;
            }
            return MaxChildCount;
        }
        public int LayersDeep(){
            int layers = 0;
            Offshoot currentParent = this;
            while (currentParent.parent != null){
                layers = currentParent instanceof Corridor ? layers : layers + 1;
                currentParent = currentParent.parent;
            }
            return layers;
        }

        public abstract int getMaxAllowedFeatureCount();
        public abstract double getFeaturePlaceChance();

        public enum OffshootStates{
            disabled,
            active,
            finished
        }
        public enum OffshootTypes{
            empty,
            chamber,
            corridor
        }

        private byte OffshootState;
        public byte getOffshootState(){
            return OffshootState;
        }
        protected void setOffshootState(byte state){
            OffshootState = state;
            NestSaveData.Dirty();
        }
        public void Disable(){ setOffshootState((byte)0); }
        public void Enable(){ setOffshootState((byte)1); }
        public void Finish(){ setOffshootState((byte)2); }
        private final byte OffshootType;
        public byte getOffshootType(){
            return OffshootType;
        }
        protected boolean finishedWithFeatures;
        public boolean isFinishedWithFeatures(){
            return finishedWithFeatures;
        }
        public void setFinishedPlacingFeatures(boolean flag){
            finishedWithFeatures = flag;
        }

        public boolean Alive(){
            return generator.isActive() && getOffshootState() == 1;
        }
        public boolean Dead(){
            return getOffshootState() == 0 || generator.isOfState(WorldShapeManager.GeneratorStates.disabled);
        }
        public boolean Finished(){
            return getOffshootState() == 2 && generator.isOfState(WorldShapeManager.GeneratorStates.done);
        }
        public void Kill(boolean continuous) {
            setOffshootState((byte)0);
            if (generator != null){
                generator.Disable();
            }
            if (continuous && children != null){
                for (Offshoot child : children){
                    child.Kill(true);
                }
            }
            NestSaveData.Dirty();
        }

        protected ArrayList<GhostSphere> QueuedGhostPositions;
        public void addToQueuedGhostPositions(ArrayList<GhostSphere> ghostsLists){
            if (QueuedGhostPositions == null) QueuedGhostPositions = new ArrayList<>();
            QueuedGhostPositions.addAll(ghostsLists);
        }
        public void addToQueuedGhostPosition(GhostSphere ghost){
            if (QueuedGhostPositions == null) QueuedGhostPositions = new ArrayList<>();
            QueuedGhostPositions.add(ghost);
        }
        public ArrayList<GhostSphere> getQueuedGhostPositions(){
            if (QueuedGhostPositions == null) QueuedGhostPositions = new ArrayList<>();
            return new ArrayList<>(QueuedGhostPositions);
        }
        public void removeQueuedGhosts(ArrayList<GhostSphere> toRemove){
            if (QueuedGhostPositions == null) {
                QueuedGhostPositions = new ArrayList<>();
                return;
            }
            QueuedGhostPositions.removeAll(toRemove);
        }
        protected void RegisterAllGhosts(){
            if (getGenerator() == null || QueuedGhostPositions == null || QueuedGhostPositions.size() == 0) return;
            ArrayList<GhostSphere> toRemove = new ArrayList<>();

            if (getGenerator() instanceof ChamberGenerator C){
                for (GhostSphere ghost : getQueuedGhostPositions()){
                    C.addToGhostShapes(ghost);
                    toRemove.add(ghost);
                }
            }
            else if (getGenerator() instanceof TunnelGenerator T){
                for (GhostSphere ghost : getQueuedGhostPositions()){
                    T.addToGhostSpheres(ghost);
                    toRemove.add(ghost);
                }
            }
            removeQueuedGhosts(toRemove);
        }

        protected boolean shouldGeneratorTick(){
            return OffshootState == 1 && generator != null && generator.isActive();
        }
        public boolean areAnyOfMyChildrenAlive(){
            if (children == null) return false;
            for (Offshoot child : children){
                if (child.Alive()) return true;
            }
            return false;
        }
        public boolean shouldThisBecomeAParent(){
            if (DeadEnd) return false;

            if (this instanceof Corridor) return children == null;

            if (parent == null){
                return (children == null || children.size() < getMaxChildCount()) && !areAnyOfMyChildrenAlive();
            }

            if (LayersDeep() > NestManager.getNestMaxLayers()){
                return false;
            }
            else{
                return children == null || children.size() < getMaxChildCount();
            }
        }
        public abstract boolean canSupportFeatures();
        public boolean featurePlacePrecheck(){
            return canSupportFeatures() && !isFinishedWithFeatures()
                    && Finished() && !shouldThisBecomeAParent() && !areAnyOfMyChildrenAlive();
        }
        protected boolean isFeaturePositionValid(Vec3 pos, Feature feature,
                                                          @Nullable Direction facing, HashMap<Vec3, Feature> alreadyPlaced){
            //ToDo: Add testing for wall features IF required, otherwise remove
            if (children != null){
                for (Offshoot o : children){
                    if (o.position.distanceTo(pos) < (o instanceof Chamber c1 ? c1.radius
                            : o instanceof Corridor c2 ?(double)c2.weight / 2 : 0)){
                        System.out.println("Position " + pos + " was invalid because it was too close to an offshoot");
                        return false;
                    }
                }
            }
            return testFeatureDistance(pos, feature, alreadyPlaced);
        }
        protected final boolean testFeatureDistance(Vec3 pos, Feature toTest, HashMap<Vec3, Feature> existing){
            Vec3i toTestSize = toTest.getTemplate(server, null).getSize();
            AABB toTestAABB = AABB.ofSize(pos, toTestSize.getX(), toTestSize.getY(), toTestSize.getZ());
            for (Vec3 pos1 : existing.keySet()){
                Feature f = existing.get(pos1);
                Vec3i existingSize = f.size();
                AABB existingAABB = AABB.ofSize(pos, existingSize.getX(), existingSize.getY(), existingSize.getZ());
                if (toTestAABB.intersects(existingAABB)){
                    System.out.println("Position " + pos + " was invalid because it was too close to a preexisting feature");
                    return false;
                }
            }
            return true;
        }
        protected abstract Vec3 generateFeaturePlacementPosition(byte placementPos);
        protected abstract Pair<Vec3, Direction> generateFeaturePlacementPositionForWall(byte placementPos);

        public final void TickGenerator(){
            RegisterAllGhosts();
            generator.Build();
            if (generator.isOfState(WorldShapeManager.GeneratorStates.done)) Finish();
        }
        public void placeFeatures(){
            HashMap<Vec3, Feature> placedFeaturePosHashmap = new HashMap<>();

            int cycle = 0; //TEMPORARY FOR DEBUGGING
            for (int i = 0; i < getMaxAllowedFeatureCount(); i++){
                if (getFeaturePlaceChance() < random.nextDouble()) continue;

                Feature f = generateRandomValidFeature(); if (f == null) return;
                Pair<Vec3, Direction> placePair = generateAndTestFeaturePosition(f, placedFeaturePosHashmap);
                if (placePair == null || placePair.getA() == null) return;

                if (f.Place(placePair.getA(), server, null, placePair.getB())){
                    placedFeaturePosHashmap.put(placePair.getA(), f);
                    System.out.println("Successfully placed " + f + " at " + placePair.getA());
                    cycle++;
                }
                else{
                    System.out.println("FAILED to place " + f + " at " + placePair.getA());
                }
            }
            System.out.println("Placed " + cycle + " features!");
            setFinishedPlacingFeatures(true);
        }
        protected @Nullable Feature generateRandomValidFeature(){
            Feature f;
            byte placePosType;
            int cycle = 0;
            do{
                placePosType = (byte)random.nextInt(1, 4);
                f = FeatureManager.FeatureTypeHolder.getRandomFeature(getOffshootType(), placePosType);
            } while (f == null && cycle++ < 10);
            if (f == null){
                System.out.println("An offshoot was unable to retrieve a random feature for Offshoot Type (" +
                        getOffshootType() + ") and Placement Position (" + placePosType + ") in " + cycle + " attempts.");
                return null;
            }
            return f.isVariantPackage() ? ((FeatureVariantPackage)f).getRandomInstance() : f;
        }
        protected @Nullable Pair<Vec3, Direction> generateAndTestFeaturePosition(Feature f, final HashMap<Vec3, Feature> alreadyPlaced){
            Vec3 placePos;
            @Nullable Direction facing = null;

            int cycle = 0;
            do {
                if (f.isWallFeature()) {
                    Pair<Vec3, Direction> placePair = generateFeaturePlacementPositionForWall(f.PlacementPos);
                    placePos = placePair.getA();
                    facing = placePair.getB();
                }
                else placePos = generateFeaturePlacementPosition(f.PlacementPos);
            } while (!isFeaturePositionValid(placePos, f, facing, alreadyPlaced) && cycle++ < 10);

            if (isFeaturePositionValid(placePos, f, facing, alreadyPlaced)){
                return new Pair<>(placePos, facing);
            }
            else{
                System.out.println("An offshoot was unable to place " + f
                        + " because the offshoot failed to generate a valid placement position in " + cycle + " attempts.");
                return null;
            }
        }

        public void OffshootTick(boolean tickChildren, boolean continuous, int layers){
            if (shouldGeneratorTick()){
                TickGenerator();
            }
            if (tickChildren && children != null){
                for (Offshoot child : children) {
                    child.OffshootTick(continuous, layers != 0, layers - 1);
                }
            }
            if (featurePlacePrecheck()) placeFeatures();
        }

        public final Offshoot ConstructNewChild(byte newShootType){
            Offshoot child;
            Vec3 OffshootPos = getOffshootPosition();
            if (OffshootPos == null){
                return null;
            }
            switch (newShootType){
                default -> child = null;
                case 1 -> {
                    switch (random.nextIntBetweenInclusive(1, 3)){
                        default -> child = new Chamber(server, this, OffshootPos, NestManager.getRandomSmallChamberRadius(random),
                                NestManager.getNestSmallChamberThickness());
                        case 2 -> child = new Chamber(server, this, OffshootPos, NestManager.getRandomMediumChamberRadius(random),
                                NestManager.getNestMediumChamberThickness());
                        case 3 -> child = new Chamber(server, this, OffshootPos, NestManager.getRandomLargeChamberRadius(random),
                                NestManager.getNestLargeChamberThickness());
                    }
                }
                case 2 -> {
                    switch (random.nextIntBetweenInclusive(1, 3)){
                        default -> child = new Corridor(server, this, OffshootPos, NestManager.getRandomSmallCorridorRadius(random),
                                NestManager.getNestSmallCorridorThickness());
                        case 2 -> child = new Corridor(server, this, OffshootPos, NestManager.getRandomMediumCorridorRadius(random),
                                NestManager.getNestMediumCorridorThickness());
                        case 3 -> child = new Corridor(server, this, OffshootPos, NestManager.getRandomLargeCorridorRadius(random),
                                NestManager.getNestLargeCorridorThickness());
                    }
                }
            }
            this.AddToChildren(child);
            return child;
        }

        protected abstract Vec3 getOffshootPosition();
    }
    public static class Chamber extends Offshoot{
        private static final byte OffshootType = 1;
        public Chamber(ServerLevel server, @org.jetbrains.annotations.Nullable Nest.Offshoot parent, Vec3 pos, int radius, int thickness) {
            super(server, OffshootType, parent, pos);
            super.MaxChildCount = 2;
            this.radius = radius;
            this.thickness = thickness;
            ConstructGenerator(server, getPosition(), radius, thickness);
        }
        protected void ConstructGenerator(ServerLevel server, Vec3 pos, int radius, int thickness) {
            ChamberGenerator generator = WorldShapeManager.CreateChamber(server, NestManager.getNestBuildSpeed(), NestManager.getNestBlocks(),
                    pos, true, NestManager.getNestMaxHardness(), radius, thickness, 0.5, true);
            if (parent != null && parent instanceof Corridor corridor && corridor.getGenerator() instanceof TunnelGenerator tunnel){
                for (GhostSphere ghost : tunnel.getGhostLineSpheres((radius + thickness) * 2, true)){
                    addToQueuedGhostPosition(ghost);
                }
            }

            setGenerator(generator);
            RegisterAllGhosts();
        }

        //Blueprint Construction, for use in unpacking
        public static Chamber ConstructFromBlueprint(ServerLevel server, @Nullable Offshoot parent, Vec3 pos,
                                                     int radius, int thickness, boolean deadEnd,
                                                     byte state, boolean main, boolean featuresFinished){
            Chamber chamber = new Chamber(server, parent, pos, radius, thickness, deadEnd, state, featuresFinished);
            if (main){
                chamber.DenoteAsMain();
            }
            if (parent != null){
                parent.AddToChildren(chamber);
            }
            return chamber;
        }
        //Private constructor for use in blueprint unpacking
        private Chamber(ServerLevel server, @org.jetbrains.annotations.Nullable Nest.Offshoot parent, Vec3 pos,
                        int radius, int thickness, boolean deadEnd,
                        byte state, boolean featuresFinished) {
            super(server, OffshootType, parent, pos, state, deadEnd);
            super.MaxChildCount = 2;
            this.radius = radius;
            this.thickness = thickness;
            this.finishedWithFeatures = featuresFinished;
            ConstructGenerator(server, getPosition(), radius, thickness);
        }


        public final int radius;
        public final int thickness;
        private boolean MainChamber = false;
        public boolean isMainChamber(){
            return MainChamber;
        }
        private Chamber DenoteAsMain(){
            MainChamber = true;
            return this;
        }

        @Override
        public void OffshootTick(boolean tickChildren, boolean continuous, int layers) {
            super.OffshootTick(tickChildren, continuous, layers);
            if (getGenerator() != null && getGenerator().isOfState(WorldShapeManager.GeneratorStates.done)
                    && getOffshootState() == 2 && !areAnyOfMyChildrenAlive()){
                if (shouldThisBecomeAParent()){
                    if (isMainChamber()){
                        boolean noEntrance = true;
                        if (children != null){
                            for (Offshoot O : children){
                                if (O instanceof Corridor c && c.isEntrance()){
                                    noEntrance = false;
                                }
                            }
                        }
                        if (noEntrance){
                            Corridor C;
                            switch (random.nextIntBetweenInclusive(1, 3)){
                                default -> C = new Corridor(server, this, getOffshootPosition(),
                                        NestManager.getRandomSmallCorridorRadius(random),
                                        NestManager.getNestSmallCorridorThickness(), true);
                                case 2 -> C = new Corridor(server, this, getOffshootPosition(),
                                        NestManager.getRandomMediumCorridorRadius(random),
                                        NestManager.getNestMediumCorridorThickness(), true);
                                case 3 -> C = new Corridor(server, this, getOffshootPosition(),
                                        NestManager.getRandomLargeCorridorRadius(random),
                                        NestManager.getNestLargeCorridorThickness(), true);
                            }
                            AddToChildren(C);
                            return;
                        }
                    }

                    ConstructNewChild((byte)2);
                }
            }
        }

        @Override
        protected Vec3 getOffshootPosition() {
            HollowSphereGenerator sphereGenerator = (HollowSphereGenerator)generator;
            Vec3 direction;
            Vec3 toReturn;
            int cycleCounter = 0;
            do{
                direction = getPosition().yRot(random.nextInt(-20, 20)).xRot(random.nextInt(-180, 180)).zRot(random.nextInt(-180, 180)).normalize();
                toReturn = getPosition().add(direction.scale(sphereGenerator.radius - sphereGenerator.thickness));
                cycleCounter++;
            }
            while (!TestOffshootPosition(toReturn) && cycleCounter < 10);
            if (TestOffshootPosition(toReturn)) return toReturn;
            else {
                DeadEnd = true;
                return null;
            }
        }

        private boolean TestOffshootPosition(Vec3 pos){
            if (children == null){
                return true;
            }
            boolean flag = true;
            for (Offshoot offshoot : children){
                if (offshoot.getGenerator() instanceof TunnelGenerator tunnel){
                    if (pos.distanceTo(offshoot.getPosition()) < tunnel.weight * 2){
                        flag = false;
                        break;
                    }
                }
            }
            if (parent == null) return flag;
            if (flag && parent.getGenerator() instanceof TunnelGenerator tunnel){
                flag = pos.distanceTo(tunnel.getEnd()) < tunnel.weight * 2;
            }
            return flag;
        }

        @Override
        public int getMaxAllowedFeatureCount() {
            return 2;
        }

        @Override
        public double getFeaturePlaceChance() {
            return 0.5;
        }

        @Override
        public boolean canSupportFeatures() {
            return true;
        }

        @Override
        protected Vec3 generateFeaturePlacementPosition(byte placementPos) {
            int difference = (radius - (thickness * 2));
            int yOffset = switch (placementPos){
                case 0 -> switch (random.nextIntBetweenInclusive(1, 3)){
                    case 2 -> 0;
                    case 3 -> difference;
                    default -> -difference;
                };
                case 2 -> 0;
                case 3 -> difference;
                default -> -difference;
            };

            Vec3 offset;
            if (placementPos != 2){
                offset = new Vec3(random.nextIntBetweenInclusive(0, difference) * (random.nextBoolean() ? 1 : -1),
                        yOffset,
                        random.nextIntBetweenInclusive(0, difference) * (random.nextBoolean() ? 1 : -1));
            }
            else{
                offset = Vec3.ZERO.yRot(random.nextInt(-180, 180))
                        .xRot(random.nextInt(-30, 30))
                        .scale(difference);
            }

            int towards = placementPos == 1 ? 1 : placementPos == 3 ? -1 : 0;
            if (towards != 0){
                BlockPos.MutableBlockPos mBPos = new BlockPos.MutableBlockPos(offset.x, offset.y, offset.z);
                while (!server.getBlockState(mBPos.offset(0, towards, 0)).isAir()){
                    mBPos.move(0, towards, 0);
                    if (placementPos == 1 && mBPos.getY() > 0) break;
                    if (placementPos == 3 && mBPos.getY() < 0) break;
                    if (Math.sqrt(mBPos.offset((int)position.x, (int)position.y, (int)position.z)
                            .distToCenterSqr(position.x, position.y, position.z)) > difference) break;
                }
                mBPos.move(0, -towards * 2, 0);
                offset = mBPos.getCenter();
            }

            return position.add(offset);
        }

        @Override
        protected Pair<Vec3, Direction> generateFeaturePlacementPositionForWall(byte placementPos) {
            return null;
        }
    }
    public static class Corridor extends Offshoot{
        private static final byte OffshootType = 2;
        public Corridor(ServerLevel server, @Nonnull Nest.Offshoot parent, Vec3 position, int weight, int thickness) {
            super(server, OffshootType, parent, position);
            super.MaxChildCount = 1;
            this.weight = weight;
            this.thickness = thickness;
            ConstructGenerator(weight, thickness);
        }
        private Corridor(ServerLevel server, @Nonnull Nest.Offshoot parent, Vec3 position, int weight, int thickness, boolean isEntrance) {
            super(server, OffshootType, parent, position);
            if (isEntrance) this.denoteAsEntrance();
            super.MaxChildCount = 1;
            this.weight = weight;
            this.thickness = thickness;
            ConstructGenerator(weight, thickness);
        }
        protected void ConstructGenerator(int weight, int thickness){
            TunnelGenerator tunnel = WorldShapeManager.CreateTunnel(server, NestManager.getNestBuildSpeed(), NestManager.getNestBlocks(),
                    NestManager.getNestMaxHardness(), getPosition(), GenerateEndPosition(), true, weight, thickness);

            if (parent.getGenerator() instanceof ChamberGenerator chamber){
                addToQueuedGhostPosition(chamber.GenerateInternalGhostSphere());
                if (parent.parent instanceof Corridor parentCorridor && parentCorridor.getGenerator() instanceof TunnelGenerator parentTunnel){
                    for (GhostSphere ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                        addToQueuedGhostPosition(ghost);
                    }
                }
            }
            else if (parent.getGenerator() instanceof TunnelGenerator parentTunnel){
                for (GhostSphere ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                    addToQueuedGhostPosition(ghost);
                }
            }

            setGenerator(tunnel);
            RegisterAllGhosts();
        }

        //Blueprint Construction, for use in unpacking
        public static Corridor ConstructFromBlueprint(ServerLevel server, @Nonnull Offshoot parent, Vec3 position, Vec3 end,
                                                      int weight, int thickness, boolean deadEnd,
                                                      byte state, boolean entrance, boolean finishedFeatures){
            Corridor corridor = new Corridor(server, parent, position, end, weight, thickness, deadEnd, state, entrance, finishedFeatures);
            parent.AddToChildren(corridor);
            return corridor;
        }
        private Corridor(ServerLevel server, @Nonnull Nest.Offshoot parent, Vec3 position, Vec3 end,
                         int weight, int thickness, boolean deadEnd,
                         byte state, boolean isEntrance, boolean finishedFeatures) {
            super(server, OffshootType, parent, position, state, deadEnd);
            if (isEntrance) denoteAsEntrance();
            super.MaxChildCount = 1;
            this.weight = weight;
            this.thickness = thickness;
            this.end = end;
            this.finishedWithFeatures = finishedFeatures;
            ConstructGeneratorFromBlueprint(weight, thickness, end);
        }
        private void ConstructGeneratorFromBlueprint(int weight, int thickness, Vec3 end){
            TunnelGenerator tunnel = WorldShapeManager.CreateTunnel(server, NestManager.getNestBuildSpeed(), NestManager.getNestBlocks(),
                    NestManager.getNestMaxHardness(), getPosition(), end, true, weight, thickness);

            if (parent.getGenerator() instanceof ChamberGenerator chamber){
                addToQueuedGhostPosition(chamber.GenerateInternalGhostSphere());
                if (parent.parent instanceof Corridor parentCorridor && parentCorridor.getGenerator() instanceof TunnelGenerator parentTunnel){
                    for (GhostSphere ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                        addToQueuedGhostPosition(ghost);
                    }
                }
            }
            else if (parent.getGenerator() instanceof TunnelGenerator parentTunnel){
                for (GhostSphere ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                    addToQueuedGhostPosition(ghost);
                }
            }

            if (getOffshootState() == 1 && isEntrance() && DeadEnd){
                addToQueuedGhostPosition(GenerateSurfaceGhost(end));
            }

            setGenerator(tunnel);
            RegisterAllGhosts();
        }

        private boolean entrance = false;
        public boolean isEntrance(){
            return entrance;
        }
        public void denoteAsEntrance(){
            entrance = true;
        }

        public final int weight;
        public final int thickness;

        public int getAmountOfExtensions(){
            int toReturn = 0;
            Corridor currentCorridor = this;
            while (currentCorridor.parent instanceof Corridor corridor){
                currentCorridor = corridor;
            }
            while (currentCorridor.children != null) {
                for (Offshoot child : currentCorridor.children) {
                    if (child instanceof Corridor corridor){
                        toReturn++;
                        currentCorridor = corridor;
                    }
                }
            }
            return toReturn;
        }
        public boolean ShouldGetExtension(){
            return (NestManager.getNestMaxCorridorExtensions() > getAmountOfExtensions()
                    && random.nextDouble() < NestManager.getNestCorridorExtensionChance()
                    && children == null) || (isEntrance() && !DeadEnd);
        }

        public Vec3 end;
        private Vec3 GenerateEndPosition(){
            Vec3 toReturn;

            int cycleCounter = 0;
            if (isEntrance()){
                Vec3 surface = findSurface(getPosition());
                do{
                    Vec3 direction = EntomoDataManager.getDirectionToAFromB(surface, getPosition())
                            .yRot(random.nextIntBetweenInclusive(-180, 180))
                            .xRot(random.nextIntBetweenInclusive(-45, 45))
                            .zRot(random.nextIntBetweenInclusive(-45, 45))
                            .normalize();
                    direction = direction.multiply(1,
                            direction.y < 0 ? getPosition().y < surface.y ? -1 : 1
                                    : getPosition().y > surface.y ? -1 : 1, 1);
                    toReturn = getPosition().add(direction.scale((double)NestManager.getRandomCorridorLength(random) / 2));
                    Vec3 surfaceFromReturn = findSurface(toReturn);
                    if (toReturn.y > surfaceFromReturn.y) {
                        toReturn = toReturn.multiply(1, 0, 1).add(0, surfaceFromReturn.y, 0);
                    }
                    cycleCounter++;
                }
                while (IsThisEndPositionInvalid(toReturn) && cycleCounter < 10);
                if (toReturn.y >= surface.y) {
                    DeadEnd = true;
                    addToQueuedGhostPosition(GenerateSurfaceGhost(toReturn));
                }
            }
            else{
                do{
                    Vec3 direction = EntomoDataManager.getDirectionToAFromB(getPosition(), getComparePosition())
                            .yRot(random.nextIntBetweenInclusive(-25, 25))
                            .xRot(random.nextIntBetweenInclusive(0, 25))
                            .zRot(random.nextIntBetweenInclusive(0, 25))
                            .normalize();
                    direction = direction.multiply(1,
                            getPosition().y > NestManager.getNestYBuildPriority() ? direction.y > 0 ? -1 : 1 :
                                    direction.y < 0 ? -1 : 1, 1);
                    toReturn = getPosition().add(direction.scale(NestManager.getRandomCorridorLength(random)));
                    cycleCounter++;
                }
                while (IsThisEndPositionInvalid(toReturn) && cycleCounter < 10);
            }
            if (IsThisEndPositionInvalid(toReturn)){
                this.Kill(false);
            }
            NestSaveData.Dirty();
            return end = toReturn;
        }
        private boolean IsThisEndPositionInvalid(Vec3 toTest){
            Vec3 start = getStartDirect();
            Vec3 direction = EntomoDataManager.getDirectionFromAToB(start, toTest);
            int thicknessScale = 0;
            if (parent instanceof Chamber c){
                thicknessScale = c.radius + c.thickness;
            }
            if (parent instanceof Corridor c){
                thicknessScale = c.weight + c.thickness;
            }
            if (!isEntrance()){
                start = start.add(direction.scale(thicknessScale));
                int distance = (int)start.distanceTo(toTest);
                for (int i = 0; i < distance; i++){
                    Vec3 buildPos = i == 0 ? start : start.add(direction.scale(i));
                    BlockPos bPos = new BlockPos((int)buildPos.x - 1, (int)buildPos.y - 1, (int)buildPos.z - 1);
                    BlockState bState = server.getBlockState(bPos);
                    for (BlockState placementBlocks : NestManager.getNestBlocks()){
                        if (bState.getBlock().defaultBlockState() == placementBlocks){
                            return true;
                        }
                    }
                }
            }


            int checkSize = isEntrance() ? NestManager.getNestLargeCorridorMaxRadius() : NestManager.getNestLargeChamberMaxRadius();
            for (int x = -checkSize / 2; x < checkSize / 2; x++){
                for (int y = -checkSize / 2; y < checkSize / 2; y++){
                    for (int z = -checkSize / 2; z < checkSize / 2; z++){
                        BlockState bState = server.getBlockState(new BlockPos((int)toTest.x + x, (int)toTest.y + y, (int)toTest.z + z));
                        for (BlockState placementBlocks : NestManager.getNestBlocks()){
                            if (bState.getBlock().defaultBlockState() == placementBlocks){
                                return true;
                            }
                        }
                    }
                }
            }
            Vec3 pos = getPosition();
            Vec3 midpoint = new Vec3((pos.x + toTest.x) / 2, (pos.y + toTest.y) / 2, (pos.y + toTest.y) / 2);
            if (parent == null) return false;
            return midpoint.distanceTo(parent.getPosition()) < pos.distanceTo(parent.getPosition());
        }

        private Vec3 getComparePosition(){
            if (getGenerator() == null || !getGenerator().isOfState(WorldShapeManager.GeneratorStates.done)){
                if (parent instanceof Corridor corridor && corridor.getGenerator() instanceof TunnelGenerator tunnel){
                    return tunnel.getStart();
                }
                return parent.getPosition();
            }
            else{
                TunnelGenerator tunnel = (TunnelGenerator)getGenerator();
                return tunnel.getStart();
            }
        }

        @Override
        public Vec3 getPosition() {
            if (getGenerator() == null || !getGenerator().isOfState(WorldShapeManager.GeneratorStates.done)){
                return super.getPosition();
            }
            else{
                TunnelGenerator tunnel = (TunnelGenerator)getGenerator();
                return tunnel.getEnd();
            }
        }

        public Vec3 getStartDirect(){
            return position;
        }
        @Override
        protected Vec3 getOffshootPosition() {
            return getPosition().add(EntomoDataManager.getDirectionToAFromB(getPosition(), parent.getPosition()).scale((double)weight / 2));
        }

        @Override
        public void OffshootTick(boolean tickChildren, boolean continuous, int layers) {
            super.OffshootTick(tickChildren, continuous, layers);
            if (generator.isOfState(WorldShapeManager.GeneratorStates.done) && getOffshootState() == 2){
                ManageExtension();
            }
        }

        private void ManageExtension() {
            if (shouldThisBecomeAParent()){
                if (ShouldGetExtension()) {
                    this.AddToChildren(new Corridor(server, this, end, weight, thickness, isEntrance()));
                }
                else{
                    ConstructNewChild((byte)1);
                }
            }
        }

        private Vec3 findSurface(Vec3 pos){
            int YTracker = (int)pos.y;
            BlockPos.MutableBlockPos mBPos = new BlockPos.MutableBlockPos(pos.x, pos.y, pos.z);
            do{
                mBPos.move(0, 1, 0);
                YTracker++;
            } while ((!CheckNearbyBlocksForSky(mBPos) && YTracker < server.getMaxBuildHeight()) || !server.getBlockState(mBPos).getFluidState().isEmpty());
            while (!((server.getBlockState(mBPos).isSolidRender(server, mBPos) || !server.getBlockState(mBPos).getFluidState().isEmpty())
                    || NestManager.getNestBlocks().contains(server.getBlockState(mBPos).getBlock().defaultBlockState()))
                    && YTracker > server.getMinBuildHeight()){
                mBPos.move(0, -1, 0);
                YTracker--;
            }
            return mBPos.getCenter();
        }
        private boolean CheckNearbyBlocksForSky(BlockPos start){
            if (checkForSkyTransparentSensitive(start)) return true;
            for (int y = -weight / 2; y < weight / 2; y++){
                BlockPos bPos = new BlockPos(start.getX(), start.getY() + y, start.getZ());
                if (checkForSkyTransparentSensitive(bPos)) return true;
            }
            return false;
        }
        private boolean checkForSkyTransparentSensitive(BlockPos bPos){
            return server.getBrightness(LightLayer.SKY, bPos) > 0;
        }
        private GhostSphere GenerateSurfaceGhost(Vec3 pos) {
            return new GhostSphere(pos, weight * 2);
        }

        /*Corridors do NOT support features (as of now)*/
        @Override
        public int getMaxAllowedFeatureCount() {return 0;}
        @Override
        public double getFeaturePlaceChance() {return 0;}
        @Override
        public boolean canSupportFeatures() {return false;}
        @Override
        protected Vec3 generateFeaturePlacementPosition(byte placementPos) {return null;}
        @Override
        protected Pair<Vec3, Direction> generateFeaturePlacementPositionForWall(byte placementPos) {return null;}
    }
}
