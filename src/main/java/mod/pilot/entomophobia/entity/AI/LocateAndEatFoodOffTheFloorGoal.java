package mod.pilot.entomophobia.entity.AI;

import com.mojang.datafixers.util.Pair;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LocateAndEatFoodOffTheFloorGoal extends Goal {
    final MyiaticBase parent;
    final int SearchRange;
    ItemEntity targetItem;

    public LocateAndEatFoodOffTheFloorGoal(MyiaticBase parent, int searchRange){
        this.parent = parent;
        SearchRange = searchRange;
    }
    @Override
    public boolean canUse() {
        return parent.getTarget() == null && (parent.getHealth() < parent.getAttributeValue(Attributes.MAX_HEALTH) && !parent.hasEffect(MobEffects.REGENERATION)) || parent.hasEffect(MobEffects.HUNGER);
    }

    @Override
    public void tick() {
        targetItem = LocateFood();
        if (targetItem != null && (parent.getHealth() < parent.getAttributeValue(Attributes.MAX_HEALTH) && !parent.hasEffect(MobEffects.REGENERATION) || parent.hasEffect(MobEffects.HUNGER))){
            parent.getNavigation().moveTo(targetItem, 1.0d);
            if (parent.distanceTo(targetItem) < 2){
                EatFood();
            }
        }
        else{
            stop();
        }
    }

    @Override
    public void stop() {
        targetItem = null;
    }

    private void EatFood() {
        ItemStack foodItem = targetItem.getItem();
        if (foodItem.isEdible()){
            int foodAmount = foodItem.getCount();
            int healAmount = foodItem.getFoodProperties(parent).getNutrition() * 40;
            int amp = 0;
            if (parent.hasEffect(MobEffects.HUNGER)){
                healAmount /= 4;
            }
            while (healAmount > 200){
                healAmount /= 2;
                amp++;
            }
            if (foodAmount > 0){
                targetItem.getItem().setCount(foodAmount - 1);
                parent.addEffect(new MobEffectInstance(MobEffects.REGENERATION, healAmount, amp));
                if (foodItem.getFoodProperties(parent) != null){
                    for (Pair<MobEffectInstance, Float> foodEffect : foodItem.getFoodProperties(parent).getEffects()) {
                        parent.addEffect(foodEffect.getFirst());
                    }
                }
            }
            parent.level().playSound(parent, parent.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.HOSTILE, 2.0f, 0.75f);
        }
    }

    protected ItemEntity LocateFood(){
        AABB FoodLocator = parent.getBoundingBox().inflate(SearchRange);
        ItemEntity closestFood = null;
        for (ItemEntity possibleFood : parent.level().getEntitiesOfClass(ItemEntity.class, FoodLocator)){
            if (possibleFood.getItem().isEdible()){
                if (closestFood != null){
                    if (closestFood.distanceTo(parent) > possibleFood.distanceTo(parent)){
                        closestFood = possibleFood;
                    }
                }
                else{
                    closestFood = possibleFood;
                }
            }
        }
        return closestFood;
    }
}
