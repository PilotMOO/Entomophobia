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

@OnlyIn(Dist.CLIENT)
public class SkyboxModelManager {
    public static void setup(){
        MinecraftForge.EVENT_BUS.addListener(SkyboxModelManager::clientTick);
    }

    private static final ArrayList<RenderPackage> _vPackages = new ArrayList<>();
    public static ArrayList<RenderPackage> _que = new ArrayList<>();
    private static boolean _packagesInQue = false;

    private static ArrayList<RenderPackage> copyList(){
        return new ArrayList<>(_vPackages);
    }
    public static void packagesInQue(){_packagesInQue = true;}

    public static void SkyboxRenderHook(PoseStack poseStack, Matrix4f projectionMatrix,
                                        float partialTick, Camera camera, boolean isFoggy){
        if (_packagesInQue) {
            _vPackages.addAll(_que);
            _que.clear();
            _packagesInQue = false;
        }

        Quaternionf orbit = new Quaternionf();
        Quaternionf rotate = new Quaternionf();
        _vPackages.forEach((renderPackage -> renderPackage.render(poseStack, projectionMatrix, partialTick, camera, isFoggy, orbit, rotate)));
    }

    public static void clientTick(TickEvent.ClientTickEvent event){
        copyList().forEach(RenderPackage::stepKeyframes);
    }
}
