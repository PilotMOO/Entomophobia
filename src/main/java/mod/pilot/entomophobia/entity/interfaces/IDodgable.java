package mod.pilot.entomophobia.entity.interfaces;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface IDodgable {
    double getDodgeChance();
    default boolean tryToDodge(Mob parent) {
        if (canDodge(parent)){
            if (parent.getRandom().nextDouble() <= getDodgeChance()){
                int Direction = parent.getRandom().nextBoolean() ? -1 : 1;
                parent.setDeltaMovement(parent.getDeltaMovement().add(Vec3.directionFromRotation(
                        new Vec2(parent.getRotationVector().x, parent.getRotationVector().y + 135 * Direction))
                        .multiply(1.25, 0, 1.25)));
                return true;
            }
        }
        return false;
    }

    default boolean canDodge(Mob parent){
        return parent.onGround() && parent.verticalCollisionBelow && !parent.horizontalCollision;
    }
}
