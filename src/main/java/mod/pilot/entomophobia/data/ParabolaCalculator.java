package mod.pilot.entomophobia.data;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/**
 * A simple 2d parabola calculator, allows you to input values in for a, b, and c in accordance to {@code y = a(x - b)^2 + c}
 * and then calculate any matching Y values for some value X.
 * Has a static calculation method but also supports object creation for keeping track of one specific parabola across multiple calculations.
 * See constructors for details.
 * <p>Also supports simplistic 3d calculations to be used in-world. See {@link ParabolaCalculator#calculateParabolaWorldPositionFromVector(Vec3)}</p>
 */
public class ParabolaCalculator {
    /**
     * The magnitude of the parabola, how steep the drop/rise of the parabola is. If positive, parabola arcs upwards. If negative, downwards.
     * <p>Variable {@code a} as seen in {@code y = a(x - b)^2 + c}</p>
     */
    public double magnitude;
    /**
     * The X offset of the parabola, shifts the parabola on the X axis by the set value.
     * Positive shifts the parabola to the right, negative to the left.
     * <p>Variable {@code b} as seen in {@code y = a(x - b)^2 + c}</p>
     */
    public double xOffset;
    /**
     * The Y offset of the parabola, shifts the parabola on the Y axis by the set value.
     * Positive shifts upwards, negative downwards.
     * <p>Variable {@code c} as seen in {@code y = a(x - b)^2 + c}</p>
     */
    public double yOffset;
    /**
     * The "center" of the parabola, what the parabola thinks {@code [0,0,0]} is.
     * Adjust the X, Y, and Z values of this {@link Vector3d} to shift the final answers by that amount. Defaults to {@code [0,0,0]}
     * <p>Technically does the same thing as {@link ParabolaCalculator#xOffset} and {@link ParabolaCalculator#yOffset} just in Vector form,
     * however this also supports offsetting the Z value as well.</p>
     * <p>Intended to be used in in-world positional calculations, as seen in {@link ParabolaCalculator#calculateParabolaWorldPositionFromVector(Vec3)}</p>
     * <p>Is NOT used in {@link ParabolaCalculator#calculateY(double)}</p>
     */
    public Vector3d parabolaCenter;

    /**
     * Creates a new parabola calculator with the given values.
     * @param magnitude the magnitude of the parabola. The {@code a} variable as seen in {@code y = a(x - b)^2 + c}
     * @param xOffset the x offset of the parabola, shifts the whole parabola along the x-axis by the specified value.
     *               The {@code b} variable as seen in {@code y = a(x - b)^2 + c}
     * @param yOffset the y offset of the parabola, shifts the whole parabola along the y-axis by the specified value.
     *               The {@code c} variable as seen in {@code y = a(x - b)^2 + c}
     * @param parabolaCenter the "center" of the parabola, where the calculator thinks {@code [0,0,0]} is.
     *                      Offsets the {@link net.minecraft.world.phys.Vec3}
     *                      value of the {@link ParabolaCalculator#calculateParabolaWorldPositionFromVector(Vec3)} method.
     */
    public ParabolaCalculator(double magnitude, double xOffset, double yOffset, Vector3d parabolaCenter){
        this.magnitude = magnitude;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.parabolaCenter = parabolaCenter;
    }
    /**
     * Creates a new parabola calculator with only the specified magnitude. Everything else is defaulted to 0
     * @param magnitude the magnitude of the parabola
     */
    public ParabolaCalculator(double magnitude){
        this(magnitude, 0, 0, new Vector3d(0));
    }

    /**
     * Modifies the inputted X value of a given parabola calculation before calculated. Intended to be overridden in subclasses
     * or in anonymous instances of this class to change the X value dynamically.
     * <p></p>Invoked in {@link ParabolaCalculator#calculateY(double)} as an argument
     * @param x the original X value as fed in by argument parameters
     * @return the modified X value
     */
    public double modifyX(double x){
        return x;
    }
    /**
     * Modifies the magnitude of this parabola without changing the variable's value. Intended to be overridden in subclasses
     * or in anonymous instances of this class to change the magnitude dynamically without setting the variable.
     * <p></p>Invoked in {@link ParabolaCalculator#calculateY(double)} as an argument
     * @param magnitude the original variable's value to be modified
     * @return the modified value. Does not modify the variable itself.
     */
    public double modifyMagnitude(double magnitude){
        return magnitude;
    }
    /**
     * Modifies the X offset of this parabola without changing the variable's value. Intended to be overridden in subclasses
     * or in anonymous instances of this class to change the X offset dynamically without setting the variable.
     * <p></p>Invoked in {@link ParabolaCalculator#calculateY(double)} as an argument
     * @param xOffset the original variable's value to be modified
     * @return the modified value. Does not modify the variable itself
     */
    public double modifyXOffset(double xOffset){
        return xOffset;
    }
    /**
     * Modifies the Y offset of this parabola without changing the variable's value. Intended to be overridden in subclasses
     * or in anonymous instances of this class to change the YOffset dynamically without setting the variable.
     * <p></p>Invoked in {@link ParabolaCalculator#calculateY(double)} as an argument
     * @param yOffset the original variable's value to be modified
     * @return the modified value. Does not modify the variable itself
     */
    public double modifyYOffset(double yOffset){
        return yOffset;
    }

    /**
     * Shorthand, supplies the object-specific values (then modifies them as specified by the calculator)
     * into a static {@link ParabolaCalculator#calculateY(double, double, double, double)} call
     * <p>Does NOT take into account the {@link ParabolaCalculator#parabolaCenter} variable unless otherwise stated by {@code modification methods}</p>
     * <p></p>
     * {@code Modification methods}, I.E. {@link ParabolaCalculator#modifyX(double)}, {@link ParabolaCalculator#modifyMagnitude(double)},
     * {@link ParabolaCalculator#modifyXOffset(double)}, and {@link ParabolaCalculator#modifyYOffset(double)}
     * @param x the given X value to calculate the Y value from
     * @return the Y value of the specific X value in this parabola
     */
    public double calculateY(double x){
        return calculateY(modifyX(x), modifyMagnitude(magnitude), modifyXOffset(xOffset), modifyYOffset(yOffset));
    }

    /**
     * Finds the Y coordinates for an {@code X} value of {@code N} in this parabola---
     * where {@code N} is the average of the absolute values of X and Z coordinates in the supplied {@link Vec3} argument---
     * taking into account the parabola's offset.
     * Returns a new {@link Vec3} with the same X and Z values as the argument but with the computed Y value
     * @param position the original in-world position to calculate the Y value from
     * @return the new {@link Vec3} with the computed Y value. X and Z values are unchanged from the argument.
     */
    public Vec3 calculateParabolaWorldPositionFromVector(Vec3 position){
        double x = Math.abs(position.x - parabolaCenter.x);
        double z = Math.abs(position.z - parabolaCenter.z);
        double average = (x + z) / 2;
        double y = calculateY(average) + parabolaCenter.y;
        return new Vec3(position.x, y, position.z);
    }

    /**
     * Calculates the Y value from X on a given parabola with the supplied arguments
     * @param x the given X value to calculate the Y value from
     * @param a the magnitude of the parabola
     * @param b the X offset of the parabola, shifts it along the X axis
     * @param c the Y offset of the parabola, shifts it along the Y axis
     * @return the Y value calculated from the supplied X value of a parabola defined as {@code y = a(x - b)^2 + c}
     */
    public static double calculateY(double x, double a, double b, double c){
        return (a * ((x - b) * (x - b))) + c;
    }
}