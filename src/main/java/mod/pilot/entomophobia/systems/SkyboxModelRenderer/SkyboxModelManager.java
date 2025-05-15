package mod.pilot.entomophobia.systems.SkyboxModelRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class SkyboxModelManager {
    public static void setup(){
        MinecraftForge.EVENT_BUS.addListener(SkyboxModelManager::clientTick);
    }

    private static final ArrayList<RenderPackage> vPackages = new ArrayList<>();
    public static ArrayList<RenderPackage> que = new ArrayList<>(){
        @Override
        public boolean add(RenderPackage renderPackage) {
            SkyboxModelManager.packagesInQue(); return super.add(renderPackage);
        }
        @Override
        public void add(int index, RenderPackage element) {
            SkyboxModelManager.packagesInQue(); super.add(index, element);
        }
        @Override
        public boolean addAll(Collection<? extends RenderPackage> c) {
            SkyboxModelManager.packagesInQue(); return super.addAll(c);
        }
        @Override
        public boolean addAll(int index, Collection<? extends RenderPackage> c) {
            SkyboxModelManager.packagesInQue(); return super.addAll(index, c);
        }
    };
    private static boolean packagesInQue = false;

    private static ArrayList<RenderPackage> copyList(){
        return new ArrayList<>(vPackages);
    }
    public static void packagesInQue(){packagesInQue = true;}

    public static void skyboxRenderHook(PoseStack poseStack, Matrix4f projectionMatrix,
                                        float partialTick, Camera camera, boolean isFoggy){
        if (packagesInQue) {
            vPackages.addAll(que);
            que.clear();
            packagesInQue = false;
        }

        Quaternionf orbit = new Quaternionf();
        Quaternionf rotate = new Quaternionf();
        vPackages.forEach((renderPackage -> renderPackage.render(poseStack, projectionMatrix, partialTick, camera, isFoggy, orbit, rotate)));
        vPackages.removeIf(RenderPackage::shouldRemove);
    }

    public static void clientTick(TickEvent.ClientTickEvent event){
        copyList().forEach(RenderPackage::stepKeyframes);
    }
}
