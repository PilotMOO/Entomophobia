package mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.pain;

import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.StimulantType;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class RetaliateWithSwarmDecision extends Decision {
    public RetaliateWithSwarmDecision(HiveNervousSystem nervousSystem, StimulantType stimulantType) {
        super(nervousSystem, stimulantType);
    }

    public RetaliateWithSwarmDecision(HiveNervousSystem nervousSystem) {
        super(nervousSystem);
    }

    private HiveHeartEntity hh;
    @Override
    public boolean condition(StimulantPackage sPackage) {
        if (sPackage.serverSide() && sPackage.source() instanceof LivingEntity le){
            if (hh == null && (hh = this.nervousSystem.getHiveHeart()) == null){
                return false;
            }
            return hh.testValidEntity(le);
        }
        return false;
    }

    @Override
    public void activate(StimulantPackage sPackage) {
        if (!sPackage.serverSide() || !(sPackage.source() instanceof LivingEntity le)) return;
        if (hh == null && (hh = this.nervousSystem.getHiveHeart()) == null){
            return;
        }
        HiveSaveData.Packet packet = hh.accessData();
        RandomSource random = hh.getRandom();
        Level level = hh.level();
        int count = Math.min(packet.getTotalInStorage(), random.nextInt(8, 16));
        if (count == 0) return;
        ArrayList<MyiaticBase> mobs = new ArrayList<>();
        for (; count >= 0; count--){
            Vec3 delta = new Vec3(1, 0.5, 1);
            delta = delta.yRot((float)Math.toRadians(random.nextIntBetweenInclusive(-180, 180)));
            delta = delta.scale(0.5 + (random.nextDouble() * 0.5));

            LivingEntity le1 = packet.getAnythingFromStorage(level);
            if (le1 == null) continue;
            le1.setPos(hh.position());
            level.addFreshEntity(le1);
            le1.setDeltaMovement(delta);

            if (le1 instanceof MyiaticBase m){
                mobs.add(m);
                m.setTarget(le);
            }
        }
        SwarmManager.createAttackSwarm(mobs, 15, le);
    }
}
