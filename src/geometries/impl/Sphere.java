package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * This class represents a sphere in 3D space.
 *
 * @author Miri and Yael
 */
public class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere
     */
    private final Point _center;

    /**
     * Constructor to initialize a sphere with its center point and radius.
     *
     * @param _center the center point of the sphere
     * @param radius  the radius of the sphere
     */
    public Sphere(Point _center, double radius) {
        super(radius);
        this._center = _center;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}