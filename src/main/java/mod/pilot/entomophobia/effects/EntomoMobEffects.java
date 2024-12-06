package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.effects.pheromones.PheromoneFrenzy;
import mod.pilot.entomophobia.effects.pheromones.PheromoneHunt;
import mod.pilot.entomophobia.effects.pheromones.PheromonePrey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntomoMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Entomophobia.MOD_ID);

    public static final RegistryObject<MobEffect> HUNT = MOB_EFFECTS.register("hunt",
            PheromoneHunt::new);
    public static final RegistryObject<MobEffect> PREY = MOB_EFFECTS.register("prey",
            PheromonePrey::new);
    public static final RegistryObject<MobEffect> FRENZY = MOB_EFFECTS.register("frenzy",() ->
            new PheromoneFrenzy().addAttributeModifier(Attributes.FOLLOW_RANGE, "e145f982-ea68-4ba0-ad42-2e5f5c9a1bac", 1.5f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "29c7540d-ce41-4f8a-91a1-866f3a0887b3", -0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "61974953-04c6-4e8f-ab3e-5d30301fdf08", 0.75f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.JUMP_STRENGTH, "1a639cf5-89f1-4825-89c7-27edca76e05e", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "2988e860-11c1-4188-a8f2-f303248567d0", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "d03f01a2-c9fc-4a41-a101-f223b515fbe6", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> MYIASIS = MOB_EFFECTS.register("myiasis",
            Myiasis::new);
    public static final RegistryObject<MobEffect> NEUROINTOXICATION = MOB_EFFECTS.register("neurointoxication", () ->
            new Neurointoxication().addAttributeModifier(Attributes.MOVEMENT_SPEED, "99c9da36-345b-459b-83b1-81aa4f409756", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, "aa1a1e37-3c7f-4f97-b709-92b5c0b2dd71", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.JUMP_STRENGTH, "65972154-8d83-41a4-abfc-f229b503caf1", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> OVERSTIMULATION = MOB_EFFECTS.register("overstimulation", () ->
            new Overstimulation().addAttributeModifier(Attributes.MOVEMENT_SPEED, "5276572d-53ca-4f42-a024-765c164b3a21", 1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, "e00418a6-1dbb-4d23-bb6f-84608f7fc32f", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
