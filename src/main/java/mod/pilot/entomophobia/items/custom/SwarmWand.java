package mod.pilot.entomophobia.items.custom;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.swarm.Swarm;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SwarmWand extends Item {
    public SwarmWand(Properties pProperties) {
        super(pProperties);
    }

    private static final int amountOfTypes = SwarmManager.SwarmTypes.values().length;
    private byte currentType = 0;
    private SwarmManager.SwarmTypes getCurrentType(){
        return SwarmManager.SwarmTypes.values()[currentType];
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack item, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (target instanceof MyiaticBase M){
            if (M.level() instanceof ServerLevel server && attacker instanceof ServerPlayer player){
                if (player.isSecondaryUseActive()){
                    Vec3 pos = M.position();
                    String toPrint = M.isInSwarm() ? "Myiatic is apart of " + M.getSwarm() : "Myiatic is not in a swarm";
                    server.getNearestPlayer(pos.x, pos.y, pos.z, -1, false)
                            .displayClientMessage(Component.literal(toPrint), false);
                    if (M.amITheCaptain()){
                        server.getNearestPlayer(pos.x, pos.y, pos.z, -1, false)
                                .displayClientMessage(Component.literal("This is the captain of the swarm!"), false);
                    }
                }
                else{
                    SwarmManager.createSwarm(getCurrentType(), M, SwarmManager.getBaseSwarmMaxSize(), null);
                    server.playSound(null, M.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0f, 1.0f);
                    Vec3 pos = M.position();
                    server.getNearestPlayer(pos.x, pos.y, pos.z, -1, false)
                            .displayClientMessage(Component.literal("Assigning " + target.getEncodeId() + " as captain of a new swarm!"), true);
                }
                target.heal(item.getDamageValue());
            }
        }
        return false;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedhand) {
        if (player.isSecondaryUseActive()){
            if (level instanceof ServerLevel){
                if (currentType < amountOfTypes - 1){
                    currentType++;
                }
                else{
                    currentType = 0;
                }
            }
            else{
                int StateGhost = currentType;
                if (StateGhost < amountOfTypes - 1){
                    StateGhost++;
                }
                else{
                    StateGhost = 0;
                }
                player.displayClientMessage(Component.literal(SwarmManager.SwarmTypes.values()[StateGhost].name()), true);
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
        else{
            for (Swarm swarm : SwarmManager.getSwarms()){
                if (swarm.isDisbanded()) continue;
                player.displayClientMessage(Component.literal("Highlighting all Captains"), true);
                swarm.getCaptain().addEffect(new MobEffectInstance(MobEffects.GLOWING, 100));
            }
        }
        return InteractionResultHolder.success(new ItemStack(this));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level pLevel, @NotNull List<Component> components, @NotNull TooltipFlag isAdvanced) {
        components.add(Component.translatable("item.entomophobia.tooltip.dev_wand"));
        components.add(Component.literal("Type: " + getCurrentType()));
        components.add(Component.translatable("item.entomophobia.tooltip.swarm_wand"));
        super.appendHoverText(stack, pLevel, components, isAdvanced);
    }
}
