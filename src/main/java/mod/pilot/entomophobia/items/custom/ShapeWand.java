package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.WorldShapes.WorldShapeManager;
import mod.pilot.entomophobia.data.WorldShapes.ShapeGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ShapeWand extends Item {
    private static final int amountOfStates = states.values().length;
    enum states{
        cube,
        rectangle,
        square,
        sphere,
        hollow_sphere,
        square_slow,
        square_fast
    }
    int state = 0;

    public ShapeWand(Properties pProperties) {
        super(pProperties);
    }
    static List<ShapeGenerator> shapes = new ArrayList<>();
    public List<ShapeGenerator> shapeInstances(){
        return new ArrayList<>(shapes);
    }
    private static final List<BlockState> BlockStates = new ArrayList<>();

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (shapes != null){
            for (ShapeGenerator shape : shapeInstances()){
                if (pLevel instanceof ServerLevel){
                    shape.Build();
                }
                if (shape.isOfState(WorldShapeManager.GeneratorStates.done)){
                    if (pEntity instanceof Player player){
                        player.displayClientMessage(Component.translatable("entomophobia.item.nest_builder_finish"), true);
                    }
                    shapes.remove(shape);
                }
            }
        }
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand usedHand) {
        if (!player.isSecondaryUseActive()){
            SwitchState(player);
            player.getCooldowns().addCooldown(this, 5);
            return InteractionResultHolder.success(new ItemStack(this));
        }
        else{
            BlockStates.clear();
            player.displayClientMessage(Component.literal("Cleared list of Blockstates!"), true);
            player.playSound(SoundEvents.DISPENSER_DISPENSE);
        }
        return InteractionResultHolder.fail(new ItemStack(this));
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (BlockStates.size() > 0){
            if (player != null && context.getLevel() instanceof ServerLevel server){
                Vec3 GeneratorCenter = context.isSecondaryUseActive() ? context.getClickedPos().getCenter() : context.getClickedPos().relative(context.getClickedFace()).getCenter();
                if (state == states.cube.ordinal()){
                    shapes.add(WorldShapeManager.CreateCube(server, 2, BlockStates, GeneratorCenter, false, 5));
                }
                if (state == states.rectangle.ordinal()){
                    shapes.add(WorldShapeManager.CreateRectangle(server, 2, BlockStates, GeneratorCenter, false, 5, 2, 10));
                }
                if (state == states.square.ordinal()){
                    shapes.add(WorldShapeManager.CreateSquare(server, 2, BlockStates, GeneratorCenter, false, 10, WorldShapeManager.Axis.Y));
                }
                if (state == states.sphere.ordinal()) {
                    shapes.add(WorldShapeManager.CreateSphere(server, 2, BlockStates, GeneratorCenter, false, 3));
                }
                if (state == states.hollow_sphere.ordinal()) {
                    shapes.add(WorldShapeManager.CreateHollowSphere(server, 50, BlockStates, GeneratorCenter, true, 20, 1));
                }
                if (state == states.square_slow.ordinal()){
                    shapes.add(WorldShapeManager.CreateSquare(server, 0.5, BlockStates, GeneratorCenter, false, 10, WorldShapeManager.Axis.Y));
                }
                if (state == states.square_fast.ordinal()){
                    shapes.add(WorldShapeManager.CreateSquare(server, 5, BlockStates, GeneratorCenter, false, 10, WorldShapeManager.Axis.Y));
                }
                player.displayClientMessage(Component.literal("Generating a new " + states.values()[state].name()), true);
            }
            return InteractionResult.SUCCESS;
        }
        else{
            player.displayClientMessage(Component.literal("Can't create shape, list of blocks is empty!"), true);
            player.playSound(SoundEvents.ANVIL_LAND);
            return InteractionResult.FAIL;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        BlockState state = player.level().getBlockState(pos);
        if (!player.isSecondaryUseActive()){
            if (!BlockStates.contains(state)){
                BlockStates.add(state);
                player.displayClientMessage(Component.literal("Added " + state.getBlock() + " to list of BlockStates!"), true);
                player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 1.5f);
            }
        }
        else {
            if (BlockStates.contains(state)) {
                BlockStates.remove(state);
                player.displayClientMessage(Component.literal("Removed " + state.getBlock() + " from list of BlockStates!"), true);
                player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 0.5f);
            }
            else{
                player.displayClientMessage(Component.literal("Can't remove that block, it isn't in the list!"), true);
                player.playSound(SoundEvents.AMETHYST_CLUSTER_BREAK);
            }
        }

        if (player.level() instanceof ServerLevel server){
            server.setBlock(pos, state, 7);
        }
        return true;
    }

    private void SwitchState(Player player) {
        if (player.level() instanceof ServerLevel){
            if (state < amountOfStates - 1){
                state++;
            }
            else{
                state = 0;
            }
        }
        else{
            int StateGhost = state;
            if (StateGhost < amountOfStates - 1){
                StateGhost++;
            }
            else{
                StateGhost = 0;
            }
            player.displayClientMessage(Component.literal(states.values()[StateGhost].name()), true);
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
    }
}
