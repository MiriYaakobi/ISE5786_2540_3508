package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Cylinder represents a finite cylinder in 3D space.
 * Inherits from Tube and adds height and base covers.
 *
 * @author Miri and Yael
 */
public class Cylinder extends Tube {
    /**
     * Height of the cylinder.
     */
    private final double _height;

    /**
     * Plane representing the bottom base.
     */
    private final Plane _bottomBase;

    /**
     * Plane representing the top base.
     */
    private final Plane _topBase;

    /**
     * Constructor to initialize a cylinder.
     * Pre-calculates the base planes to avoid temporary objects during intersection calculation.
     *
     * @param radius radius of the cylinder
     * @param axis   central axis ray
     * @param height height of the cylinder
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this._height = height;

        Vector v = _axis.direction();
        Point p0 = _axis.origin();

        _bottomBase = new Plane(p0, v.scale(-1));
        _topBase = new Plane(_axis.getPoint(height), v);
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        Vector p0ToPoint;
        try {
            p0ToPoint = point.subtract(p0);
        } catch (IllegalArgumentException e) {
            return v.scale(-1);
        }

        double t = alignZero(v.dotProduct(p0ToPoint));

        if (isZero(t)) return v.scale(-1);
        if (isZero(t - _height)) return v;

        return super.getNormal(point);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersection> intersections = new ArrayList<>();

        List<Intersection> tubeIntersections = super.calcIntersectionsHelper(ray);
        if (tubeIntersections != null) {
            for (Intersection intr : tubeIntersections) {
                double t = alignZero(_axis.direction().dotProduct(intr.point.subtract(_axis.origin())));
                if (t > 0 && t < _height) {
                    intersections.add(new Intersection(this, intr.point));
                }
            }
        }

        List<Intersection> bottomIntersections = intersectBase(ray, _bottomBase);
        if (bottomIntersections != null) {
            intersections.addAll(bottomIntersections);
        }

        List<Intersection> topIntersections = intersectBase(ray, _topBase);
        if (topIntersections != null) {
            intersections.addAll(topIntersections);
        }

        return intersections.isEmpty() ? null : intersections;
    }

    /**
     * Helper method to find intersection with a cylinder's base (a disk).
     * Uses pre-calculated plane to avoid temporary object creation.
     *
     * @param ray       the intersecting ray
     * @param basePlane the pre-calculated plane of the base
     * @return list of Intersection objects or null
     */
    private List<Intersection> intersectBase(Ray ray, Plane basePlane) {
        List<Intersection> planeIntersections = basePlane.calcIntersections(ray);
        if (planeIntersections == null) return null;

        Point p = planeIntersections.getFirst().point;
        Point center = basePlane.getPoint();

        try {
            if (alignZero(p.distanceSquared(center) - _radiusSquared) < 0) {
                return List.of(new Intersection(this, p));
            }
        } catch (IllegalArgumentException e) {
            return List.of(new Intersection(this, p));
        }

        return null;
    }

    @Override
    public String toString() {
        return "Cylinder: " + super.toString() + ", height=" + _height;
    }
}