package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * Class Sphere represents a sphere in 3D space.
 *
 * @author Miri and Yael
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

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) { // Renamed and changed return type/access
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Vector from ray origin to sphere center: L = O - P0
        Vector l;
        try {
            l = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // Ray starts at the center of the sphere (P0 == O)
            // The intersection point is at distance R: P = P0 + R*v
            return List.of(new Intersection(this, ray.getPoint(_radius))); // Changed to Intersection
        }

        // Projection of l on the ray: tm = v * l
        double tm = alignZero(v.dotProduct(l));

        // Distance squared from center to the ray's line: d^2 = |l|^2 - tm^2
        double dSquared = alignZero(l.lengthSquared() - tm * tm);

        // If distance squared is greater than or equal to radius squared, no intersection
        // Tangent points (dSquared == _radiusSquared) are excluded as per instructions
        if (alignZero(dSquared - _radiusSquared) >= 0) return null;

        // Half distance between intersection points: th = sqrt(r^2 - d^2)
        double th = alignZero(Math.sqrt(_radiusSquared - dSquared));

        // Intersection distances from ray origin: t1, t2 = tm +/- th
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // Return only points that are in the ray's direction (t > 0)
        // Since th > 0, t1 is always smaller than t2, ensuring distance order
        if (t1 > 0 && t2 > 0)
            return List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2))); // Changed to Intersection

        return t1 > 0 ? List.of(new Intersection(this, ray.getPoint(t1))) : // Changed to Intersection
                (t2 > 0 ? List.of(new Intersection(this, ray.getPoint(t2))) : null); // Changed to Intersection
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }
}
