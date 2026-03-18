package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * This class represents a plane in 3D space.
 * It inherits from the Geometry abstract class.
 *
 * @author Miri and Yael
 */
public class Plane extends Geometry {
    /**
     * A point on the plane used as a reference point
     */
    private final Point _point;

    /**
     * The normal vector to the plane
     */
    private final Vector _normal;

    /**
     * Constructor to initialize a plane from three points.
     * At this stage (Stage 1), it only stores the first point as a reference
     * and sets the normal to null.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;
        _normal = null; // As per Stage 1 instructions
    }

    /**
     * Constructor to initialize a plane from a point and a normal vector.
     * The normal vector is normalized.
     *
     * @param point  a point on the plane
     * @param normal the normal vector to the plane
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();
    }

    /**
     * Implementation of the getNormal method from Geometry.
     *
     * @param point the point at which to calculate the normal (unused for Plane)
     * @return the normal vector to the plane
     */
    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }
}