package mod.pilot.entomophobia.systems.PolyForged.Shapes.AbstractShapes;

import mod.pilot.entomophobia.systems.PolyForged.common.WorldShapeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class FlatShapeGenerator extends ShapeGenerator{
    protected FlatShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, boolean replaceableOnly, int Xsize, int Ysize, int Zsize, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, replaceableOnly);
        int X = 1, Y = 1, Z = 1;
        switch (excluded.ordinal()) {
            case 0 -> {
                Y = Ysize;
                Z = Zsize;
            }
            case 1 -> {
                X = Xsize;
                Z = Zsize;
            }
            case 2 -> {
                X = Xsize;
                Y = Ysize;
            }
            default -> X = Y = Z = 0;
        }
        this.Xsize = X; this.Ysize = Y; this.Zsize = Z;
        ExcludedAxis = excluded;
    }

    protected FlatShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, int maxHardness, int Xsize, int Ysize, int Zsize, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, maxHardness);
        int X = 1, Y = 1, Z = 1;
        switch (excluded.ordinal()) {
            case 0 -> {
                Y = Ysize;
                Z = Zsize;
            }
            case 1 -> {
                X = Xsize;
                Z = Zsize;
            }
            case 2 -> {
                X = Xsize;
                Y = Ysize;
            }
            default -> X = Y = Z = 0;
        }
        this.Xsize = X; this.Ysize = Y; this.Zsize = Z;
        ExcludedAxis = excluded;
    }

    protected FlatShapeGenerator(ServerLevel server, double buildSpeed, List<BlockState> blockTypes, Vec3 pos, @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist, int Xsize, int Ysize, int Zsize, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, whitelist, blacklist);
        int X = 1, Y = 1, Z = 1;
        switch (excluded.ordinal()) {
            case 0 -> {
                Y = Ysize;
                Z = Zsize;
            }
            case 1 -> {
                X = Xsize;
                Z = Zsize;
            }
            case 2 -> {
                X = Xsize;
                Y = Ysize;
            }
            default -> X = Y = Z = 0;
        }
        this.Xsize = X; this.Ysize = Y; this.Zsize = Z;
        ExcludedAxis = excluded;
    }

    public final int Xsize;
    public final int Ysize;
    public final int Zsize;
    public final WorldShapeManager.Axis ExcludedAxis;
}
