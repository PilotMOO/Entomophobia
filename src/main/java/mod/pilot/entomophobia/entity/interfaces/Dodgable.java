package mod.pilot.entomophobia.entity.interfaces;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface Dodgable {
    double getDodgeChance();
    default boolean TryToDodge(Mob parent) {
        if (CanDodge(parent)){
            if (parent.getRandom().nextDouble() <= getDodgeChance()){
                int Direction = parent.getRandom().nextIntBetweenInclusive(0, 1) != 0 ? -1 : 1;
                parent.setDeltaMovement(parent.getDeltaMovement().add(Vec3.directionFromRotation(new Vec2(parent.getRotationVector().x, parent.getRotationVector().y + 135 * Direction)).multiply(1.25, 0, 1.25)));
                return true;
            }
        }
        return false;
    }

    default boolean CanDodge(Mob parent){
        return parent.onGround() && parent.verticalCollisionBelow && !parent.horizontalCollision;
    }
}
