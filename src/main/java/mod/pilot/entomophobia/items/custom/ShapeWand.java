package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.WorldShapes.EntomoWorldShapeManager;
import mod.pilot.entomophobia.data.WorldShapes.ShapeGenerator;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapeWand extends Item {
    private static final int amountOfStates = 3;
    enum states{
        cube,
        sphere,
        hollow_sphere
    }
    int state = 0;

    public ShapeWand(Properties pProperties) {
        super(pProperties);
    }
    static List<ShapeGenerator> shapes = new ArrayList<>();
    public List<ShapeGenerator> shapeInstances(){
        return new ArrayList<>(shapes);
    }
    private static final List<BlockState> BlockStates = new ArrayList<>(Arrays.asList(Blocks.MUD.defaultBlockState()));

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (shapes != null){
            for (ShapeGenerator shape : shapeInstances()){
                if (pLevel instanceof ServerLevel){
                    shape.Build();
                }
                if (shape.isOfState(EntomoWorldShapeManager.GeneratorStates.done)){
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
        if (player.isSecondaryUseActive()){
            SwitchState(player);
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResultHolder.success(new ItemStack(this));
        }
        return InteractionResultHolder.fail(new ItemStack(this));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && context.getLevel() instanceof ServerLevel server){
            if (!context.isSecondaryUseActive()){
                if (state == states.cube.ordinal()){
                    shapes.add(EntomoWorldShapeManager.CreateCube(server, 2, BlockStates, context.getClickedPos().getCenter(), false, 5));
                }
                if (state == states.sphere.ordinal()) {
                    shapes.add(EntomoWorldShapeManager.CreateSphere(server, 2, BlockStates, context.getClickedPos().getCenter(), false, 3));
                }
                if (state == states.hollow_sphere.ordinal()) {
                    shapes.add(EntomoWorldShapeManager.CreateHollowSphere(server, 2, BlockStates, context.getClickedPos().getCenter(), false, 6, 2));
                }
                player.displayClientMessage(Component.literal("Generating a new " + states.values()[state].name()), true);
            }
        }
        return InteractionResult.SUCCESS;
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
