package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import static mod.pilot.entomophobia.data.EntomoDataManager.isThisGlass;

public class BreakBlocksInMyWayGoal extends Goal {
    final MyiaticBase parent;
    public BreakBlocksInMyWayGoal(MyiaticBase parent){
        this.parent = parent;
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
    @Override
    public boolean canUse() {
        return parent.getTarget() != null;
    }
    @Override
    public void tick() {
        if (parent.tickCount % 40 == 0){
            BreakBlocksInMyWay();
        }
    }

    public void BreakBlocksInMyWay(){
        AABB breakBox = parent.getBoundingBox().inflate(1.2);
        Level level = parent.level();
        for (BlockPos pos : BlockPos.betweenClosed((int)breakBox.minX, (int)breakBox.minY, (int)breakBox.minZ, (int)breakBox.maxX, (int)breakBox.maxY, (int)breakBox.maxZ)){
            BlockState state = level.getBlockState(pos);
            if (state.is(BlockTags.LEAVES) || isThisGlass(state) || state.getBlock() instanceof BambooStalkBlock){
                level.removeBlock(pos, false);
                level.levelEvent(2001, pos, Block.getId(level.getBlockState(pos)));
                level.playSound(parent, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5f, 1.25f);
            }
        }
    }
}
