package mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.idle;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.data.IntegerCycleTracker;
import mod.pilot.entomophobia.data.worlddata.EntomoGeneralSaveData;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.AI.ShortLifespanGoal;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.StimulantType;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;
import mod.pilot.entomophobia.systems.swarm.SwarmManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class CreateHuntSwarmsDecision extends Decision {
    public CreateHuntSwarmsDecision(HiveNervousSystem nervousSystem, StimulantType stimulantType) {
        super(nervousSystem, stimulantType);
    }

    public CreateHuntSwarmsDecision(HiveNervousSystem nervousSystem) {
        super(nervousSystem);
    }

    private static final int mobCap = Config.SERVER.mob_cap.get();
    private final IntegerCycleTracker.Randomized conditionTicker = new IntegerCycleTracker.Randomized(24000, 12000);
    @Override
    public boolean condition(StimulantPackage sPackage) {
        if (sPackage.serverSide()
                && accessHiveHeart() != null
                && hh.accessData().getTotalInStorage() > 0
                && (mobCap - EntomoGeneralSaveData.getMyiaticCount()) > 16){
            return conditionTicker.tick();
        }
        return false;
    }

    @Override
    public void activate(StimulantPackage sPackage) {
        System.out.println("CreateHuntSwarmDecision INVOKED");

        if (accessHiveHeart() == null) return;
        Level level = hh.level();

        final int maxHeight = level.getMaxBuildHeight();
        final int minHeight = level.getMinBuildHeight();
        BlockPos.MutableBlockPos bPos = hh.blockPosition().mutable();
        while (level.getBrightness(LightLayer.SKY, bPos) < 8 && bPos.getY() <= maxHeight){
            bPos.move(0, 1, 0);
        }
        while (level.getBlockState(bPos.below()).canBeReplaced() && bPos.getY() >= minHeight){
            bPos.move(0, -1, 0);
        }

        Vec3 pos = bPos.getCenter();
        HiveSaveData.Packet packet = hh.accessData();
        int inStorage = packet.getTotalInStorage();
        int swarmCount = Math.min(inStorage, level.random.nextInt(8, 16));
        ArrayList<MyiaticBase> units = new ArrayList<>(swarmCount);
        for (int i = 0; i < swarmCount; i++){
            LivingEntity le1 = packet.getAnythingFromStorage(level);
            if (le1 == null) continue;
            le1.setPos(pos);
            level.addFreshEntity(le1);

            if (le1 instanceof MyiaticBase m){
                units.add(m);
            }
        }
        SwarmManager.createHuntSwarm(units, swarmCount, null)
                .relayOrder(new ShortLifespanGoal(null, 3000, 0,
                        m -> m.getTarget() == null || m.distanceTo(hh) > 256), false);
    }
}
