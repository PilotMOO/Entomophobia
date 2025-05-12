package mod.pilot.entomophobia.systems.nest.hivenervoussystem;

import com.google.common.collect.Lists;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.idle.CreateHuntSwarmsDecision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.idle.GenerateNewMyiaticsDecision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.pain.RetaliateWithSwarmDecision;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

public class HiveNervousSystem {
    public HiveNervousSystem(Nest nest, UUID hiveHeartUUID){
        this.nest = nest;
        this.hiveHeartUUID = hiveHeartUUID;
        this.serverLevel = nest.server;
        Manager.addNervousSystem(this);
    }
    public HiveNervousSystem(Nest nest, HiveHeartEntity hiveHeart){
        this.nest = nest;
        this.hiveHeartUUID = hiveHeart.getUUID();
        this.serverLevel = nest.server;
        Manager.addNervousSystem(this);
    }
    public HiveNervousSystem(Nest nest){
        setNest(nest);
        Manager.addNervousSystem(this);
    }

    public void setNest(Nest nest){
        this.nest = nest;
        this.hiveHeartUUID = nest.mainChamber.getHiveHeartUUID();
        this.serverLevel = nest.server;
    }
    public UUID hiveHeartUUID;
    public Nest nest;
    public ServerLevel serverLevel;
    public @Nullable HiveHeartEntity getHiveHeart(){
        if (hiveHeartUUID != null && serverLevel != null){
            return (HiveHeartEntity)serverLevel.getEntity(hiveHeartUUID);
        } return null;
    }

    public void attachDecision(Decision d){
        filterStimulants(d.stimulantType).attachDecision(d);
    }

    public void populateDefaultDecisions(){
        attachDecision(new RetaliateWithSwarmDecision(this, StimulantType.Pain));
        attachDecision(new GenerateNewMyiaticsDecision(this, StimulantType.Idle));
        attachDecision(new CreateHuntSwarmsDecision(this, StimulantType.Idle));
    }

    public void stimulate(StimulantType stimulant, StimulantPackage sPackage){
        filterStimulants(stimulant).respond(sPackage);
    }
    private Response filterStimulants(StimulantType stimulant){
        return switch (stimulant){
            case Idle -> idleResponse;
            case Pain -> painResponse;
            case Alarm -> alarmResponse;
            default -> dud;
        };
    }

    public final Response dud = new Response(d -> false);
    public final Response idleResponse = new Response(d -> d.stimulantType == StimulantType.Idle);
    public final Response painResponse = new Response(d -> d.stimulantType == StimulantType.Pain);
    public final Response alarmResponse = new Response(d -> d.stimulantType == StimulantType.Alarm);

    public void queActiveDecision(Decision.Continuous dc){
        this.queuedDecisions.add(dc);
        this.activeQueued = true;
    }
    private final ArrayList<Decision.Continuous> queuedDecisions = new ArrayList<>();
    private boolean activeQueued = false;
    private final ArrayList<Decision.Continuous> activeDecisions = new ArrayList<>();
    public void tickContinuousDecisions(){
        if (activeQueued){
            activeDecisions.addAll(queuedDecisions);
            queuedDecisions.clear();
            activeQueued = false;
        }
        ArrayList<Decision.Continuous> toRemove = null;
        for(Decision.Continuous c : activeDecisions){
            if (c.activeUntil()) c.lifecycle();
            else {
                c.finish();
                if (toRemove == null) toRemove = Lists.newArrayList(c);
                else toRemove.add(c);
            }
        }
        if (toRemove != null) activeDecisions.removeAll(toRemove);
    }
    private void flushDecisions(){
        queuedDecisions.clear();
        activeQueued = false;
        activeDecisions.clear();
    }

    public class Response {
        protected Predicate<Decision> supportedDecisionType;
        public Response(Predicate<Decision> supportedDecisionType){
            this.supportedDecisionType = supportedDecisionType;
        }

        public void attachDecision(Decision d){
            if (supportedDecisionType.test(d)){
                que.add(d);
                this.decisionsQueued = true;
            }
        }
        public void removeDecision(Decision d){
            unque.add(d);
            this.decisionsUnqueued = true;
        }
        public void removeDecisions(Collection<Decision> d){
            unque.addAll(d);
            this.decisionsUnqueued = true;
        }
        public void removeDecisions(Predicate<Decision> removeIf){
            for(Decision d : decisions){
                if (removeIf.test(d)) {
                    unque.add(d);
                    this.decisionsUnqueued = true;
                }
            }
        }
        protected ArrayList<Decision> que = new ArrayList<>();
        protected boolean decisionsQueued;
        protected ArrayList<Decision> unque = new ArrayList<>();
        protected boolean decisionsUnqueued;
        protected ArrayList<Decision> decisions = new ArrayList<>();

        public void respond(StimulantPackage sPackage){
            if (decisionsQueued){
                decisions.addAll(que);
                que.clear();
                decisionsQueued = false;
            }
            if (decisionsUnqueued) {
                decisions.removeAll(unque);
                unque.clear();
                decisionsUnqueued = false;
            }
            decisions.forEach(d -> {
                d.trigger(sPackage);
                Decision.Continuous c;
                if ((c = d.continuous()) != null) queActiveDecision(c);
            });
        }
    }
    public static class Manager{
        public static void setup(){
            MinecraftForge.EVENT_BUS.addListener(Manager::tickNervousSystems);
            MinecraftForge.EVENT_BUS.addListener(Manager::serverCloseCleanup);
        }

        private static final ArrayList<HiveNervousSystem> activeNervousSystems = new ArrayList<>();
        private static final ArrayList<HiveNervousSystem> que = new ArrayList<>();
        private static boolean addFlag = false;
        private static final ArrayList<HiveNervousSystem> unque = new ArrayList<>();
        private static boolean removeFlag = false;
        public static void addNervousSystem(HiveNervousSystem hNS){
            que.add(hNS);
            addFlag = true;
        }
        public static void removeNervousSystem(HiveNervousSystem hNS){
            unque.add(hNS);
            removeFlag = true;
        }

        public static void tickNervousSystems(TickEvent.ServerTickEvent event){
            if (addFlag){
                activeNervousSystems.addAll(que);
                que.clear();
                addFlag = false;
            }
            if (removeFlag){
                activeNervousSystems.removeAll(unque);
                unque.clear();
                removeFlag = false;
            }
            activeNervousSystems.forEach(hNS -> {
                hNS.stimulate(StimulantType.Idle, StimulantPackage.empty(event.side.isServer()));
                hNS.tickContinuousDecisions();
            });
        }
        public static void serverCloseCleanup(ServerStoppedEvent event){
            System.out.println("[HIVE NERVOUS SYSTEM] Clearing all Nervous Systems and Continuous Decisions");
            activeNervousSystems.forEach(HiveNervousSystem::flushDecisions);
            activeNervousSystems.clear();
            que.clear();
            unque.clear();
            addFlag = false;
            removeFlag = false;
        }
    }
}
