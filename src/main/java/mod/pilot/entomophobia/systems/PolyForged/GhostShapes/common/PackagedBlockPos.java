package mod.pilot.entomophobia.systems.PolyForged.GhostShapes.common;

import net.minecraft.core.BlockPos;

public record PackagedBlockPos(int x, int y, int z) {
    public static PackagedBlockPos Pack(BlockPos toPack) {
        return new PackagedBlockPos(toPack.getX(), toPack.getY(), toPack.getZ());
    }
    public static BlockPos Unpack(PackagedBlockPos toUnpack) {
        return new BlockPos(toUnpack.x, toUnpack.y, toUnpack.z);
    }
    public static final PackagedBlockPos ZERO = new PackagedBlockPos(0, 0, 0);
}
