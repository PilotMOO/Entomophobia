package mod.pilot.entomophobia.systems.EventStart;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.data.EntomoDataManager;
import mod.pilot.entomophobia.data.clientsyncing.EventStartSyncer;
import mod.pilot.entomophobia.systems.GenericModelRegistry.GenericModelHub;
import mod.pilot.entomophobia.systems.GenericModelRegistry.IGenericModel;
import mod.pilot.entomophobia.systems.GenericModelRegistry.models.CarrioniteModel;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.RenderPackage;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.LifetimeKeyframe;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.OffsetKeyframe;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.OrbitKeyframe;
import mod.pilot.entomophobia.systems.SkyboxModelRenderer.keyframe.RotationKeyframe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public abstract class EventStart {
    private EventStart(){}

    private static final Random random = new Random();

    public static void setup(){Server.setup(); Client.setup();}
    public static void unpackFromData(CompoundTag tag) {
        eventOver = tag.getBoolean(DATA_EVENT_OVER);
        eventStarted = tag.getBoolean(DATA_EVENT_STARTED);
        changeEventState(tag.getInt(DATA_FADE_IN), FadeState.fromInt(tag.getInt(DATA_FADE_STATE)));
    }
    public static void packToData(CompoundTag tag) {
        tag.putBoolean(DATA_EVENT_OVER, eventOver);
        tag.putBoolean(DATA_EVENT_STARTED, eventStarted);
        tag.putInt(DATA_FADE_IN, fade);
        tag.putInt(DATA_FADE_STATE, fadeState.ordinal());
    }
    private static void dirty() {
        if (Entomophobia.activeData != null) Entomophobia.activeData.setDirty();

    }

    private static final String DATA_PREPEND = "EventStart_";
    private static String createDataTag(String ID) {
        return DATA_PREPEND + ID;
    }

    private static final String DATA_EVENT_STARTED = createDataTag("EVENT-STARTED");
    public static boolean eventStarted = false;
    public static void setEventStarted(boolean flag) {
        eventStarted = flag; dirty();
    }
    private static final String DATA_EVENT_OVER = createDataTag("EVENT-OVER");
    public static boolean eventOver = false;
    public static void setEventOver(boolean flag){
        eventOver = flag; dirty();
    }

    private static final String DATA_FADE_IN = createDataTag("FADE-IN");
    public static int fade = -1;
    public static final String DATA_FADE_STATE = createDataTag("FADE-STATE");
    public static FadeState fadeState = FadeState.INACTIVE;
    public enum FadeState {
        INACTIVE, FADE_IN(200), HOLDING(600), FADE_OUT(400);
        public final int fadeTime;

        FadeState() {
            this(-1);
        }

        FadeState(int fadeTime) {
            this.fadeTime = fadeTime;
        }

        private static final int count = FadeState.values().length;

        public FadeState next() {
            return values()[(this.ordinal() + 1) % count];
        }

        public static FadeState fromInt(int i) {
            return values()[i % count];
        }
    }
    public static void changeEventState(int ticks, FadeState newState) {
        fade = ticks; fadeState = newState; dirty();
    }
    public static void changeEventState(FadeState newState) {changeEventState(newState.fadeTime, newState);}
    public static void resetEventState() {changeEventState(-1, FadeState.INACTIVE);}

    protected static void tickEventFade(){
        if (fadeState != FadeState.INACTIVE && fade != -1 && --fade == 0) {
            //System.out.println("Changing state to [" + fadeState.next() + "]");
            changeEventState(fadeState.next());
        } /*else {
            if (fadeState == FadeState.INACTIVE) System.out.println("[EVENT INACTIVE]");
            else if (fade != -1) {
                System.out.println("Ticking... fade left: [" + fade + "]");
            }
        }*/
    }

    private static final int doomsDay = Config.SERVER.doomsday.get();
    private static final float dayPercentage = 0.75f;
    private static boolean isItDoomsday() {
        return !eventOver && EntomoDataManager.getDaysElapsed() == doomsDay;
    }
    private static boolean eventActive(boolean ignoreStarted){
        return !eventOver && (eventStarted || ignoreStarted) && isItDoomsday() && EntomoDataManager.getDayPercentage() > dayPercentage;
    }

    public static EventStartSyncer.ServerSyncPacket buildPacket(){
        return new EventStartSyncer.ServerSyncPacket(eventOver, eventStarted, fade, fadeState);
    }

    public static class Server {
        private Server(){}

        public static void setup() {
            MinecraftForge.EVENT_BUS.addListener(EventStart.Server::eventWatcher);
            MinecraftForge.EVENT_BUS.addListener(EventStart.Server::noSleeping);
        }

        public static @SubscribeEvent void eventWatcher(TickEvent.ServerTickEvent event) {
            if (eventOver) return;
            FadeState oldState = fadeState;
            switch (fadeState){
                case INACTIVE -> handleEventINACTIVE(event);
                case FADE_IN -> handleEventFADE_IN(event);
                case HOLDING -> handleEventHOLDING(event);
                case FADE_OUT -> handleEventFADE_OUT(event);
            }
            if (fadeState != oldState) {
                if (oldState == FadeState.FADE_OUT) setEventOver(true);
                sendChangesToClient(event.getServer().overworld());
            }
        }
        private static void handleEventINACTIVE(TickEvent.ServerTickEvent event){
            if (eventActive(true)) {
                eventSetup(event.getServer().overworld());
            }
        }
        private static void handleEventFADE_IN(TickEvent.ServerTickEvent event){
            tickEventFade();
        }
        private static void handleEventHOLDING(TickEvent.ServerTickEvent event){
            tickEventFade();
        }
        private static void handleEventFADE_OUT(TickEvent.ServerTickEvent event){
            tickEventFade();
        }

        public static @SubscribeEvent void noSleeping(SleepingTimeCheckEvent event) {
            if (isItDoomsday()) {
                event.setResult(Event.Result.DENY);
                Player p = event.getEntity();
                p.displayClientMessage(getEventSleepDisabledTranslatable(), false);
                p.playNotifySound(SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 0.25f, 2f);
            }
        }

        private static void eventSetup(ServerLevel server) {
            setEventStarted(true);
            changeEventState(FadeState.FADE_IN);
            sendChangesToClient(server);
        }
        private static void sendChangesToClient(ServerLevel server){
            EventStartSyncer.syncAllClients(buildPacket(), server);
        }

        private static MutableComponent getEventSleepDisabledTranslatable() {
            return Component.translatable(switch (random.nextInt(7)) {
                case 1 -> "entomophobia.event.sleep_disabled1";
                case 2 -> "entomophobia.event.sleep_disabled2";
                case 3 -> "entomophobia.event.sleep_disabled3";
                case 4 -> "entomophobia.event.sleep_disabled4";
                case 5 -> "entomophobia.event.sleep_disabled5";
                case 6 -> "entomophobia.event.sleep_disabled6";
                default -> "entomophobia.event.sleep_disabled0";
            });
        }
    }

    public static class Client {
        private Client(){}
        private static FadeState oldState;
        public static IGenericModel carrioniteModel;
        public static RenderPackage cPackage;

        public static void setup(){
            MinecraftForge.EVENT_BUS.addListener(Client::eventWatcher);
        }

        public static void update(EventStartSyncer.ServerSyncPacket packet){
            eventOver = packet.over();
            eventStarted = packet.started();
            fade = packet.fade();
            fadeState = packet.state();
        }

        public static @SubscribeEvent void eventWatcher(TickEvent.ClientTickEvent event) {
            if (eventOver) return;
            tickEventFade();
            if (oldState != fadeState){
                if (fadeState == FadeState.FADE_IN){
                    createCarrioniteModel();
                }
            }
            oldState = fadeState;
        }

        private static void createCarrioniteModel() {
            carrioniteModel = new CarrioniteModel(GenericModelHub.ModelSet.bakeLayer(CarrioniteModel.LAYER_LOCATION));
            cPackage = RenderPackage.create(carrioniteModel);
            cPackage.offset(0.5, 2, 0.5).orbit(180, 0).rotate(0, -90, 0);
            cPackage.addKeyframe(new LifetimeKeyframe(1200))
                    /*.addKeyframe(new RotationKeyframe(0, 0, 400, 1600))*/
                    .addKeyframe(new OrbitKeyframe(-4000, 0, 1000))
                    .addKeyframe(new OffsetKeyframe(6, 0.5, 0, 800))
                    .addKeyframe(new OffsetKeyframe(6, -4, 0, 400){
                        @Override
                        public boolean active() {
                            return age > 500;
                        }
                    });
            cPackage.que();
        }
    }
}