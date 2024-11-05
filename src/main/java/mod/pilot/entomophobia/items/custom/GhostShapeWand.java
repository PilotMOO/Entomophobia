package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.PolyForged.GhostShapes.GhostQuadrilateral;
import mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common.Ghost3D;
import mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common.GhostManager;
import mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common.GhostShape;
import mod.pilot.entomophobia.systems.PolyForged.WorldShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GhostShapeWand extends Item {
    public GhostShapeWand(Properties pProperties) {
        super(pProperties);
    }

    private GhostShape<?> currentGhost;

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player.isSecondaryUseActive()){
            if (player.level() instanceof ServerLevel server){
                CreateBlocks(currentGhost, Blocks.AIR.defaultBlockState(), server);
                System.out.println("Replacing with air");
            }
            currentGhost = currentGhost instanceof GhostQuadrilateral g3 ? g3.rotate(WorldShapeManager.SignedAxis.PosX) : currentGhost;
        }
        else{
            currentGhost = GhostQuadrilateral.Generate(3, 4, 5, context.getClickLocation());
        }

        context.getPlayer().getCooldowns().addCooldown(this, 15);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel instanceof ServerLevel s){
            boolean air = pEntity.tickCount % 2 == 0;
            CreateBlocks(currentGhost, air ? Blocks.AIR.defaultBlockState() : Blocks.GOLD_BLOCK.defaultBlockState(), s);
        }
    }

    private void CreateBlocks(GhostShape<?> lastGhost, BlockState blockState, ServerLevel server) {
        if (lastGhost == null) {
            return;
        }
        for (BlockPos bPos : GhostManager.TranslateToPositions(lastGhost.getGhosts())){
            server.setBlock(bPos, blockState, 3);
        }
    }
}
