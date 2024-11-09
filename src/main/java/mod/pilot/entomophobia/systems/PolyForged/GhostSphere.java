package mod.pilot.entomophobia.systems.PolyForged;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GhostSphere {
    public GhostSphere(Vec3 position, int radius){
        this.position = position;
        this.radius = radius;
    }
    public final Vec3 position;
    public final double radius;
    public boolean isGhost(BlockPos bPos){
        return radius > Mth.sqrt((float)((bPos.getX() - position.x) * (bPos.getX() - position.x)
                                + (bPos.getY() - position.y) * (bPos.getY() - position.y)
                                + (bPos.getZ() - position.z) * (bPos.getZ() - position.z)));
    }
}
