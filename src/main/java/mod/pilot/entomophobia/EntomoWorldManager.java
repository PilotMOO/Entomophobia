package mod.pilot.entomophobia;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntomoWorldManager {

    // MOB MANAGEMENT
    public static LivingEntity CreateNewEntityAt(EntityType<? extends LivingEntity> entityType, Vec3 pos, Level world){
        LivingEntity newEntity = entityType.create(world);
        assert newEntity != null;
        newEntity.setPos(pos);

        world.addFreshEntity(newEntity);
        return newEntity;
    }
    public static LivingEntity CreateNewEntityAt(EntityType<? extends LivingEntity> entityType, LivingEntity parent){
        return CreateNewEntityAt(entityType, parent.position(), parent.level());
    }
}
