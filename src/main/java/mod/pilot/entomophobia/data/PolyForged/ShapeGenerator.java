package mod.pilot.entomophobia.data.PolyForged;

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
    public void setState(WorldShapeManager.GeneratorStates state){
        GeneratorState = state.ordinal();
    }
    public boolean isOfState(WorldShapeManager.GeneratorStates state){
        return GeneratorState == state.ordinal();
    }
    public boolean isActive(){
        return isOfState(WorldShapeManager.GeneratorStates.active);
    }

    private int PlacementDetail;
    public int getPlacementDetail(){
        return PlacementDetail;
    }
    public void setPlacementDetail(int detail){
        PlacementDetail = detail;
    }
    public void setPlacementDetail(WorldShapeManager.PlacementDetails detail){
        PlacementDetail = detail.ordinal();
    }
    public boolean isOfPlacementDetail(WorldShapeManager.PlacementDetails detail){
        return PlacementDetail == detail.ordinal();
    }

    public int MaxHardness;

    @Nullable
    public List<BlockState> ReplaceWhitelist;
    @Nullable
    public List<BlockState> ReplaceBlacklist;

    public boolean CanThisBeReplaced(BlockState state, BlockPos pos){
        switch (getPlacementDetail()){
            case 0 ->{
                return state.canBeReplaced() && isNotInList(state.getBlock().defaultBlockState());
            }
            case 1 ->{
                return ((MaxHardness >= state.getDestroySpeed(server, pos) && isNotInList(state.getBlock().defaultBlockState())) || state.canBeReplaced()) && state.getDestroySpeed(server, pos) != -1;
            }
            case 2 ->{
                if (ReplaceWhitelist == null){
                    if (ReplaceBlacklist != null){
                        return ((!ReplaceBlacklist.contains(state.getBlock().defaultBlockState()) && isNotInList(state.getBlock().defaultBlockState())) || state.canBeReplaced()) && state.getDestroySpeed(server, pos) != -1;
                    }
                    return false;
                }
                else{
                    return ((ReplaceWhitelist.contains(state.getBlock().defaultBlockState()) && (ReplaceBlacklist == null || !ReplaceBlacklist.contains(state.getBlock().defaultBlockState()))) && isNotInList(state.getBlock().defaultBlockState())) || state.canBeReplaced() && state.getDestroySpeed(server, pos) != -1;
                }
            }
            case 3 ->{
                return (isNotInList(state.getBlock().defaultBlockState()) || state.canBeReplaced()) && state.getDestroySpeed(server, pos) != -1;
            }
            default -> {
                return false;
            }
        }
    }
    protected final boolean isNotInList(BlockState state){
        boolean flag = true;
        for (BlockState buildBlocks : BuildingBlocks){
            if (state == buildBlocks) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private double BuildSpeed;
    public double getBuildSpeed(){
        return BuildSpeed;
    }
    public void setBuildSpeed(double newSpeed){
        BuildSpeed = newSpeed;
    }

    protected int ActiveTime;
    public int getActiveTime(){
        return ActiveTime;
    }
    protected void setActiveTime(int count){
        ActiveTime = count;
    }
    protected void ActiveTimeTick(){
        setActiveTime(getActiveTime() + 1);
    }

    public final List<BlockState> BuildingBlocks;

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
        setState(WorldShapeManager.GeneratorStates.disabled);
    }
    public void Enable(){
        if (!isOfState(WorldShapeManager.GeneratorStates.done)){
            setState(WorldShapeManager.GeneratorStates.active);
        }
    }
    public void Finish(){
        setState(WorldShapeManager.GeneratorStates.done);
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
        if (BuildingBlocks.size() > index){
            return ReplaceBlock(pos, BuildingBlocks.get(index), 3);
        }
        return false;
    }
    protected boolean ReplaceBlock(BlockPos pos){
        if (BuildingBlocks.size() > 0){
            return ReplaceBlock(pos, BuildingBlocks.get(server.random.nextInt(0, BuildingBlocks.size())), 3);
        }
        return false;
    }
}
