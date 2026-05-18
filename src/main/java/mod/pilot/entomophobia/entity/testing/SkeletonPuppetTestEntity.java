package mod.pilot.entomophobia.entity.testing;

import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class SkeletonPuppetTestEntity extends LivingEntity implements GeoEntity, Targeting {
    private static final Logger log = LoggerFactory.getLogger(SkeletonPuppetTestEntity.class);

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticChickenEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.ATTACK_SPEED, 1D);
    }
    private static final NonNullList<ItemStack> items = NonNullList.create();
    @Override public Iterable<ItemStack> getArmorSlots() {return items;}
    @Override public ItemStack getItemBySlot(EquipmentSlot pSlot) {return ItemStack.EMPTY;}
    @Override public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {}
    @Override public HumanoidArm getMainArm() {return HumanoidArm.LEFT;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    //All ^^ of this is useless filler shit, don't worry about it.

    public SkeletonPuppetTestEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    //Implementing Targeting interface
    public @Nullable LivingEntity target;
    @Nullable public LivingEntity getTarget() {
        return this.target;
    }
    public void setTarget(@Nullable LivingEntity pTarget) {
        LivingChangeTargetEvent changeTargetEvent = ForgeHooks.onLivingChangeTarget(this, pTarget, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        if(!changeTargetEvent.isCanceled()) {
            this.target = changeTargetEvent.getNewTarget();
        }
    }

    @Override
    public void aiStep() {
        //Tick hardcoded goals
        targetClosestPlayer();
        if (tickCount % 100 == 0) simpleLookAtTarget();
    }

    private void targetClosestPlayer() {
        List<Player> players = level().getNearbyPlayers(TargetingConditions.forNonCombat(), this,
                getBoundingBox().inflate(getAttributeValue(Attributes.FOLLOW_RANGE)));
        if (!players.isEmpty()) {
            if (players.size() == 1) setTarget(players.get(0));
            else {
                Player player = null;
                double dist = Double.MAX_VALUE;
                for (Player player1 : players) {
                    double dist1 = distanceTo(player1);
                    if (dist1 < dist) {
                        player = player1;
                        dist = dist1;
                    }
                }
                setTarget(player);
            }
        }
    }

    private void simpleLookAtTarget() {
        if (target == null) return;
        lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
    }
}
