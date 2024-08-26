package mod.pilot.entomophobia.data.WorldShapes;

import mod.pilot.entomophobia.data.WorldShapes.EntomoWorldShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ShapeGenerator{
    protected ShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly){
        this.server = server;
        setPlacementDetail(replaceableOnly ? 0 : 3);
        BuildSpeed = buildSpeed;
        BuildingBlocks = blockTypes;
        Position = pos;
    }
    protected ShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness){
        this.server = server;
        setPlacementDetail(1);
        BuildSpeed = buildSpeed;
        BuildingBlocks = blockTypes;
        Position = pos;
        MaxHardness = maxHardness;
    }
    protected ShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist){
        this.server = server;
        setPlacementDetail(2);
        BuildSpeed = buildSpeed;
        BuildingBlocks = blockTypes;
        Position = pos;
        ReplaceWhitelist = whitelist;
        ReplaceBlacklist = blacklist;
    }

    private int GeneratorState;
    public int getState(){
        return GeneratorState;
    }
    public void setState(int state){
        GeneratorState = state;
    }
    public void setState(EntomoWorldShapeManager.GeneratorStates state){
        GeneratorState = state.ordinal();
    }
    public boolean isOfState(EntomoWorldShapeManager.GeneratorStates state){
        return GeneratorState == state.ordinal();
    }

    private int PlacementDetail;
    public int getPlacementDetail(){
        return PlacementDetail;
    }
    public void setPlacementDetail(int detail){
        PlacementDetail = detail;
    }
    public void setPlacementDetail(EntomoWorldShapeManager.PlacementDetails detail){
        PlacementDetail = detail.ordinal();
    }
    public boolean isOfPlacementDetail(EntomoWorldShapeManager.PlacementDetails detail){
        return PlacementDetail == detail.ordinal();
    }

    int MaxHardness;

    @Nullable
    List<BlockState> ReplaceWhitelist;
    @Nullable
    List<BlockState> ReplaceBlacklist;
    public boolean CanThisBeReplaced(BlockState state, BlockPos pos){
        switch (getPlacementDetail()){
            case 0 ->{
                return state.canBeReplaced();
            }
            case 1 ->{
                return MaxHardness <= state.getDestroySpeed(server, pos);
            }
            case 2 ->{
                if (ReplaceWhitelist == null){
                    if (ReplaceBlacklist != null){
                        return !ReplaceBlacklist.contains(state);
                    }
                    return false;
                }
                else{
                    return ReplaceWhitelist.contains(state) && (ReplaceBlacklist == null || !ReplaceBlacklist.contains(state));
                }
            }
            case 3 ->{
                for (BlockState buildBlocks : BuildingBlocks){
                    if (state == buildBlocks){
                        continue;
                    }
                    return true;
                }
            }
            default -> {
                return false;
            }
        }
        return false;
    }

    private double BuildSpeed;
    public double getBuildSpeed(){
        return BuildSpeed;
    }
    public void setBuildSpeed(double newSpeed){
        BuildSpeed = newSpeed;
    }

    public List<BlockState> BuildingBlocks;

    private final Vec3 Position;
    public Vec3 getPosition(){
        return Position;
    }

    protected ServerLevel server;

    protected BlockPos lastBlock;
    protected SoundEvent getLastBlockPlaceSFX(){
        if (lastBlock != null){
            return server.getBlockState(lastBlock).getSoundType().getPlaceSound();
        }
        return null;
    }

    public void Disable(){
        setState(EntomoWorldShapeManager.GeneratorStates.disabled);
    }
    public void Enable(){
        if (!isOfState(EntomoWorldShapeManager.GeneratorStates.done)){
            setState(EntomoWorldShapeManager.GeneratorStates.active);
        }
    }
    public void Finish(){
        setState(EntomoWorldShapeManager.GeneratorStates.done);
    }

    public abstract boolean Build();

    protected boolean ReplaceBlock(BlockPos pos, BlockState state, int flag){
        boolean placed = server.setBlock(pos, state, flag);
        if (placed){
            lastBlock = pos;
            SoundEvent placeSound = getLastBlockPlaceSFX();
            if (placeSound != null){
                server.playSound(null, pos, placeSound, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
        return placed;

    }
    protected boolean ReplaceBlock(BlockPos pos, BlockState state){
        return ReplaceBlock(pos, state, 3);
    }
    protected boolean ReplaceBlock(BlockPos pos, int index){
        return ReplaceBlock(pos, BuildingBlocks.get(index), 3);
    }
    protected boolean ReplaceBlock(BlockPos pos){
        return ReplaceBlock(pos, BuildingBlocks.get(server.random.nextInt(0, BuildingBlocks.size())), 3);
    }
}
