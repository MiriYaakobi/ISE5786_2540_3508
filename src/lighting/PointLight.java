package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a point light source (like a light bulb).
 * The light has a specific position and its intensity is attenuated by distance.
 *
 * @author Miri and Yael
 */
public class PointLight extends Light implements LightSource {

    /**
     * The position of the light source in the scene
     */
    private final Point _position;

    /**
     * Constant attenuation factor
     */
    private double _kC = 1.0;

    /**
     * Linear attenuation factor
     */
    private double _kL = 0.0;

    /**
     * Quadratic attenuation factor
     */
    private double _kQ = 0.0;

    /**
     * Constructor for PointLight.
     *
     * @param intensity the color intensity of the light
     * @param position  the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this._position = position;
    }

    /**
     * Sets the constant attenuation factor (fluent API).
     *
     * @param kC the constant factor
     * @return the PointLight object itself
     */
    public PointLight setKc(double kC) {
        this._kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor (fluent API).
     *
     * @param kL the linear factor
     * @return the PointLight object itself
     */
    public PointLight setKl(double kL) {
        this._kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor (fluent API).
     *
     * @param kQ the quadratic factor
     * @return the PointLight object itself
     */
    public PointLight setKq(double kQ) {
        this._kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double d = _position.distance(p);
        double attenuation = _kC + _kL * d + _kQ * d * d;
        // Dividing by attenuation is mathematically equal to multiplying by (1 / attenuation)
        return getIntensity().scale(1.0 / attenuation);
    }

    @Override
    public Vector getL(Point p) {
        // The direction is from the light position to the point
        return p.subtract(_position).normalize();
    }

    @Override
    public double getDistance(Point p) {
        return _position.distance(p);
    }
}