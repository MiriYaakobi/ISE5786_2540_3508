package geometries.api;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import primitives.Material;
import primitives.Point;
import primitives.Ray;

/**
 * Common interface for all graphic objects that can be intersected by a ray.
 * Uses the NVI (Non-Virtual Interface) pattern for intersection calculations.
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
     * Helper class representing an intersection point between a ray and a geometry.
     * This is a PDS (Plain Data Structure) class.
     */
    public static final class Intersection {
        /**
         * The geometry that was intersected.
         */
        public final Geometry geometry;

        /**
         * The point of intersection.
         */
        public final Point point;

        /**
         * The material of the intersected geometry.
         */
        public final Material material;

        /**
         * Constructor for Intersection data.
         *
         * @param geometry the intersected geometry
         * @param point    the intersection point
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = (geometry == null) ? new Material() : geometry.getMaterial();
        }

        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    ", material=" + material +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Intersection that = (Intersection) o;

            // Geometry comparison is by reference (==) as per project requirements
            return this.geometry == that.geometry && this.point.equals(that.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(geometry), point);
        }
    }

    /**
     * Public NVI method for finding intersections.
     *
     * @param ray the ray intersecting the geometry
     * @return list of intersections (Intersection objects) or null if none found
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Protected abstract helper method for the NVI pattern.
     * Each geometry must implement its specific intersection logic here.
     *
     * @param ray the ray intersecting the geometry
     * @return list of intersections (Intersection objects) or null if none found
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Finds all intersection points with the geometry.
     * This is a wrapper for backward compatibility using the new NVI mechanism.
     *
     * @param ray the ray intersecting the geometry
     * @return list of intersection points or null if none found
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .collect(Collectors.toList());
    }
}