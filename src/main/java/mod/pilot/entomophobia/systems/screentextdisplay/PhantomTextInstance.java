package mod.pilot.entomophobia.systems.screentextdisplay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Predicate;

public abstract class PhantomTextInstance extends TextInstance{
    public static Mother create(int recursivePhantomCount){
        return create("", recursivePhantomCount);
    }
    public static Mother create(String text, int recursivePhantomCount){
        return create(text, null, null, recursivePhantomCount);
    }
    public static Mother create(String text, @Nullable String prepend, @Nullable String append, int recursivePhantomCount){
        return new Mother(text, prepend, append, recursivePhantomCount);
    }
    private PhantomTextInstance(String text, @Nullable String prepend, @Nullable String append) {
        super(text, prepend, append);
    }

    public int phantomLayers;
    public PhantomTextInstance setPhantom(Daughter daughter){
        this.trailingPhantom = daughter;
        return this;
    }
    public @Nullable PhantomTextInstance.Daughter trailingPhantom;
    public PhantomTextInstance withAlphaDifference(int aDiff){
        this.alphaDifference = aDiff;
        return this;
    }
    public int alphaDifference = 0;
    public PhantomTextInstance withIncrementalSize(float incSize){
        this.incrementalSizeScale = incSize;
        return this;
    }
    public float incrementalSizeScale = 1f;
    public PhantomTextInstance updateRate(int ticksPerUpdate){
        this.updateFrequency = ticksPerUpdate;
        return this;
    }
    public int updateFrequency = 1;
    protected int updateCount = 0;

    protected final void ActuallyRender(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        super.Render(gui, guiGraphics, partialTick, screenWidth, screenHeight);
    }

    public static class Mother extends PhantomTextInstance{
        protected Mother(String text, @Nullable String prepend, @Nullable String append, int recursivePhantomCount) {
            super(text, prepend, append);
            this.phantomLayers = recursivePhantomCount;
            if (recursivePhantomCount > 0) this.trailingPhantom = new Daughter(this, recursivePhantomCount - 1);
        }
        public @Nullable Predicate<Daughter> daughtersShouldRender;
        public boolean ShouldDaughterRender(Daughter daughter){
            return daughtersShouldRender == null || daughtersShouldRender.test(daughter);
        }
        @Override
        public void Render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
            if (trailingPhantom == null) ActuallyRender(gui, guiGraphics, partialTick, screenWidth, screenHeight);
            else trailingPhantom.FindRootEndThenRenderAll(gui, guiGraphics, partialTick, screenWidth, screenHeight);
        }

        @Override
        public void Tick() {
            super.Tick();
            if (trailingPhantom != null){
                trailingPhantom.CopyFrom(this, true, true);
            }
        }
    }
    public static class Daughter extends PhantomTextInstance{
        protected Daughter(PhantomTextInstance parent, int recursiveCount) {
            super(parent.text, parent.prepend, parent.append);
            this.parent = parent;
            this.phantomLayers = recursiveCount;
            if (recursiveCount > 0) this.trailingPhantom = new Daughter(this, recursiveCount - 1);

            this.withAlphaDifference(parent.alphaDifference)
                    .withIncrementalSize(parent.incrementalSizeScale)
                    .updateRate(parent.updateFrequency);

            this.age = -2;
        }
        private @Nullable Mother mother;
        public @NotNull Mother getMother(){
            if (mother != null) return mother;
            PhantomTextInstance pText = parent;
            while (pText instanceof Daughter d) pText = d.parent;
            return mother = (Mother)pText;
        }
        public final @NotNull PhantomTextInstance parent;
        public void FindRootEndThenRenderAll(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
            if (this.trailingPhantom == null){
                RenderRecursiveInverse(gui, guiGraphics, partialTick, screenWidth, screenHeight);
            } else this.trailingPhantom.FindRootEndThenRenderAll(gui, guiGraphics, partialTick, screenWidth, screenHeight);
        }
        public void RenderRecursiveInverse(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
            ActuallyRender(gui, guiGraphics, partialTick, screenWidth, screenHeight);
            if (parent instanceof Daughter c) c.RenderRecursiveInverse(gui, guiGraphics, partialTick, screenWidth, screenHeight);
            else if (parent instanceof Mother m) m.ActuallyRender(gui, guiGraphics, partialTick, screenWidth, screenHeight);
        }

        public void CopyFrom(PhantomTextInstance from, boolean pushOldToChildren, boolean recursive){
            if (pushOldToChildren && trailingPhantom != null){
                trailingPhantom.CopyFrom(this, recursive, recursive);
            }
            if (++updateCount >= updateFrequency) {
                updateCount = 0;
                this.at(from.x, from.y)
                        .withColor(new Color(from.color.getRed(), from.color.getBlue(), from.color.getGreen(),
                                Math.min(Math.max(from.color.getAlpha() - from.alphaDifference, 0), 255)))
                        .withFont(from.font)
                        .Shadowed(from.shadow)
                        .ofSize(from.size * from.incrementalSizeScale)
                        .withShaking(from.shakingStrength);

                if (this.age == -2) this.age = -1;
                else this.age = getMother().ShouldDaughterRender(this) ? 1 : -1;
            }
        }
    }
}
