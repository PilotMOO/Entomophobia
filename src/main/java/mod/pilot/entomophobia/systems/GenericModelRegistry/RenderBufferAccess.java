package mod.pilot.entomophobia.systems.GenericModelRegistry;

import net.minecraft.client.renderer.RenderBuffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBufferAccess {
    private static RenderBuffers _bufferAccess;
    public static void registerAccess(RenderBuffers buffers){
        if (_bufferAccess != null) throw new RenderBufferAccessAlreadyDefined();
        else _bufferAccess = buffers;
    }
    public static RenderBuffers access(){return _bufferAccess;}

    private static class RenderBufferAccessAlreadyDefined extends Error{
        public RenderBufferAccessAlreadyDefined(){
            super("[RENDER BUFFER ACCESS] ERROR! Attempted to assign a new RenderBuffers to the RenderBuffer accessor despite an accessor already being defined!");
        }
    }
}
