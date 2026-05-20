package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Util;
import primitives.Vector;

/**
 * Represents an enhanced Spot Light source with narrow beam capability (bonus).
 * Extends PointLight and overrides all setters to maintain Fluent API for SpotLight.
 *
 * @author Miri and Yael
 */
public class SpotLight extends PointLight {
    /**
     * The direction vector of the spot light beam
     */
    private final Vector direction;

    /**
     * The concentration factor for narrowing the light beam (default is 1.0)
     */
    private double narrowBeam = 1.0;

    /**
     * Constructor for SpotLight.
     *
     * @param intensity the intensity color of the light
     * @param position  the position of the light source
     * @param direction the direction vector of the light beam
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Setter for the narrow beam concentration factor (Bonus).
     *
     * @param narrowBeam the exponent factor for narrowing the beam
     * @return the SpotLight object itself (Fluent API)
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

    // --- Overriding Parent Setters to maintain SpotLight type in Fluent API ---

    @Override
    public SpotLight setKc(double kc) {
        super.setKc(kc);
        return this;
    }

    @Override
    public SpotLight setKl(double kl) {
        super.setKl(kl);
        return this;
    }

    @Override
    public SpotLight setKq(double kq) {
        super.setKq(kq);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        // Vector from the light source position to the target point
        Vector l = getL(p);

        // Dot product between the spot direction and the vector to the point
        double cosAngle = Util.alignZero(direction.dotProduct(l));

        // If the dot product is negative, the point is behind the light beam
        if (cosAngle <= 0) {
            return Color.BLACK;
        }

        // Raise the factor to the power of narrowBeam for the bonus implementation
        double factor = Math.pow(cosAngle, narrowBeam);

        // Scale the base point light intensity by the calculated spot factor
        return super.getIntensity(p).scale(factor);
    }
}