package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.data.WorldShapes.EntomoWorldShapeManager;
import mod.pilot.entomophobia.data.WorldShapes.SphereGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NestBuilder extends Item {
    public NestBuilder(Properties pProperties) {
        super(pProperties);
    }
    static SphereGenerator sphere;
    private static final List<BlockState> states = new ArrayList<>(Arrays.asList(Blocks.MUD.defaultBlockState()));

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel instanceof ServerLevel server){
            System.out.println("Ticking on the server!");
            if (sphere != null){
                System.out.println("building that sphere!");
                sphere.Build();
                if (sphere.isOfState(EntomoWorldShapeManager.GeneratorStates.done)){
                    sphere = null;
                    System.out.println("sphere is now null");
                }
            }
            else{
                System.out.println("makin' a new sphere!");
                sphere = EntomoWorldShapeManager.CreateSphere(server, 2, states, pPlayer.position(), false, 3);
                return InteractionResultHolder.success(new ItemStack(this));
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
