package mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes;

import mod.pilot.entomophobia.systems.PolyForged.utility.GeneratorBlockPacket;
import mod.pilot.entomophobia.systems.PolyForged.utility.WorldShapeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class FlatShapeGenerator extends ShapeGenerator{
    protected FlatShapeGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                 boolean replaceableOnly, int Xsize, int Ysize, int Zsize, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, replaceableOnly);
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

    protected FlatShapeGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos, boolean hydrophobic,
                                 int maxHardness, int Xsize, int Ysize, int Zsize, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, maxHardness);
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

    protected FlatShapeGenerator(ServerLevel server, double buildSpeed, GeneratorBlockPacket blockTypes, Vec3 pos,  boolean hydrophobic,
                                 @Nullable List<BlockState> whitelist, @Nullable List<BlockState> blacklist,
                                 int Xsize, int Ysize, int Zsize, WorldShapeManager.Axis excluded) {
        super(server, buildSpeed, blockTypes, pos, hydrophobic, whitelist, blacklist);
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
