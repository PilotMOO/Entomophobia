package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPacketTestWand extends Item {
    private static final GeneratorBlockPacket GBP = new GeneratorBlockPacket(10);
    public BlockPacketTestWand(Properties pProperties) {
        super(pProperties);

    }

    static{
        AddShitToBlockPacket();
    }

    private static void AddShitToBlockPacket() {
        GBP.add(Blocks.BAMBOO_BLOCK.defaultBlockState());
        GBP.add(Blocks.GOLD_BLOCK.defaultBlockState(), 1);
        GBP.add(Blocks.COAL_BLOCK.defaultBlockState(), 50);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!(pContext.getLevel() instanceof ServerLevel server)) return InteractionResult.SUCCESS;

        BlockState generated = GBP.getRandomState();
        if (generated == null){
            server.explode(null, null, null,
                    pContext.getClickLocation(), 5, false, Level.ExplosionInteraction.MOB);
            return InteractionResult.SUCCESS;
        }
        server.setBlock(pContext.getClickedPos(), generated, 3);
        return InteractionResult.SUCCESS;
    }
}
