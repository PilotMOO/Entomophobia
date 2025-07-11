package mod.pilot.entomophobia.data;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import oshi.util.tuples.Pair;

/**
 * A simple 2d parabola calculator, allows you to input values in for a, b, and c in accordance to {@code y = a(x - b)^2 + c}
 * and then calculate any matching Y values for some value X.
 * Has a static calculation method but also supports object creation for keeping track of one specific parabola across multiple calculations.
 * See constructors for details.
 * <p>Also supports simplistic 3d calculations to be used in-world. See {@link ParabolaCalculator#calculateParabolaYValueFromWorldPosition(Vec3)}</p>
 */
public class ParabolaCalculator {
    /**
     * The slope of the parabola, how steep the drop/rise of the parabola is. If positive, parabola arcs upwards. If negative, downwards.
     * <p>Variable {@code a} as seen in {@code y = a(x - b)^2 + c}</p>
     */
    public double slope;
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
     * <p>Intended to be used in in-world positional calculations, as seen in {@link ParabolaCalculator#calculateParabolaYValueFromWorldPosition(Vec3)}</p>
     * <p>Is NOT used in {@link ParabolaCalculator#calculateY(double)}</p>
     */
    public Vector3d parabolaCenter;

    /**
     * Setter, sets the {@link ParabolaCalculator#parabolaCenter} variable to the argument.
     * @param center the {@link Vector3d} to set the {@link ParabolaCalculator#parabolaCenter} variable to
     */
    public void setParabolaCenter(Vector3d center){
        this.parabolaCenter = center;
    }

    /**
     * Overloaded method, translates the {@link Vec3} argument into a {@link Vector3d} object
     * then sets the {@link ParabolaCalculator#parabolaCenter} to it
     * @param vec3 the {@link Vec3} to translate into a {@link Vector3d} then set
     */
    public void setParabolaCenter(Vec3 vec3){
        setParabolaCenter(new Vector3d(vec3.x, vec3.y, vec3.z));
    }

    /**
     * Creates a new parabola calculator with the given values.
     * @param slope the slope of the parabola. The {@code a} variable as seen in {@code y = a(x - b)^2 + c}
     * @param xOffset the x offset of the parabola, shifts the whole parabola along the x-axis by the specified value.
     *               The {@code b} variable as seen in {@code y = a(x - b)^2 + c}
     * @param yOffset the y offset of the parabola, shifts the whole parabola along the y-axis by the specified value.
     *               The {@code c} variable as seen in {@code y = a(x - b)^2 + c}
     * @param parabolaCenter the "center" of the parabola, where the calculator thinks {@code [0,0,0]} is.
     *                      Offsets the {@link net.minecraft.world.phys.Vec3}
     *                      value of the {@link ParabolaCalculator#calculateParabolaYValueFromWorldPosition(Vec3)} method.
     */
    public ParabolaCalculator(double slope, double xOffset, double yOffset, Vector3d parabolaCenter){
        this.slope = slope;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.parabolaCenter = parabolaCenter;
    }

    /**
     * Overloaded method, creates a new parabola calculator with the given values, and translates the given {@link Vec3} into a {@link Vector3d}
     * @param slope the slope of the parabola. The {@code a} variable as seen in {@code y = a(x - b)^2 + c}
     * @param xOffset the x offset of the parabola, shifts the whole parabola along the x-axis by the specified value.
     *               The {@code b} variable as seen in {@code y = a(x - b)^2 + c}
     * @param yOffset the y offset of the parabola, shifts the whole parabola along the y-axis by the specified value.
     *               The {@code c} variable as seen in {@code y = a(x - b)^2 + c}
     * @param parabolaCenter the "center" of the parabola, where the calculator thinks {@code [0,0,0]} is.
     *                      Offsets the {@link net.minecraft.world.phys.Vec3}
     *                      value of the {@link ParabolaCalculator#calculateParabolaYValueFromWorldPosition(Vec3)} method.
     */
    public ParabolaCalculator(double slope, double xOffset, double yOffset, Vec3 parabolaCenter) {
        this(slope, xOffset, yOffset, new Vector3d(parabolaCenter.x, parabolaCenter.y, parabolaCenter.z));
    }

    /**
     * Creates a new parabola calculator with only the specified slope. Everything else is defaulted to 0
     * @param slope the slope of the parabola. The {@code a} variable as seen in {@code y = a(x - b)^2 + c}
     */
    public ParabolaCalculator(double slope){
        this(slope, 0, 0, new Vector3d(0));
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
     * Modifies the inputted Y value of a given parabola calculation before calculated. Intended to be overridden in subclasses
     * or in anonymous instances of this class to change the Y value dynamically.
     * <p></p>Invoked in {@link ParabolaCalculator#calculateX(double)} as an argument
     * @param y the original Y value as fed in by argument parameters
     * @return the modified Y value
     */
    public double modifyY(double y){
        return y;
    }
    /**
     * Modifies the slope of this parabola without changing the variable's value. Intended to be overridden in subclasses
     * or in anonymous instances of this class to change the slope dynamically without setting the variable.
     * <p></p>Invoked in {@link ParabolaCalculator#calculateY(double)} as an argument
     * @param slope the original variable's value to be modified
     * @return the modified value. Does not modify the variable itself.
     */
    public double modifyslope(double slope){
        return slope;
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
     * or in anonymous instances of this class to change the Y offset dynamically without setting the variable.
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
     * <p>Does NOT take into account the {@link ParabolaCalculator#parabolaCenter} variable unless otherwise stated by the relevant {@code modification methods},
     * I.E. {@link ParabolaCalculator#modifyX(double)}, {@link ParabolaCalculator#modifyslope(double)},
     * {@link ParabolaCalculator#modifyXOffset(double)}, and {@link ParabolaCalculator#modifyYOffset(double)}</p>
     * @param x the given X value to calculate the Y value from
     * @return the Y value calculated from the supplied X value of a parabola defined as {@code y = a(x - b)^2 + c}
     */
    public double calculateY(double x){
        return calculateY(modifyX(x), modifyslope(slope), modifyXOffset(xOffset), modifyYOffset(yOffset));
    }

    /**
     * Calculates the Y value from X on a given parabola with the supplied arguments
     * @param x the given X value to calculate the Y value from
     * @param a the slope of the parabola
     * @param b the X offset of the parabola, shifts it along the x-axis
     * @param c the Y offset of the parabola, shifts it along the y-axis
     * @return the Y value calculated from the supplied X value of a parabola defined as {@code y = a(x - b)^2 + c}
     */
    public static double calculateY(double x, double a, double b, double c){
        return (a * ((x - b) * (x - b))) + c;
    }

    /**
     * Shorthand, supplies the object-specific values (then modifies them as specified by the calculator)
     * into a static {@link ParabolaCalculator#calculateX(double, double, double, double)} call
     * <p>Does NOT take into account the {@link ParabolaCalculator#parabolaCenter} variable unless otherwise stated by the relevant {@code modification methods},
     * I.E. {@link ParabolaCalculator#modifyY(double)}, {@link ParabolaCalculator#modifyslope(double)},
     * {@link ParabolaCalculator#modifyXOffset(double)}, and {@link ParabolaCalculator#modifyYOffset(double)}</p>
     * @param y the given Y value to calculate the pair of X values from
     * @return a pair of X values, ordered positive first, that solves the equation {@code x = √[(y-c) / a] + b},
     * where y is equal to the argument value of the argument y,
     * or null if the y value is invalid for the given parabola
     */
    public @Nullable Pair<Double, Double> calculateX(double y){
        return calculateX(modifyY(y), modifyslope(slope), modifyXOffset(xOffset), modifyYOffset(yOffset));
    }

    /**
     * Calculates and returns a pair of the two X positions for a given Y position on a given parabola with the supplied arguments,
     * or null if no matching X values exist
     * @param y the Y value to locate the X values for
     * @param a the slope of the parabola
     * @param b the X offset of the parabola, shifts it along the x-axis
     * @param c the Y offset of the parabola, shifts it along the y-axis
     * @return a pair of X values, ordered positive first, that solves the equation {@code x = √[(y-c) / a] + b} (the equation of a parabola solved for X),
     * where y is equal to the argument value of the argument y,
     * or null if the y value is invalid for the given parabola
     */
    public static @Nullable Pair<Double, Double> calculateX(double y, double a, double b, double c){
        double sqrtPos = Math.sqrt((y - c) / a);
        if (Double.isNaN(sqrtPos)){return null;}
        double sqrtNeg = -sqrtPos;
        return new Pair<>(sqrtPos + b, sqrtNeg + b);
    }

    /**
     * Finds the Y coordinates for a {@code X} value of {@code N} in this parabola---
     * where {@code N} is the average of the absolute values of X and Z coordinates in the supplied {@link Vec3} argument---
     * taking into account the parabola's offset.
     * Returns a new {@link Vec3} with the same X and Z values as the argument but with the computed Y value
     * @param position the original in-world position to calculate the Y value from
     * @return the new {@link Vec3} with the computed Y value. X and Z values are unchanged from the argument.
     */
    public Vec3 calculateParabolaYValueFromWorldPosition(Vec3 position){
        double x = Math.abs(position.x - parabolaCenter.x);
        double z = Math.abs(position.z - parabolaCenter.z);
        double average = (x + z) / 2;
        double y = calculateY(average) + parabolaCenter.y;
        return new Vec3(position.x, y, position.z);
    }

    //Kind of broken, needs work...
    /**
     * Finds the X coordinates for a given {@code Y} value in this parabola, taking into account the parabola's offset,
     * then finds the ratio between X and Z in the {@link Vec3} argument and uses that to calculate the relevant Z values,
     * before packaging it into a {@link Pair} of {@link Vec3} objects and returning.
     * Returns {@code null} if the inputted Y value is invalid for this parabola
     * @param position the original in-world position to calculate the X and Z values from
     * @return a {@link Pair} of {@link Vec3} values containing the world coordinates of the matching X values--- split between itself and Z
     * in accordance to the ratio between X and Z in the original argument--- of the Y value of the argument.
     * The returned objects' Y values are equivalent to that of the argument's.
     */
    public @Nullable Pair<Vec3, Vec3> calculateParabolaXValuesFromWorldPosition(Vec3 position){
        Pair<Double, Double> solutions = calculateX(position.y - parabolaCenter.y);
        if (solutions == null) return null;
        Pair<Double, Double> value1 = locateSecondaryCirclePosition(position.x - parabolaCenter.x,
                position.z - parabolaCenter.z, solutions.getA());
        Pair<Double, Double> value2 = locateSecondaryCirclePosition(position.x - parabolaCenter.x,
                position.z - parabolaCenter.z, solutions.getB());
        return new Pair<>(new Vec3(value1.getA() + parabolaCenter.x, position.y, value1.getB() + parabolaCenter.z),
                new Vec3(value2.getA() + parabolaCenter.x, position.y, value2.getB() + parabolaCenter.z));
    }

    /**
     * Locates a pair of coordinates on the rim of a circle with radius {@code r2} centered on {@code [0,0]}
     * whose ratio between the coordinate's values is equal to that of the argument's coordinates (also centered on {@code [0,0]})
     * @param x1 the X coordinate of the initial position and circle
     * @param z1 the Z coordinate of the initial position and circle
     * @param r2 the radius of the second circle on which the answer's coordinates would fall on
     *          in accordance to the equation {@code a^2 + b^2 = c^2} where {@code a} and {@code b}
     *          are equal to the pair of answers and {@code c} is equivalent to argument {@code r2}
     * @return a pair of coordinates whose ratio is equivalent to the ratio between the arguments {@code x1} and {@code x2}
     */
    private static Pair<Double, Double> locateSecondaryCirclePosition(double x1, double z1, double r2){
        double r1 = Math.sqrt((x1 * x1) + (z1 * z1));
        double R = r1/r2;
        return new Pair<>(x1 * R, z1 * R);
    }

    @Override
    public String toString() {
        return "Parabola Calculator [slope: " + slope + ", xOffset: " + xOffset + ", yOffset: " + yOffset + ", parabolaCenter: " + parabolaCenter + "]";
    }
}