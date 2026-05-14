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
     * The center point of the sphere.
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
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Vector l;
        try {
            l = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            return List.of(new Intersection(this, ray.getPoint(_radius)));
        }

        double tm = alignZero(v.dotProduct(l));
        double dSquared = alignZero(l.lengthSquared() - tm * tm);

        if (alignZero(dSquared - _radiusSquared) >= 0) return null;

        double th = alignZero(Math.sqrt(_radiusSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 > 0 && t2 > 0)
            return List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2)));

        return t1 > 0 ? List.of(new Intersection(this, ray.getPoint(t1))) :
                (t2 > 0 ? List.of(new Intersection(this, ray.getPoint(t2))) : null);
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }
}