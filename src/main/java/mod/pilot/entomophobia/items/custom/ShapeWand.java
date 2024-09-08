package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.PolyForged.WorldShapeManager;
import mod.pilot.entomophobia.data.PolyForged.ShapeGenerator;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapeWand extends Item {
    private static final int amountOfStates = states.values().length;
    enum states{
        square,
        circle,
        cube,
        rectangle,
        sphere,
        hollow_sphere,
        line,
        weighted_square_line,
        weighted_circle_line,
        hollow_weighted_circle_line_small,
        hollow_weighted_circle_line_medium,
        hollow_weighted_circle_line_large,
        tunnel
    }
    int state = 0;

    public ShapeWand(Properties pProperties) {
        super(pProperties);
    }
    static List<ShapeGenerator> shapes = new ArrayList<>();
    public List<ShapeGenerator> shapeInstances(){
        return new ArrayList<>(shapes);
    }
    private final List<BlockState> BlockStates = new ArrayList<>();

private static final List<BlockState> whitelist = new ArrayList<>(Arrays.asList(Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState()));
    private static final List<BlockState> blacklist = new ArrayList<>(Arrays.asList(Blocks.COBBLESTONE.defaultBlockState(), Blocks.COARSE_DIRT.defaultBlockState()));

    private Vec3 LineVectorStart;

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
        if (player != null){
            if (BlockStates.size() > 0){
                if (context.getLevel() instanceof ServerLevel server){
                    Vec3 GeneratorCenter = context.isSecondaryUseActive() ? context.getClickedPos().getCenter() : context.getClickedPos().relative(context.getClickedFace()).getCenter();
                    switch (state){
                        case 0 -> shapes.add(WorldShapeManager.CreateSquare(server, 1, BlockStates, GeneratorCenter, false, 10, WorldShapeManager.Axis.Y));
                        case 1 -> shapes.add(WorldShapeManager.CreateCircle(server, 0.25, BlockStates, GeneratorCenter, false, 5, WorldShapeManager.Axis.Y));
                        case 2 -> shapes.add(WorldShapeManager.CreateCube(server, 1, BlockStates, GeneratorCenter, false, 5));
                        case 3 -> shapes.add(WorldShapeManager.CreateRectangle(server, 1, BlockStates, GeneratorCenter, false, 5, 2, 10));
                        case 4 -> shapes.add(WorldShapeManager.CreateSphere(server, 1, BlockStates, GeneratorCenter, false, 3));
                        case 5 -> shapes.add(WorldShapeManager.CreateHollowSphere(server, 50, BlockStates, GeneratorCenter, true, 20, 1, true));
                        case 6, 7, 8, 9, 10, 11, 12 -> CreateLine(context.getPlayer(), GeneratorCenter);
                    }
                    if (state != states.line.ordinal() && state != states.weighted_square_line.ordinal() && state != states.weighted_circle_line.ordinal() && state != states.hollow_weighted_circle_line_small.ordinal()  && state != states.hollow_weighted_circle_line_medium.ordinal() && state != states.hollow_weighted_circle_line_large.ordinal() && state != states.tunnel.ordinal()){
                        player.displayClientMessage(Component.literal("Generating a new " + states.values()[state].name()), true);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            else{
                player.displayClientMessage(Component.literal("Can't create shape, list of blocks is empty!"), true);
                player.playSound(SoundEvents.ANVIL_LAND);
            }
        }
        return InteractionResult.FAIL;
    }

    private void CreateLine(Player player, Vec3 generatorCenter) {
        if (LineVectorStart == null){
            LineVectorStart = generatorCenter;
            player.displayClientMessage(Component.literal("Added Position to Line start!"), true);
            player.playSound(SoundEvents.CHICKEN_EGG);
            player.getCooldowns().addCooldown(this, 5);
        }
        else{
            if (player.level() instanceof ServerLevel server){
                switch (state){
                    case 6 -> shapes.add(WorldShapeManager.CreateLine(server, 1, BlockStates, false, LineVectorStart, generatorCenter));
                    case 7 -> shapes.add(WorldShapeManager.CreateWeightedSquareLine(server, 10, BlockStates, false, LineVectorStart, generatorCenter, 5));
                    case 8 -> shapes.add(WorldShapeManager.CreateWeightedCircleLine(server, 25, BlockStates, false, LineVectorStart, generatorCenter, 10));
                    case 9 -> shapes.add(WorldShapeManager.CreateHollowWeightedCircleLine(server, 10, BlockStates, false, LineVectorStart, generatorCenter, 5, 1));
                    case 10 -> shapes.add(WorldShapeManager.CreateHollowWeightedCircleLine(server, 25, BlockStates, false, LineVectorStart, generatorCenter, 15, 2));
                    case 11 -> shapes.add(WorldShapeManager.CreateHollowWeightedCircleLine(server, 30, BlockStates, false, LineVectorStart, generatorCenter, 25, 3));
                    case 12 -> shapes.add(WorldShapeManager.CreateTunnel(server, 30, BlockStates, false, LineVectorStart, generatorCenter, 6, 1));
                }
                LineVectorStart = null;
            }
            player.displayClientMessage(Component.literal("Generating a new " + states.values()[state].name()), true);
            player.playSound(SoundEvents.CHICKEN_EGG);
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        BlockState state = player.level().getBlockState(pos);
        if (!player.isSecondaryUseActive()){
            if (!BlockStates.contains(state)){
                BlockStates.add(state);
                player.displayClientMessage(Component.literal("Added " + state.getBlock() + " to list of BlockStates!"), true);
                player.getCooldowns().addCooldown(this, 5);
                player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 1.5f);
            }
        }
        else {
            if (BlockStates.contains(state)) {
                BlockStates.remove(state);
                player.displayClientMessage(Component.literal("Removed " + state.getBlock() + " from list of BlockStates!"), true);
                player.getCooldowns().addCooldown(this, 5);
                player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1f, 0.5f);
            }
            else{
                player.displayClientMessage(Component.literal("Can't remove that block, it isn't in the list!"), true);
                player.getCooldowns().addCooldown(this, 5);
                player.playSound(SoundEvents.AMETHYST_CLUSTER_BREAK);
            }
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
