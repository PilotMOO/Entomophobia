package mod.pilot.entomophobia.systems.nest;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.data.WorldSaveData;
import mod.pilot.entomophobia.systems.PolyForged.ShapeGenerator;
import mod.pilot.entomophobia.systems.PolyForged.Shapes.ChamberGenerator;
import mod.pilot.entomophobia.systems.PolyForged.Shapes.HollowSphereGenerator;
import mod.pilot.entomophobia.systems.PolyForged.Shapes.TunnelGenerator;
import mod.pilot.entomophobia.systems.PolyForged.WorldShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class Nest {
    public Nest(ServerLevel server, Vec3 start, int tickFrequency){
        this.server = server;
        origin = start;
        TickFrequency = tickFrequency;
        MainChamber = CreateMainChamber();
        Enable();
    }
    private Nest(ServerLevel server, Vec3 start, int tickFrequency, Chamber mainChamber){
        this.server = server;
        origin = start;
        TickFrequency = tickFrequency;
        MainChamber = mainChamber;
        Enable();
    }
    public static Nest ConstructFromBlueprint(ServerLevel server, Vec3 start, int tickFrequency, Chamber mainChamber){
        return new Nest(server, start, tickFrequency, mainChamber);
    }

    public ServerLevel server;
    public static final RandomSource random = RandomSource.create();

    public final int TickFrequency;

    public final Vec3 origin;

    private byte NestState;
    public byte getNestState(){
        return NestState;
    }
    protected void setNestState(byte state){
        NestState = state;
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
        return MainChamber.Dead() || getNestState() == 3 || getNestState() == 0;
    }

    public ArrayList<Offshoot> Offshoots(){
        return MainChamber.children;
    }
    public final Chamber MainChamber;
    private Chamber CreateMainChamber(){
        return new Chamber(server, null, origin,
                Config.SERVER.large_chamber_max_size.get(), Config.SERVER.large_chamber_thickness.get());
    }

    public Offshoot CreateNewOffshootFrom(Offshoot parent, byte newShootType){
        return parent.ConstructNewChild(newShootType);
    }
    public Offshoot CreateNewOffshootFromMain(byte newShootType){
        return CreateNewOffshootFrom(MainChamber, newShootType);
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
            setOffshootType(type);
            Enable();
            this.server = server;
            this.parent = parent;
            this.position = position;
            if (this instanceof Corridor){
                DeadEnd = !ShouldThisBecomeAParent();
            }
        }

        protected ServerLevel server;
        protected final Vec3 position;

        public boolean DeadEnd;
        public int MaxChildCount = 0;
        public int LayersDeep(){
            int layers = 0;
            Offshoot currentParent = this;
            while (currentParent.parent != null){
                layers = currentParent instanceof Corridor ? layers : layers + 1;
                currentParent = currentParent.parent;
            }
            return layers;
        }

        public boolean ShouldThisBecomeAParent(){
            if (DeadEnd) return false;

            if (this instanceof Corridor) return children == null;

            if (parent == null){
                return (children == null || children.size() < MaxChildCount) && !AreAnyOfMyChildrenAlive();
            }

            if (children == null) return true;

            else if (random.nextIntBetweenInclusive(0, 10) / 10 < 1){
                if (LayersDeep() > NestManager.getNestMaxLayers()){
                    return false;
                }
                else{
                    return children.size() < MaxChildCount;
                }
            }
            return false;
        }

        public Vec3 getPosition(){
            return position;
        }

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
        }
        public void Disable(){
            setOffshootState((byte)0);
        }
        public void Enable(){
            setOffshootState((byte)1);
        }
        public void Finish(){
            setOffshootState((byte)2);
        }
        private byte OffshootType;
        public byte getOffshootType(){
            return OffshootType;
        }
        protected void setOffshootType(byte type){
            OffshootType = type;
        }

        public boolean Alive(){
            return generator.isActive() && getOffshootState() == 1;
        }
        public boolean Dead(){
            return getOffshootState() == 0 || generator.isOfState(WorldShapeManager.GeneratorStates.disabled);
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
        }

        @Nullable
        public final Offshoot parent;
        @Nullable
        public ArrayList<Offshoot> children;
        public void AddToChildren(Offshoot child){
            if (children == null){
                children = new ArrayList<>();
            }
            if (child != null){
                children.add(child);
            }
        }
        public boolean AreAnyOfMyChildrenAlive(){
            if (children == null) return false;
            for (Offshoot child : children){
                if (child.Alive()) return true;
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
        public final void TickGenerator(){
            generator.Build();
        }
        protected boolean ShouldGeneratorTick(){
            return OffshootState == 1 && generator != null && generator.isOfState(WorldShapeManager.GeneratorStates.active);
        }
        public void OffshootTick(boolean tickChildren, boolean continuative, int layers){
            if (ShouldGeneratorTick()){
                TickGenerator();
            }
            if (tickChildren && children != null){
                for (Offshoot child : children) {
                    child.OffshootTick(continuative, layers != 0, layers - 1);
                }
            }
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
                                Config.SERVER.small_chamber_thickness.get());
                        case 2 -> child = new Chamber(server, this, OffshootPos, NestManager.getRandomMediumChamberRadius(random),
                                Config.SERVER.medium_chamber_thickness.get());
                        case 3 -> child = new Chamber(server, this, OffshootPos, NestManager.getRandomLargeChamberRadius(random),
                                Config.SERVER.large_chamber_thickness.get());
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
            ConstructGenerator(server, getPosition(), radius, thickness);
            super.MaxChildCount = 3;
            this.radius = radius;
            this.thickness = thickness;
        }
        protected void ConstructGenerator(ServerLevel server, Vec3 pos, int radius, int thickness) {
            ChamberGenerator generator = WorldShapeManager.CreateChamber(server, NestManager.getNestBuildSpeed(), NestManager.getNestBlocks(), pos, NestManager.getNestMaxHardness(), radius, thickness, 0.5, true);
            setGenerator(generator);
            if (parent != null && parent instanceof Corridor corridor && corridor.getGenerator() instanceof TunnelGenerator tunnel){
                for (ArrayList<BlockPos> ghost : tunnel.getGhostLineSpheres((radius + thickness) * 2, true)){
                    generator.AddToGhostSpheres(ghost);
                }
            }
        }
        public static Chamber ConstructFromPackage(WorldSaveData.NestPackager.PackagedChamber packaged, @Nullable Offshoot parent){
            return new Chamber(packaged.getServer(), parent, new Vec3(packaged.X, packaged.Y, packaged.Z), packaged.radius, packaged.thickness);
        }

        public final int radius;
        public final int thickness;

        @Override
        public void OffshootTick(boolean tickChildren, boolean continuative, int layers) {
            if (ShouldGeneratorTick()){
                TickGenerator();
            }
            if (tickChildren && children != null){
                for (Offshoot child : children) {
                    child.OffshootTick(continuative, layers != 0, layers - 1);
                }
            }
            if (getGenerator() != null && getGenerator().isOfState(WorldShapeManager.GeneratorStates.done) && !AreAnyOfMyChildrenAlive()){
                if (ShouldThisBecomeAParent()){
                    System.out.println("We gonna have a child!");
                    ConstructNewChild((byte)2);
                }
                else{
                    System.out.println("No child :[");
                }
            }
        }

        @Override
        protected Vec3 getOffshootPosition() {
            int Origin = getPosition().y > NestManager.getNestYBuildPriority() ? -25 : 0;
            int Bound = getPosition().y > NestManager.getNestYBuildPriority() ? 0 : 25;
            HollowSphereGenerator sphereGenerator = (HollowSphereGenerator)generator;
            Vec3 direction;
            Vec3 toReturn = null;
            int cycleCounter = 0;
            while ((toReturn == null || !TestOffshootPosition(toReturn)) && cycleCounter < 10){
                direction = getPosition().normalize().yRot(random.nextInt(Origin, Bound)).xRot(random.nextInt(-180, 180)).zRot(random.nextInt(-180, 180));
                toReturn = getPosition().add(direction.scale(sphereGenerator.radius - sphereGenerator.thickness));
                cycleCounter++;
            }
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
    }
    public static class Corridor extends Offshoot{
        private static final byte OffshootType = 2;
        public Corridor(ServerLevel server, @Nonnull Nest.Offshoot parent, Vec3 position, int weight, int thickness) {
            super(server, OffshootType, parent, position);
            ConstructGenerator(weight, thickness);
            super.MaxChildCount = 1;
            this.weight = weight;
            this.thickness = thickness;
        }
        private Corridor(ServerLevel server, @Nonnull Nest.Offshoot parent, Vec3 position, int weight, int thickness, Vec3 endPos){
            super(server, OffshootType, parent, position);
            super.MaxChildCount = 1;
            this.weight = weight;
            this.thickness = thickness;
            ConstructGeneratorFromBlueprint(weight, thickness, endPos, parent);
        }
        protected void ConstructGenerator(int weight, int thickness){
            TunnelGenerator tunnel = WorldShapeManager.CreateTunnel(server, NestManager.getNestBuildSpeed(), NestManager.getNestBlocks(), NestManager.getNestMaxHardness(), getPosition(), GenerateEndPosition(), weight, thickness);
            if (parent.getGenerator() instanceof ChamberGenerator chamber){
                tunnel.AddToGhostSpheres(chamber.GenerateInternalGhostSphere());
                if (parent.parent instanceof Corridor parentCorridor && parentCorridor.getGenerator() instanceof TunnelGenerator parentTunnel){
                    for (ArrayList<BlockPos> ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                        tunnel.AddToGhostSpheres(ghost);
                    }
                }
            }
            else if (parent instanceof Corridor corridor && corridor.getGenerator() instanceof TunnelGenerator parentTunnel){
                for (ArrayList<BlockPos> ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                    tunnel.AddToGhostSpheres(ghost);
                }
            }
            setGenerator(tunnel);
        }
        private void ConstructGeneratorFromBlueprint(int weight, int thickness, Vec3 end, @Nonnull Offshoot parent){
            TunnelGenerator tunnel = WorldShapeManager.CreateTunnel(server, NestManager.getNestBuildSpeed(), NestManager.getNestBlocks(), NestManager.getNestMaxHardness(), getPosition(), end, weight, thickness);
            if (parent.getGenerator() instanceof ChamberGenerator chamber){
                tunnel.AddToGhostSpheres(chamber.GenerateInternalGhostSphere());
                if (parent.parent instanceof Corridor parentCorridor && parentCorridor.getGenerator() instanceof TunnelGenerator parentTunnel){
                    for (ArrayList<BlockPos> ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                        tunnel.AddToGhostSpheres(ghost);
                    }
                }
            }
            else if (parent instanceof Corridor corridor && corridor.getGenerator() instanceof TunnelGenerator parentTunnel){
                for (ArrayList<BlockPos> ghost : parentTunnel.getGhostLineSpheres(weight, true)){
                    tunnel.AddToGhostSpheres(ghost);
                }
            }
            setGenerator(tunnel);
        }
        public static Corridor ConstructFromPackage(WorldSaveData.NestPackager.PackagedCorridor packaged, Offshoot parent){
            return new Corridor(packaged.getServer(), parent, new Vec3(packaged.X, packaged.Y, packaged.Z), packaged.weight, packaged.thickness, new Vec3(packaged.X2, packaged.Y2, packaged.Z2));
        }

        public final int weight;
        public final int thickness;

        public static final int maxCorridorExtensionCount = Config.SERVER.max_corridor_extension.get();
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
            return (maxCorridorExtensionCount > getAmountOfExtensions() && (double)random.nextIntBetweenInclusive(0, 10) / 10 < NestManager.getNestCorridorExtensionChance() && children == null);
        }

        public Vec3 end;
        private Vec3 GenerateEndPosition(){
            Vec3 toReturn = null;

            int cycleCounter = 0;
            while ((toReturn == null || IsThisEndPositionInvalid(toReturn)) && cycleCounter < 10){
                toReturn = getPosition().add(EntomoDataManager.GetDirectionToAFromB(getPosition(), getComparePosition())
                        .yRot(random.nextIntBetweenInclusive(-25, 25))
                        .xRot(random.nextIntBetweenInclusive(-25, 25))
                        .zRot(random.nextIntBetweenInclusive(-25, 25))
                        .scale(NestManager.getRandomCorridorLength(random)))
                        .multiply(0, position.y > NestManager.getNestYBuildPriority() ? -1 : 1, 0);
                cycleCounter++;
            }
            if (IsThisEndPositionInvalid(toReturn)){
                this.Kill(false);
            }
            return end = toReturn;
        }
        private boolean IsThisEndPositionInvalid(Vec3 toTest){
            boolean flag = false;
            for (int x = -weight; x < weight; x++){
                for (int y = -weight; y < weight; y++){
                    for (int z = -weight; z < weight; z++){
                        BlockState bState = server.getBlockState(new BlockPos((int)toTest.x + x, (int)toTest.y + y, (int)toTest.z + z));
                        for (BlockState placementBlocks : NestManager.getNestBlocks()){
                            if (bState.getBlock().defaultBlockState() == placementBlocks){
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) break;
                }
                if (flag) break;
            }
            Vec3 pos = getPosition();
            Vec3 midpoint = new Vec3((pos.x + toTest.x) / 2, (pos.y + toTest.y) / 2, (pos.y + toTest.y) / 2);
            if (midpoint.distanceTo(parent.getPosition()) < pos.distanceTo(parent.getPosition())){
                flag = true;
            }
            return flag;
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
            return super.getPosition();
        }
        @Override
        protected Vec3 getOffshootPosition() {
            return getPosition().add(EntomoDataManager.GetDirectionToAFromB(getPosition(), parent.getPosition()).scale((double)weight / 2));
        }

        @Override
        public void OffshootTick(boolean tickChildren, boolean continuative, int layers) {
            if (ShouldGeneratorTick()){
                generator.Build();
            }
            else if (generator.isOfState(WorldShapeManager.GeneratorStates.done)){
                ManageExtension();
            }
            if (tickChildren && children != null){
                for (Offshoot child : children) {
                    child.OffshootTick(continuative, layers != 0, layers - 1);
                }
            }
        }

        private void ManageExtension() {
            if (ShouldThisBecomeAParent()){
                if (ShouldGetExtension()) {
                    this.AddToChildren(new Corridor(server, this, end, weight, thickness));
                }
                else{
                    ConstructNewChild((byte)1);
                }
            }
        }
    }
}
