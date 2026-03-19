package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * This class represents a finite cylinder in 3D space.
 *
 * @author Miri and Yael
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder
     */
    private final double _height;

    /**
     * Constructor to initialize a cylinder with a given radius, central axis, and height.
     *
     * @param radius the radius of the cylinder
     * @param axis   the central axis ray of the cylinder
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this._height = height;
    }

    /**
     * Calculates the normal vector to the cylinder at a given point.
     *
     * @param point the point on the cylinder surface
     * @return the normal vector to the cylinder at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // return null; // Removed Stage 1 dummy implementation

        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Case 1: The point is exactly at the origin of the ray (bottom base center)
        if (point.equals(p0)) {
            return v.scale(-1d);
        }

        Vector p0ToPoint = point.subtract(p0);
        double t = v.dotProduct(p0ToPoint);

        // Case 2: The point is on the bottom base (projection is zero)
        if (Util.isZero(t)) {
            return v.scale(-1d);
        }

        // Case 3: The point is on the top base (projection equals height)
        if (Util.isZero(t - _height)) {
            return v;
        }

        // Case 4: The point is on the side surface (handled by Tube's logic)
        return super.getNormal(point);
    }
}