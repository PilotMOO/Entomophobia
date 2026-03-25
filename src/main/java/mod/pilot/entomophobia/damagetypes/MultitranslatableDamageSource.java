package mod.pilot.entomophobia.damagetypes;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultitranslatableDamageSource {

    public MultitranslatableDamageSource(@Nullable Level level,
                                         ResourceKey<DamageType> resource,
                                         String... translatable){
        this.translatables = translatable;
        this.key = resource;
        if (level != null){
            damage = level.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(resource);
        } else lazy = true;
    }
    public ResourceKey<DamageType> key;
    public boolean lazy = false;
    public Holder<DamageType> damage;
    public final String[] translatables;

    public Instance instance(@NotNull LivingEntity victim, @Nullable Entity killer){
        if (lazy) {
            Level level = victim.level();
            damage = level.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(key);
            lazy = false;
        }
        return new Instance(damage, victim, killer);
    }

    public class Instance extends DamageSource {
        public Instance(Holder<DamageType> pType, @Nullable Entity pDirectEntity, @Nullable Entity pCausingEntity) {
            super(pType, pDirectEntity, pCausingEntity);
        }

        public String randomTranslatable(RandomSource random) {
            return translatables[random.nextInt(translatables.length)];
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity victim) {
            LivingEntity killer = victim.getKillCredit();
            RandomSource random = victim.getRandom();
            if (killer != null) {
                return Component.translatable(randomTranslatable(random),
                        victim.getDisplayName(), killer.getDisplayName());
            } else return Component.translatable(randomTranslatable(random),
                    victim.getDisplayName());
        }
    }
}
