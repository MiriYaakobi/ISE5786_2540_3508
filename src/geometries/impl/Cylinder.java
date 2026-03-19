package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * Class Cylinder represents a finite cylinder in 3D space.
 * Inherits from Tube and adds height and base covers.
 *
 * @author Miri and Yael
 */
public class Cylinder extends Tube {
    /**
     * Height of the cylinder
     */
    private final double _height;

    /**
     * Constructor to initialize a cylinder.
     *
     * @param radius radius of the cylinder
     * @param axis   central axis ray
     * @param height height of the cylinder
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this._height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Vector from base center to the point
        Vector p0ToPoint = point.equals(p0) ? null : point.subtract(p0);

        // Case 1: Point is at the center of the bottom base (t = 0)
        if (p0ToPoint == null) return v.scale(-1);

        // Calculate projection t
        double t = v.dotProduct(p0ToPoint);

        // Case 2: Point is on the bottom base (t = 0)
        if (Util.isZero(t)) return v.scale(-1);

        // Case 3: Point is on the top base (t = height)
        if (Util.isZero(t - _height)) return v;

        // Case 4: Point is on the side surface - delegate to Tube
        return super.getNormal(point);
    }
}