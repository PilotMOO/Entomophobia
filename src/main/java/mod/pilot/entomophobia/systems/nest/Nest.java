package mod.pilot.entomophobia.systems.nest;

import com.mojang.datafixers.util.Pair;
import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.data.PolyForged.ShapeGenerator;
import mod.pilot.entomophobia.data.PolyForged.Shapes.HollowSphereGenerator;
import mod.pilot.entomophobia.data.PolyForged.Shapes.HollowWeightedCircleLineGenerator;
import mod.pilot.entomophobia.data.PolyForged.WorldShapeManager;
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
        System.out.println("testin' if this nest is active: " + (MainChamber.Alive() && getNestState() == 1));
        return MainChamber.Alive() && getNestState() == 1;
    }

    public ArrayList<Offshoot> Offshoots(){
        return MainChamber.children;
    }
    public final Chamber MainChamber;
    private Chamber CreateMainChamber(){
        return new Chamber(server, null, Config.SERVER.nest_build_speed.get(), origin, Config.SERVER.nest_max_hardness.get(),
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
        if (MainChamber.Alive()){
            MainChamber.OffshootTick(true, true, -1);
        }
        else{
            System.out.println("Main Chamber is dead!");
        }

        if (NestManager.getNestBlocks().size() == 0){
            System.out.println("Shit's fucked");
        }
        else{
            System.out.println("Amount of blocks we can build with: " + NestManager.getNestBlocks().size());
        }
    }

    public abstract static class Offshoot{
        protected Offshoot(ServerLevel server, byte type, @Nullable Offshoot parent, Vec3 position){
            setOffshootType(type);
            Enable();
            this.server = server;
            this.parent = parent;
            this.position = position;
            DeadEnd = ShouldThisBecomeAParent();
        }

        protected ServerLevel server;
        protected final Vec3 position;

        public boolean DeadEnd = false;
        public int MaxChildCount = 0;
        public int LayersDeep(){
            int layers = 0;
            Offshoot currentParent = this;
            while (currentParent.parent != null){
                layers++;
                currentParent = currentParent.parent;
            }
            return layers;
        }

        public boolean ShouldThisBecomeAParent(){
            return (!DeadEnd && LayersDeep() <= NestManager.getNestMaxLayers()) || this instanceof Corridor;
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
            chamber_small,
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
        public void Kill(boolean continuous) {
            setOffshootState((byte)0);
            generator.Disable();
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
                if (tickChildren && children != null){
                    for (Offshoot child : children) {
                        child.OffshootTick(continuative, layers != 0, layers - 1);
                    }
                }
            }
        }

        public final Offshoot ConstructNewChild(byte newShootType){
            Offshoot child;
            switch (newShootType){
                default -> {
                    child = null;
                }
                case 1 -> {
                    switch (random.nextIntBetweenInclusive(1, 3)){
                        default -> {
                            child = new Chamber(server, this, NestManager.getNestBuildSpeed(), getOffshootPosition(), NestManager.getNestMaxHardness(),
                                    random.nextIntBetweenInclusive(Config.SERVER.small_chamber_min_size.get(), Config.SERVER.small_chamber_max_size.get()),
                                    Config.SERVER.small_chamber_thickness.get());
                        }
                        case 2 -> {
                            child = new Chamber(server, this, NestManager.getNestBuildSpeed(), getOffshootPosition(), NestManager.getNestMaxHardness(),
                                    random.nextIntBetweenInclusive(Config.SERVER.medium_chamber_min_size.get(), Config.SERVER.medium_chamber_max_size.get()),
                                    Config.SERVER.medium_chamber_thickness.get());
                        }
                        case 3 -> {
                            child = new Chamber(server, this, NestManager.getNestBuildSpeed(), getOffshootPosition(), NestManager.getNestMaxHardness(),
                                    random.nextIntBetweenInclusive(Config.SERVER.large_chamber_min_size.get(), Config.SERVER.large_chamber_max_size.get()),
                                    Config.SERVER.large_chamber_thickness.get());
                        }
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
        protected Chamber(ServerLevel server, @org.jetbrains.annotations.Nullable Nest.Offshoot parent, double buildSpeed, Vec3 pos, int maxHardness, int radius, int thickness) {
            super(server, OffshootType, parent, pos);
            ConstructGenerator(server, buildSpeed, NestManager.getNestBlocks(), getPosition(), maxHardness, radius, thickness);
            super.MaxChildCount = 3;
        }
        protected void ConstructGenerator(ServerLevel server, double buildSpeed, ArrayList<BlockState> nestBlocks, Vec3 pos, int maxHardness, int radius, int thickness) {
            setGenerator(WorldShapeManager.CreateRandomizedHollowSphere(server, buildSpeed, nestBlocks, pos, maxHardness, radius, thickness, 0.5, true));
        }

        @Override
        protected Vec3 getOffshootPosition() {
            Vec3 toReturn = getPosition();
            Vec3 direction = getPosition().normalize().yRot(random.nextInt(0, 360)).xRot(random.nextInt(0, 360)).zRot(random.nextInt(0, 360));

            HollowSphereGenerator sphereGenerator = (HollowSphereGenerator)generator;
            return toReturn.add(direction.scale(sphereGenerator.radius - sphereGenerator.thickness));
        }
    }
    public static class Corridor extends Offshoot{
        private static final byte OffshootType = 2;
        public Corridor(ServerLevel server, @Nonnull Nest.Offshoot parent, Vec3 position, int weight, int thickness) {
            super(server, OffshootType, parent, position);
        }
        protected void ConstructGenerator(int weight, int thickness){
            HollowWeightedCircleLineGenerator generator = WorldShapeManager.CreateHollowWeightedCircleLine(server, NestManager.getNestBuildSpeed(), NestManager.getNestBlocks(), NestManager.getNestMaxHardness(), getPosition(), GenerateEndPosition(), weight, thickness);
            setGenerator(generator);
            generators.add(generator);
        }

        public Vec3 end;
        private Vec3 GenerateEndPosition(){
            Vec3 toSet = getPosition();
            Vec3 directTo = EntomoDataManager.GetDirectionToAFromB(getPosition(), getComparePosition()).normalize()
                    .xRot(random.nextIntBetweenInclusive(-90, 90))
                    .yRot(random.nextIntBetweenInclusive(-90, 90))
                    .zRot(random.nextIntBetweenInclusive(-90, 90));
            return end = toSet.add(directTo.scale(NestManager.getRandomCorridorLength(random)));
        }
        private Vec3 getComparePosition(){
            if (!getGenerator().isOfState(WorldShapeManager.GeneratorStates.done)){
                return parent.getPosition();
            }
            else{
                HollowWeightedCircleLineGenerator HWCLG = (HollowWeightedCircleLineGenerator)getGenerator();
                return HWCLG.getStart();
            }
        }

        @Override
        public Vec3 getPosition() {
            if (!getGenerator().isOfState(WorldShapeManager.GeneratorStates.done)){
                return super.getPosition();
            }
            else{
                HollowWeightedCircleLineGenerator HWCLG = (HollowWeightedCircleLineGenerator)getGenerator();
                return HWCLG.getEnd();
            }
        }
        @Override
        protected Vec3 getOffshootPosition() {
            return end;
        }

        @Override
        public boolean Alive() {
            boolean generatorActiveFlag = false;
            for (ShapeGenerator generator : generators){
                generatorActiveFlag = generator.isActive();
                if (generatorActiveFlag) break;
            }

            return generatorActiveFlag && getOffshootState() == 1;
        }

        public ArrayList<ShapeGenerator> generators;
        @Override
        public ShapeGenerator getGenerator() {
            ShapeGenerator toReturn = null;
            for (int i = 0; i < generators.size(); i++){
                if (generators.get(i).isActive()){
                    toReturn = generators.get(i);
                    break;
                }
            }
            if (toReturn == null && generators.size() > 0){
                toReturn = generators.get(0);
            }
            return toReturn;
        }

        @Override
        public void OffshootTick(boolean tickChildren, boolean continuative, int layers) {
            for (ShapeGenerator generator : generators){
                if (ShouldGeneratorTick()){
                    generator.Build();
                }
            }
            if (tickChildren && children != null){
                for (Offshoot child : children) {
                    child.OffshootTick(continuative, layers != 0, layers - 1);
                }
            }
        }
        @Override
        protected boolean ShouldGeneratorTick() {
            boolean nullFlag = false;
            boolean activeFlag = false;
            for (ShapeGenerator generator : generators){
                nullFlag = generator != null || nullFlag;
                if (generator != null){
                    activeFlag = generator.isActive();
                }

                if (nullFlag && activeFlag){
                    break;
                }
            }
            return getOffshootState() == 1 && nullFlag && activeFlag;
        }
    }
}
