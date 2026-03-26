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
        // _normal = null; // Removed Stage 1 dummy implementation
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
     * @param point the point at which to calculate the normal (unused for Plane)
     * @return the normal vector to the plane
     */
    @Override
    public Vector getNormal(Point unused) {
        return _normal;
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        double nv = _normal.dotProduct(v);

        // If the ray is parallel to the plane, there are no intersections
        // (or the ray is at the plane)
        if (isZero(nv)) {
            return null;
        }

        Vector p0ToPlane = _point.subtract(p0);
        double t = alignZero(p0ToPlane.dotProduct(_normal) / nv);

        // If the ray is behind the plane, there are no intersections
        if (t <= 0)
            return null;

        // Otherwise, the ray intersects the plane at point p0 + t*v
        return List.of(p0.add(v.scale(t)));
    }
}