package geometries.impl;

import java.util.List;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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
     * Getter for the point on the plane.
     *
     * @return the reference point
     */
    public Point getPoint() {
        return _point;
    }

    /**
     * Constructor to initialize a plane from three points.
     * The normal is calculated using the cross product of two vectors formed by these points.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;

        // Calculate two vectors on the plane
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        // Calculate the normal vector using cross product and normalize it
        _normal = v1.crossProduct(v2).normalize();
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
     * @param unused the point at which to calculate the normal (unused for Plane)
     * @return the normal vector to the plane
     */
    @Override
    public Vector getNormal(Point unused) {
        return _normal;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector n = _normal;

        // Denominator: n * v
        double nv = alignZero(n.dotProduct(v));

        // If ray is parallel to the plane (n * v == 0), there are no intersections
        if (isZero(nv)) {
            return null;
        }

        // Numerator: n * (Q0 - P0)
        Vector p0_q0;
        try {
            p0_q0 = _point.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // Ray starts at the plane's reference point (P0 == Q0)
            // The distance t is zero, but the origin point should not be included
            return null;
        }

        double nQminusP = alignZero(n.dotProduct(p0_q0));

        // t = (n * (Q0 - P0)) / (n * v)
        double t = alignZero(nQminusP / nv);

        // There is intersection only if it is in the direction of the ray (t > 0)
        // Using ternary operator as per KISS principle for simple conditions
        return t <= 0 ? null : List.of(ray.getPoint(t));
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }
}