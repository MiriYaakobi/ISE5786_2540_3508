package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Class Sphere represents a sphere in 3D space.
 * Inherits from {@link RadialGeometry}.
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
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return List.of();
    }
}