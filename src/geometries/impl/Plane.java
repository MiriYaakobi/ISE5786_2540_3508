package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Plane represents a plane in 3D space.
 * author: Miri and Yael
 */
public class Plane extends geometries.api.Geometry {
    /**
     * A point on the plane
     */
    private final Point _point;
    /**
     * The normal vector to the plane
     */
    private final Vector _normal;

    /**
     * Constructor using 3 points on the plane
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        _normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructor using a point and a normal vector
     *
     * @param point  a point on the plane
     * @param normal the normal vector (will be normalized)
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    /**
     * Getter for the plane's normal
     *
     * @return normal vector
     */
    public Vector getNormal() {
        return _normal;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector n = _normal;

        // Denominator: n * v
        double nv = n.dotProduct(v);

        // Ray is parallel to the plane (nv == 0)
        if (isZero(nv)) return null;

        // Numerator: n * (Q0 - P0)
        Vector p0ToQ0;
        try {
            p0ToQ0 = _point.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray starts exactly at the plane's reference point
            return null;
        }

        double nP0Q0 = alignZero(n.dotProduct(p0ToQ0));

        // t = (n * (Q0 - P0)) / (n * v)
        double t = alignZero(nP0Q0 / nv);

        // Only return points where t > 0
        return t > 0 ? List.of(ray.getPoint(t)) : null;
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }
}