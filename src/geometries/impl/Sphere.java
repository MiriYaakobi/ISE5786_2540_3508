package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * Class Sphere represents a sphere in 3D space.
 * author: Miri and Yael
 */
public class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere
     */
    private final Point _center;

    /**
     * Constructor to initialize a sphere with its center point and radius.
     *
     * @param center center point
     * @param radius radius value
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    /**
     * Getter for center point
     *
     * @return center point
     */
    public Point getCenter() {
        return _center;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Vector from ray origin to sphere center: L = O - P0
        Vector l;
        try {
            l = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // Ray starts at the center of the sphere (P0 == O)
            // The intersection point is at distance R: P = P0 + R*v
            return List.of(ray.getPoint(_radius));
        }

        // Projection of l on the ray: tm = v * l
        double tm = alignZero(v.dotProduct(l));

        // Distance squared from center to the ray's line: d^2 = |l|^2 - tm^2
        double dSquared = alignZero(l.lengthSquared() - tm * tm);

        // If distance squared is greater than or equal to radius squared, no intersection
        // Tangent points (dSquared == _radiusSquared) are excluded
        if (alignZero(dSquared - _radiusSquared) >= 0) return null;

        // Half distance between intersection points: th = sqrt(r^2 - d^2)
        double th = alignZero(Math.sqrt(_radiusSquared - dSquared));

        // Intersection distances from ray origin: t1, t2 = tm +/- th
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // Return only points that are in the ray's direction (t > 0)
        if (t1 > 0 && t2 > 0)
            return List.of(ray.getPoint(t1), ray.getPoint(t2));

        if (t1 > 0) return List.of(ray.getPoint(t1));
        if (t2 > 0) return List.of(ray.getPoint(t2));

        return null;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", " + super.toString();
    }
}