package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * Class Tube represents an infinite tube (cylinder) in 3D space.
 *
 * @author Miri and Yael
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis ray of the tube
     */
    protected final Ray _axis;

    /**
     * Constructor to initialize a tube.
     *
     * @param radius the radius of the tube
     * @param axis   the central axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this._axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Vector from ray origin to the point
        Vector p0ToPoint = point.subtract(p0);

        // Calculate projection t = v * (p - p0)
        double t = v.dotProduct(p0ToPoint);

        // If t is zero, the center is exactly p0
        if (Util.isZero(t)) return p0ToPoint.normalize();

        // Otherwise, center O = p0 + t*v
        Point o = p0.add(v.scale(t));
        return point.subtract(o).normalize();
    }
}