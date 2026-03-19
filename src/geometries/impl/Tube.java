package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * This class represents a tube (an infinite cylinder) in 3D space.
 *
 * @author Miri and Yael
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis ray of the tube
     */
    protected final Ray _axis;

    /**
     * Constructor to initialize a tube with a given radius and central axis.
     *
     * @param radius the radius of the tube
     * @param axis   the central axis ray of the tube
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this._axis = axis;
    }

    /**
     * Calculates the normal vector to the tube at a given point.
     *
     * @param point the point on the tube surface
     * @return the normal vector to the tube at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // return null; // Removed Stage 1 dummy implementation

        // Vector from the ray's origin to the given point
        Vector p0ToPoint = point.subtract(_axis.origin());

        // Calculate the projection of p0ToPoint on the ray's direction
        double t = _axis.direction().dotProduct(p0ToPoint);

        // If the projection is exactly zero, the point is directly above the origin
        // Otherwise, calculate the center point on the axis O = P0 + t * v
        Point centerOnAxis = Util.isZero(t) ? _axis.origin() : _axis.origin().add(_axis.direction().scale(t));

        // The normal is the vector from the calculated center to the given point, normalized
        return point.subtract(centerOnAxis).normalize();
    }
}