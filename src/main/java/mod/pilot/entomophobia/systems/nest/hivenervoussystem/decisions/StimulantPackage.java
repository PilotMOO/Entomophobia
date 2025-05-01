package mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record StimulantPackage(@Nullable Vec3 from, @Nullable Entity source, boolean serverSide) {
    public static StimulantPackage entity(@NotNull Entity source){
        return new StimulantPackage(source.position(), source, !source.level().isClientSide());
    }
    public static StimulantPackage positional(Vec3 from, boolean serverSide){
        return new StimulantPackage(from, null, serverSide);
    }
    public static StimulantPackage empty(boolean serverSide){
        return new StimulantPackage(null, null, serverSide);
    }
}
