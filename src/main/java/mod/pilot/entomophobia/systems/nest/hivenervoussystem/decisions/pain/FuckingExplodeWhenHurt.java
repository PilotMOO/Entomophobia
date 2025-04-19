package mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.pain;

import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.StimulantType;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FuckingExplodeWhenHurt extends Decision {
    public FuckingExplodeWhenHurt(HiveNervousSystem nervousSystem, StimulantType stimulantType) {
        super(nervousSystem, stimulantType);
    }

    public FuckingExplodeWhenHurt(HiveNervousSystem nervousSystem) {
        super(nervousSystem);
    }

    @Override
    public boolean condition(StimulantPackage sPackage) {
        return true;
    }

    @Override
    public void activate(StimulantPackage sPackage) {
        HiveHeartEntity HH;
        if ((HH = nervousSystem.getHiveHeart()) != null) {
            Vec3 pos = HH.position();
            server().explode(HH, pos.x, pos.y, pos.z, 3f, Level.ExplosionInteraction.NONE);
        }
    }
}
