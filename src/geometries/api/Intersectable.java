package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * This abstract class serves as the base class for all geometric bodies.
 *
 * @author Miri and Yael
 */
public abstract class Intersectable {
    /**
     * Default constructor for Intersectable.
     */
    protected Intersectable() {
    }

    /**
     * Find all intersection points between a given ray and the geometry.
     *
     * @param ray the ray to check for intersections
     * @return a list of intersection points, or null if there are no intersections
     */
    public abstract List<Point> findIntersections(Ray ray);
}