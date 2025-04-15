package mod.pilot.entomophobia.systems.nest.hiveheart;

import com.google.common.collect.Lists;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.hiveheart.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hiveheart.decisions.FuckingExplodeWhenHurt;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

public class HiveNervousSystem {
    public ServerLevel serverLevel;
    public HiveNervousSystem(Nest nest, UUID hiveHeartUUID){
        this.nest = nest;
        this.hiveHeartUUID = hiveHeartUUID;
        this.serverLevel = nest.server;
    }
    public HiveNervousSystem(Nest nest, HiveHeartEntity hiveHeart){
        this.nest = nest;
        this.hiveHeartUUID = hiveHeart.getUUID();
        this.serverLevel = nest.server;
    }
    public HiveNervousSystem(Nest nest){
        setNest(nest);
    }

    public void setNest(Nest nest){
        this.nest = nest;
        this.hiveHeartUUID = nest.MainChamber.getHiveHeartUUID();
        this.serverLevel = nest.server;
    }
    public UUID hiveHeartUUID;
    public Nest nest;
    public @Nullable HiveHeartEntity getHiveHeart(){
        if (hiveHeartUUID != null && serverLevel != null){
            return (HiveHeartEntity)serverLevel.getEntity(hiveHeartUUID);
        } return null;
    }

    public void attachDecision(Decision d){
        filterStimulants(d.stimulantType).attachDecision(d);
    }

    public void populateDefaultDecisions(){
        attachDecision(new FuckingExplodeWhenHurt(this, StimulantType.Pain));
    }

    public void stimulate(StimulantType stimulant){
        filterStimulants(stimulant).respond();
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
        this.queued_activeDecisions.add(dc);
        this.activeQueued = true;
    }
    private final ArrayList<Decision.Continuous> queued_activeDecisions = new ArrayList<>();
    private boolean activeQueued = false;
    private final ArrayList<Decision.Continuous> activeDecisions = new ArrayList<>();
    public void tickContinuousDecisions(){
        if (activeQueued){
            activeDecisions.addAll(queued_activeDecisions);
            queued_activeDecisions.clear();
            activeQueued = false;
        }
        ArrayList<Decision.Continuous> toRemove = null;
        for(Decision.Continuous c : activeDecisions){
            if (c.activeUntil()) c.lifecycle();
            else if (toRemove == null) toRemove = Lists.newArrayList(c);
            else toRemove.add(c);
        }
        if (toRemove != null) activeDecisions.removeAll(toRemove);
    }

    private class Response {
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

        public void respond(){
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
                d.trigger();
                Decision.Continuous c;
                if ((c = d.continuous()) != null) queActiveDecision(c);
            });
        }
    }
}
