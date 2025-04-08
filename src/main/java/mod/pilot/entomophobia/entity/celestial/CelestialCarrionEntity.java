package mod.pilot.entomophobia.entity.celestial;

import com.google.common.collect.Lists;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import mod.pilot.entomophobia.sound.EntomoSounds;
import mod.pilot.entomophobia.systems.screentextdisplay.PhantomTextInstance;
import mod.pilot.entomophobia.systems.screentextdisplay.TextInstance;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import mod.pilot.entomophobia.systems.screentextdisplay.keyframes.ColorKeyframe;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;

public class CelestialCarrionEntity extends MyiaticBase {
    public CelestialCarrionEntity(EntityType<? extends Monster> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public enum ModelType{
        empty,
        base,
        tendril,
        eye,
        claw;
        public static final int totalCount = values().length;
        public static ModelType fromInt(int index){
            return ModelType.values()[index % totalCount];
        }
    }
    public static final EntityDataAccessor<Integer> MType = SynchedEntityData.defineId(CelestialCarrionEntity.class, EntityDataSerializers.INT);
    public ModelType getMType(){return ModelType.fromInt(entityData.get(MType));}
    public int getMTypeRaw(){return entityData.get(MType);}
    public void setMType(int type) {
        entityData.set(MType, type);
    }
    public void setMType(ModelType ordinal) {setMType(ordinal.ordinal());}
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MType", entityData.get(MType));
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setMType(tag.getInt("MType"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MType, 0);
    }

    private final EntityDimensions baseDimensions = new EntityDimensions(1f, 1f, false);
    private final EntityDimensions tendrilDimensions = new EntityDimensions(1.25f, 0.75f, false);
    private final EntityDimensions eyeDimensions = new EntityDimensions(0.8f, 1.9f, false);
    private final EntityDimensions clawDimensions = new EntityDimensions(1f, 0.9f, false);
    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return switch (getMType()){
            case empty -> super.getDimensions(pose);
            case base -> baseDimensions;
            case tendril -> tendrilDimensions;
            case eye -> eyeDimensions;
            case claw -> clawDimensions;
        };
    }
    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        if (MType.equals(pKey)){
            refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticPigEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20d)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }
    @Override
    protected void registerGoals() {
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide() && getMTypeRaw() == 0) {
            setMType(random.nextInt(1, ModelType.totalCount));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide()) SpreadEffects();
        else {
            CallOutToPlayers();
        }
    }

    private static final int InfatuationSpread = 300;
    private static final int InfatuationSpreadAOE = 64;
    private static final int MyiasisSpread = 400;
    private static final int MyiasisSpreadAOE = 12;
    public void SpreadEffects(){
        if (tickCount % InfatuationSpread == 0){
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                    AABB.ofSize(position(), InfatuationSpreadAOE, InfatuationSpreadAOE, InfatuationSpreadAOE))){
                if (e instanceof MyiaticBase) continue;
                e.addEffect(new MobEffectInstance(EntomoMobEffects.INFATUATION.get(), 1200));
            }
        }
        if (tickCount % MyiasisSpread == 0){
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                    AABB.ofSize(position(), MyiasisSpreadAOE, MyiasisSpreadAOE, MyiasisSpreadAOE))){
                if (e instanceof MyiaticBase) continue;
                e.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 600));
            }
        }
    }

    @Override
    public void push(@NotNull Entity entity) {
        super.push(entity);
        if (entity instanceof LivingEntity le){
            boolean flag = !le.hasEffect(EntomoMobEffects.MYIASIS.get()) || le.getEffect(EntomoMobEffects.MYIASIS.get()).getAmplifier() < 4;
            if (flag) le.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 1200, 4));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "IdleManager", 2, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean canSwarm() {
        return false;
    }

    public void CallOutToPlayers(){
        Entity cameraEntity;
        if (!level().isClientSide() || (cameraEntity = Minecraft.getInstance().getCameraEntity()) == null) return;
        for (Entity e : level().getEntities(this, AABB.ofSize(position(),
                Threshold1VoiceRange * 2, Threshold1VoiceRange * 2, Threshold1VoiceRange * 2))){
            if (!e.is(cameraEntity)) continue;

            double dist = this.distanceTo(e);
            if (_shouldGenerateTextAtDistance(dist)){
                String text = getRandomVoiceAtDistance(dist);
                String prepend = _generatePrependText(dist);
                String append = _generateAppendText(dist);

                int widthClamp = 0; //Clamps on the X and Y positions so they don't render clipping off of the screen
                int heightClamp = 0; //ditto
                if (dist > Threshold3VoiceRange){
                    //Making the clamps withing 0.9 of the dimensions of the screen IF the distance is great enough
                    //Otherwise it lets the text spill over the screen if you are close enough which looks cool :)
                    widthClamp = (int)(TextOverlay.width * 0.1d);
                    heightClamp = (int)(TextOverlay.height * 0.1d);
                }
                int x = random.nextInt(widthClamp, TextOverlay.width - widthClamp); //x and y positions
                int y = random.nextInt(heightClamp, TextOverlay.height - heightClamp); // ditto
                Color color;
                Color shiftColor = _generateEndColor(dist);
                color = withNoAlpha(shiftColor);
                int shiftSpeed = _generateShiftSpeed(dist);
                float size = _generateStartingSize(dist);
                int shaking = _generateShakingAmount(dist);
                int maxAge = _generateMaxAge(dist);
                boolean phantom = _generatePhantom(dist);
                TextInstance instance;
                if (phantom){
                    //Manage the parameters if it's a phantom...

                    int phantomCount = _generatePhantomCount(dist);
                    int alphaDifference = random.nextInt(50);
                    int updateFreq = _generateUpdateRate(dist);
                    float incSize = /*the scaling in size (multiplicative of total) from one daughter to the next.
                        Likely static or unused here...*/ 1.1f;
                    instance = PhantomTextInstance.create(text, prepend, append, phantomCount)
                            .withAlphaDifference(alphaDifference)
                            .updateRate(updateFreq)
                            .withIncrementalSize(incSize)
                            .at(x, y)
                            .withColor(color).shiftColor(shiftColor, shiftSpeed)
                            .ofSize(size)
                            .withShaking(shaking)
                            .aged(maxAge);
                } else {
                    //Otherwise, just make a normal one
                    instance = TextInstance.create(text, prepend, append)
                                    .at(x, y)
                                    .withColor(color).shiftColor(shiftColor, shiftSpeed)
                                    .ofSize(size)
                                    .withShaking(shaking)
                                    .aged(maxAge);
                }

                //Manage keyframes
                instance.addKeyframe(new ColorKeyframe(instance, maxAge - 20, withNoAlpha(shiftColor), 20));
                /**/

                //Finally add to overlay
                TextOverlay.instance.textInstances.add(instance);

                //Dont forget to play the sound!
                SoundEvent event;
                float volume, pitch;
                volume = _generateVoiceVolume(dist);
                pitch = _generateVoicePitch(dist);
                if (volume <= 0 || text == null || (event = EntomoSounds.getVoice(text)) == null) return;
                cameraEntity.playSound(event, volume, pitch);
            }
            break;
        }
    }
    private boolean _shouldGenerateTextAtDistance(double dist) {
        if (dist > Threshold1VoiceRange) return false;
        double chance = 1d / (dist / 8);
        return random.nextDouble() < (chance * 0.25) + 0.025;
    }

    /*generate prepend based on distance, E.G. adding obfuscation if greater than 12 or smth, etc.*/
    private @Nullable String _generatePrependText(double dist) {
        if (dist > Threshold1VoiceRange || dist < Threshold2VoiceRangeMin) return null;
        if (random.nextDouble() < 1 - (1d / ((dist - Threshold2VoiceRangeMin) / 8))) return "§k";
        return null;
    }
    /*Might not be needed, but maybe generate different punctuations? E.G. '...', '.', '!', etc.*/
    private static final ArrayList<String> appends = Lists.newArrayList(
            "", ".", "...", "!", "...!");
    private @Nullable String _generateAppendText(double dist) {
        if (dist > Threshold1VoiceRangeMin) return "...";
        if (dist < Threshold3VoiceRange) return switch (random.nextInt(3)){
            case 0 -> "!";
            case 1 -> "...!";
            case 2 -> "!!!";
            default -> null;
        };
        return appends.get(random.nextInt(appends.size()));
    }
    /*Make a color!!! very dark grey at start until white at like 16~, then turn slightly red as you approach 0*/
    /*private Color _generateStartColor(double dist){
        final int cumulativeStartingHex = 80;
        int r, g, b;
        int difference = 225 - cumulativeStartingHex;
        if (dist > Threshold2VoiceRangeMin){
            double ratio = (double)difference / (Threshold1VoiceRange - Threshold2VoiceRangeMin);
            r = g = b = (int)Math.min(225 - (dist - Threshold2VoiceRangeMin) * ratio, 225);
        } else {
            r = g = b = 225;
            final double maxBleed = 225d;
            double bleed = maxBleed / Threshold2VoiceRangeMin;
            g = b -= (int)((Threshold2VoiceRangeMin - dist) * bleed);
            r += (int)(dist * (1 / (30 / Threshold2VoiceRangeMin)));
        }
        return new Color(r, g, b, 0);
    }*/
    /*this is the color that it will shift to, usually the same color but with a higher alpha...*/
    private Color _generateEndColor(double dist){
        final int cumulativeStartingHex = 80;
        int r, g, b, a;
        int difference = 225 - cumulativeStartingHex;
        if (dist > Threshold2VoiceRangeMin){
            double ratio = (double)difference / (Threshold1VoiceRange - Threshold2VoiceRangeMin);
            r = g = b = (int)Math.min(225 - (dist - Threshold2VoiceRangeMin) * ratio, 225);
        } else {
            r = g = b = 225;
            final double maxBleed = 225d;
            double bleed = maxBleed / Threshold2VoiceRangeMin;
            g = b -= (int)((Threshold2VoiceRangeMin - dist) * bleed);
            r += (int)(dist * (1 / (30 / Threshold2VoiceRangeMin)));
        }
        a = (int) Math.min(255 - (255d / Threshold1VoiceRange) * (dist / 2), 255);
        return new Color(r, g, b, a);
    }
    /*how fast the color shifts. Slower at first but faster as dist decreases*/
    /*Fuck I need to make an option to have it to bleed out alpha as its age approaches the end... bwahhh*/
    /*P.S. make the shift alpha start at like 10~ and slowly increase to 255 as you get within 4 blocks*/
    /*Starting color alpha NEEDS to be 0 so it's not too sudden...*/
    //no changes to font or shadowing
    private int _generateShiftSpeed(double dist){
        return (int) Math.max((100d / Threshold1VoiceRange) * dist, 10) * 2;
    }
    /*start at like... 0.2f size at first then slowly scale to 1.5f-2.0f as you get to around 6*/
    private float _generateStartingSize(double dist){
        float deviate = 0;
        if (dist < Threshold2VoiceRange) {
            deviate += (this.random.nextFloat() * 0.2) * (this.random.nextBoolean() ? -1 : 1);
        }
        return (float) Math.max((0.75f * ((Threshold1VoiceRange / dist) * 0.33)) + deviate, 0.75f);
    }
    /*No shaking until like... 12? 14? and very minor shaking (like strength 1)*/
    private int _generateShakingAmount(double dist){
        if (dist >= Threshold1VoiceRangeMin) return 0;
        else return (int)(3 - (3d / Threshold1VoiceRangeMin) * dist);
    }
    /*max age sitting around... uhhh 40? at max dist. then up to 120 or so at 8 dist.*/
    private int _generateMaxAge(double dist){
        final int base = 40;
        final int additionCap = 240;
        return (int)((additionCap / Threshold1VoiceRange) * dist) + base;
    }
    /*Should this text instance be a phantom? make it a pretty low chance, slightly increasing with lowered distance*/
    private boolean _generatePhantom(double dist){
        if (dist >= Threshold1VoiceRangeMin) return false;
        else return random.nextDouble() < 1 - ((1d / Threshold1VoiceRangeMin) * dist);
    }
    /*How many daughters this phantom will get. Min 1, up to like 4?*/
    /*set to 5 here because it is almost impossible for this to return 5 due to integer rounding*/
    private int _generatePhantomCount(double dist){
        if (dist >= Threshold1VoiceRangeMin) return 0;
        return (int)(5 - (5d / Threshold1VoiceRangeMin) * dist);
    }
    /*how frequently (in ticks) that the daughters will update themselves prob start at like... 20? then slowly shift down to 10~*/
    private int _generateUpdateRate(double dist){
        if (dist >= Threshold1VoiceRangeMin) return 20;
        //Adding ten so the origin-bound is 10-20 rather than 0-10
        return (int)(10 + (10d / Threshold1VoiceRangeMin) * dist);
    }

    private float _generateVoiceVolume(double dist) {
        float base = -0.5f;
        return (float)(base + (4f / Threshold1VoiceRange) * dist);
    }
    private float _generateVoicePitch(double dist) {
        float base = 1f;
        return (float)(base - (0.25f / Threshold1VoiceRange) * dist);
    }

    public static final int Threshold1VoiceRange = 64;
    public static final int Threshold1VoiceRangeMin = 28;
    public static final int Threshold2VoiceRange = 32;
    public static final int Threshold2VoiceRangeMin = 14;
    public static final int Threshold3VoiceRange = 16;
    public static final ArrayList<ArrayList<String>> AstralVoices = new ArrayList<>();
    public static void createVoices(){
        ArrayList<String> threshold1 = new ArrayList<>();
        threshold1.add("Closer");
        threshold1.add("Come");

        ArrayList<String> threshold2 = new ArrayList<>();
        threshold2.add("Free me");
        threshold2.add("Join us");
        threshold2.add("Fallen");
        threshold2.add("Defeat");
        threshold2.add("Pain");

        ArrayList<String> threshold3 = new ArrayList<>();
        threshold3.add("They know my name");
        threshold3.add("They watched me BLEED");
        threshold3.add("Reclaim me");
        threshold3.add("Salvation");
        threshold3.add("Revive");
        threshold3.add("FIX US");

        ArrayList<String> all = new ArrayList<>();
        all.addAll(threshold1);
        all.addAll(threshold2);
        all.addAll(threshold3);

        AstralVoices.add(all);
        AstralVoices.add(threshold1);
        AstralVoices.add(threshold2);
        AstralVoices.add(threshold3);
    }
    public static ArrayList<String> getAllVoicesAtDistance(double distance){
        if (distance < 0) return AstralVoices.get(0);

        ArrayList<String> toReturn = new ArrayList<>();
        if (distance > Threshold1VoiceRange + 1) return toReturn;

        if (distance >= Threshold1VoiceRangeMin){
            toReturn.addAll(AstralVoices.get(1));
        }
        if (distance <= Threshold2VoiceRange && distance >= Threshold2VoiceRangeMin){
            toReturn.addAll(AstralVoices.get(2));
        }
        if (distance <= Threshold3VoiceRange){
            toReturn.addAll(AstralVoices.get(3));
        }
        return toReturn;
    }
    public @Nullable String getRandomVoiceAtDistance(double distance){
        ArrayList<String> voices = getAllVoicesAtDistance(distance);
        if (voices == null || voices.isEmpty()) return null;
        else return voices.get(random.nextInt(voices.size()));
    }

    private Color withNoAlpha(Color color){
        return new Color(color.getRed(), color.getBlue(), color.getGreen(), 0);
    }
}
