package mod.pilot.entomophobia.systems.GenericModelRegistry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGenericModel {
    default VertexConsumer renderBufferOf(RenderType renderType){
        return RenderBufferAccess.access().bufferSource().getBuffer(renderType);
    }
    default VertexConsumer getVertexConsumer(){
        return renderBufferOf(getRenderType());
    }
    default Model getAsModel(){
        if (this instanceof Model m) return m;
        else throw new InvalidInterfaceSubclassImplementation(this);
    }
    RenderType getRenderType();
    ResourceLocation getResourceLocation();
    ModelLayerLocation getLayerLocation();

    class InvalidInterfaceSubclassImplementation extends Error{
        public InvalidInterfaceSubclassImplementation(IGenericModel incorrectImplementation){
            super("[IGenericModel] ERROR! Incorrect implementation of IGenericModel in appended object! Ensure implementations are restricted to ONLY classes that extend net.minecraft.client.model.Model. Appended [" + incorrectImplementation + "]");
        }
    }
}
