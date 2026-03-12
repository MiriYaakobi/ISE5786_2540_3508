package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * This class represents a flat 2D surface (plane) in 3D space.
 *
 * @author Miri and Yael
 */
public class Plane extends Geometry {
    /**
     * A point on the plane
     */
    private final Point q;
    /**
     * The normal vector to the plane
     */
    private final Vector normal;

    /**
     * Constructor to initialize a plane from three points on its surface.
     * At this stage, it only stores the first point.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        this.q = p1;
        this.normal = null; // As per instructions for stage 1
    }

    /**
     * Constructor to initialize a plane from a point and a normal vector.
     * The given normal vector is normalized before being stored.
     *
     * @param point  the point on the plane
     * @param normal the normal vector
     */
    public Plane(Point point, Vector normal) {
        this.q = point;
        this.normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return this.normal;
    }
}