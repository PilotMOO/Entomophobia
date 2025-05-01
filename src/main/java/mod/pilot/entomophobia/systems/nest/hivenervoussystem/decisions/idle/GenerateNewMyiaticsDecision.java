package mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.idle;

import mod.pilot.entomophobia.data.IntegerCycleTracker;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.StimulantType;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;


public class GenerateNewMyiaticsDecision extends Decision {
    public GenerateNewMyiaticsDecision(HiveNervousSystem nervousSystem, StimulantType stimulantType) {
        super(nervousSystem, stimulantType);
    }

    public GenerateNewMyiaticsDecision(HiveNervousSystem nervousSystem) {
        super(nervousSystem);
    }
    private final IntegerCycleTracker.Randomized tracker = new IntegerCycleTracker.Randomized(6000, 2400);

    @Override
    public boolean condition(StimulantPackage sPackage) {
        return sPackage.serverSide() && tracker.tick();
    }

    @Override
    public void activate(StimulantPackage sPackage) {
        HiveHeartEntity hh = nervousSystem.getHiveHeart();
        if (hh == null) return;
        HiveSaveData.Packet packet = hh.accessData();
        String encode = null;
        int count = packet.storedEntities.size();
        if (count == 0) return;
        int index = server().random.nextInt(count);
        for (String s : packet.storedEntities.keySet()){
            if (index == 0) encode = s;
            else --index;
        }
        if (encode != null){
            packet.addToStorage(encode).thenSync(hh);
            System.out.println("Adding a new " + encode + " to storage. Count: " + packet.getCountInStorage(encode));
        }
    }
}
