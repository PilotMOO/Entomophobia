package mod.pilot.entomophobia.entity.celestial;

import mod.pilot.entomophobia.data.ParabolaCalculator;
import mod.pilot.entomophobia.entity.EntoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticChickenEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class CarrioniteEntity extends LivingEntity implements GeoEntity {

    public CarrioniteEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static CarrioniteEntity createOrbitingCarrioniteOverPlayer(ServerPlayer player){
        ServerLevel server = player.serverLevel();
        final int maxHeight = server.getMaxBuildHeight();
        final int minHeight = server.getMinBuildHeight();
        BlockPos.MutableBlockPos bPos = player.blockPosition().mutable();
        while (server.getBrightness(LightLayer.SKY, bPos) < 8 && bPos.getY() <= maxHeight){
            bPos.move(0, 1, 0);
        }
        while (server.getBlockState(bPos.below()).canBeReplaced() && bPos.getY() >= minHeight){
            bPos.move(0, -1, 0);
        }
        Vec3 pos = bPos.getCenter();
        Vec3 offset = pos.add(0.35, 0, -0.65);

        CarrioniteEntity cE = EntoEntities.CARRIONITE.get().create(server);
        assert cE != null;
        cE.orbitInit(pos, offset, defaultParabolaOffset);
        server.addFreshEntity(cE);
        return cE;
    }

    public @Nullable Vec3 orbitCenter = null;
    public @Nullable Vec3 orbitStart = null;
    public @Nullable Vec3 orbitEnd = null;
    public ParabolaCalculator calculator = null;

    private static final int orbitDuration = 120;
    private static final double orbitTickRatio = 1d / orbitDuration;

    private static final double parabolaSlope = -0.1d;
    private static final float defaultParabolaOffset = 64;

    public static final EntityDataAccessor<Float> DATA_PARABOLA_OFFSET = SynchedEntityData.defineId(CarrioniteEntity.class, EntityDataSerializers.FLOAT);
    public float getParabolaOffset(){return entityData.get(DATA_PARABOLA_OFFSET);}

    public void orbitInit(Vec3 center, Vec3 startPos, double yOffset){
        orbitCenter = center;
        calculator = new ParabolaCalculator(parabolaSlope, 0, yOffset, center);
        Pair<Vec3, Vec3> startEnd = calculator.calculateParabolaXValuesFromWorldPosition(startPos);
        if (startEnd == null){
            System.err.println("[CARRIONITE] Oops! startPos " + startPos + " contains an invalid Y position for the calculator's parabola! :[");
            System.err.println("[CARRIONITE] Info-- " + calculator);
        } else {
            orbitStart = startEnd.getA();
            orbitEnd = startEnd.getB();
            this.setPos(orbitStart);
            entityData.set(DATA_PARABOLA_OFFSET, (float)yOffset);
        }
    }

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticChickenEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 6D)
                .add(Attributes.ARMOR, 1)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 2D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.25D)
                .add(Attributes.ATTACK_SPEED, 2D);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PARABOLA_OFFSET, defaultParabolaOffset);
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        boolean flag = tag.getBoolean("origin");
        entityData.set(DATA_PARABOLA_OFFSET, tag.getFloat("parabola_offset"));
        if (flag){
            orbitCenter = new Vec3(tag.getDouble("origin_c_X"),
                    tag.getDouble("origin_c_Y"),
                    tag.getDouble("origin_c_Z"));
            orbitStart = new Vec3(tag.getDouble("origin_s_X"),
                    tag.getDouble("origin_s_Y"),
                    tag.getDouble("origin_s_Z"));
            orbitEnd = new Vec3(tag.getDouble("origin_e_X"),
                    tag.getDouble("origin_e_Y"),
                    tag.getDouble("origin_e_Z"));

            if (calculator == null){
                calculator = new ParabolaCalculator(parabolaSlope, 0, getParabolaOffset(), orbitCenter);
            }
            calculator.setParabolaCenter(orbitCenter);
        } else {
            orbitCenter = null;
            orbitStart = null;
            orbitEnd = null;
            calculator = null;
        }
    }

    private static final List<ItemStack> EMPTY = new ArrayList<>();
    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return EMPTY;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }
    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot pSlot, @NotNull ItemStack pStack) {}

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        boolean flag = orbitCenter != null && orbitStart != null && orbitEnd != null;
        tag.putBoolean("origin", flag);
        tag.putFloat("parabola_offset", getParabolaOffset());
        if (flag){
            tag.putDouble("origin_c_X", orbitCenter.x);
            tag.putDouble("origin_c_Y", orbitCenter.y);
            tag.putDouble("origin_c_Z", orbitCenter.z);

            tag.putDouble("origin_s_X", orbitStart.x);
            tag.putDouble("origin_s_Y", orbitStart.y);
            tag.putDouble("origin_s_Z", orbitStart.z);

            tag.putDouble("origin_e_X", orbitEnd.x);
            tag.putDouble("origin_e_Y", orbitEnd.y);
            tag.putDouble("origin_e_Z", orbitEnd.z);
        }
    }
    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override public boolean isNoGravity() {return true;}

    @Override
    public void tick() {
        if (orbitCenter != null && orbitStart != null && orbitEnd != null){
            setPos(calculator.calculateParabolaYValueFromWorldPosition(position().add(orbitStart.subtract(orbitEnd).normalize())));
        }
        super.tick();
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "IdleManager", 2, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
