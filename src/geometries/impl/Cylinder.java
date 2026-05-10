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
     * Height of the cylinder
     */
    private final double _height;

    /**
     * Plane representing the bottom base
     */
    private final Plane _bottomBase;

    /**
     * Plane representing the top base
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

        // Initialize base planes once to satisfy the performance bonus requirement
        _bottomBase = new Plane(p0, v.scale(-1));
        _topBase = new Plane(_axis.getPoint(height), v);
    }

    /**
     * Calculates the normal to the cylinder at a given point.
     *
     * @param point the point on the cylinder surface
     * @return normalized normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Vector from bottom center to the point
        Vector p0ToPoint;
        try {
            p0ToPoint = point.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Point is exactly p0 (center of bottom base)
            return v.scale(-1);
        }

        // Projection of point on axis: t = v * (P - P0)
        double t = alignZero(v.dotProduct(p0ToPoint));

        // Case 1: Point is on the bottom base (t=0)
        if (isZero(t)) return v.scale(-1);

        // Case 2: Point is on the top base (t=height)
        if (isZero(t - _height)) return v;

        // Case 3: Point is on the side surface (delegate to Tube)
        return super.getNormal(point);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) { // Renamed and changed return type/access
        List<Intersection> result = null;

        // 1. Find intersections with the side surface (Tube)
        // CORRECTED: Call super.calcIntersectionsHelper(ray) directly to avoid infinite recursion
        List<Intersection> tubeIntersections = super.calcIntersectionsHelper(ray);
        if (tubeIntersections != null) {
            for (Intersection intr : tubeIntersections) {
                // Check if the intersection is within the cylinder's height
                double t = alignZero(_axis.direction().dotProduct(intr.point.subtract(_axis.origin())));
                if (t > 0 && t < _height) {
                    if (result == null) result = new ArrayList<>();
                    result.add(new Intersection(this, intr.point)); // Create new Intersection with this Cylinder
                }
            }
        }

        // 2. Find intersection with the bottom base (using pre-initialized plane)
        List<Intersection> bottomIntersections = intersectBase(ray, _bottomBase);
        if (bottomIntersections != null) {
            if (result == null) result = new ArrayList<>();
            for (Intersection intr : bottomIntersections) {
                result.add(new Intersection(this, intr.point)); // Create new Intersection with this Cylinder
            }
        }

        // 3. Find intersection with the top base (using pre-initialized plane)
        List<Intersection> topIntersections = intersectBase(ray, _topBase);
        if (topIntersections != null) {
            if (result == null) result = new ArrayList<>();
            for (Intersection intr : topIntersections) {
                result.add(new Intersection(this, intr.point)); // Create new Intersection with this Cylinder
            }
        }

        return result;
    }

    /**
     * Helper method to find intersection with a cylinder's base (a disk).
     * Uses pre-calculated plane to avoid temporary object creation.
     *
     * @param ray       the ray
     * @param basePlane the pre-calculated plane of the base
     * @return list of Intersection objects or null
     */
    private List<Intersection> intersectBase(Ray ray, Plane basePlane) {
        // Call calcIntersections on Plane (which will call Plane.calcIntersectionsHelper)
        List<Intersection> planeIntersections = basePlane.calcIntersections(ray);

        if (planeIntersections == null) return null;

        // A plane can only have one intersection with a ray
        Point p = planeIntersections.get(0).point;
        Point center = basePlane.getPoint();

        try {
            // Using distance squared for better performance
            if (alignZero(p.distanceSquared(center) - _radiusSquared) < 0) {
                return planeIntersections; // Return the Intersection object from the plane
            }
        } catch (IllegalArgumentException e) {
            // Point is exactly the center of the base
            return planeIntersections; // Return the Intersection object from the plane
        }

        return null;
    }

    @Override
    public String toString() {
        return "Cylinder: " + super.toString() + ", height=" + _height;
    }
}
