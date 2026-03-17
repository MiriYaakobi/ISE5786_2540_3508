package geometries.impl;

import geometries.api.Geometry;

/**
 * This abstract class serves as a base class for all radial geometries (geometries with a radius).
 *
 * @author Miri and Yael
 */
public abstract class RadialGeometry extends Geometry {
    /**
     * The radius of the geometry
     */
    protected final double _radius;
    /**
     * The squared radius of the geometry (for performance optimization)
     */
    protected final double _radiusSquared;

    /**
     * Constructor to initialize a radial geometry with a given radius.
     * It initializes both the radius and the squared radius fields.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        this._radius = radius;
        this._radiusSquared = radius * radius;
    }
}