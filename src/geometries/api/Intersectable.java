package geometries.api;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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

        // --- Cache fields for shading calculations ---
        /**
         * Ray direction
         */
        public Vector v;
        /**
         * Normal vector
         */
        public Vector n;
        /**
         * Dot product of normal and ray direction
         */
        public double nv;
        /**
         * Light direction
         */
        public Vector l;
        /**
         * Dot product of normal and light direction
         */
        public double nl;

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
            return this.geometry == that.geometry && this.point.equals(that.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(geometry), point);
        }
    }

    /**
     * Public NVI method for finding intersections (Defaulting to infinity).
     *
     * @param ray the ray intersecting the geometry
     * @return list of intersections or null if none found
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Public NVI method for finding intersections within a maximum distance (Bonus).
     *
     * @param ray         the ray intersecting the geometry
     * @param maxDistance the maximum distance to search for intersections
     * @return list of intersections or null if none found
     */
    public final List<Intersection> calcIntersections(Ray ray, double maxDistance) {
        return calcIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Protected abstract helper method for the NVI pattern.
     *
     * @param ray the ray intersecting the geometry
     * @return list of intersections or null if none found
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Protected helper method for the NVI pattern with max distance.
     * Default implementation filters the results of the standard calculation.
     *
     * @param ray         the ray intersecting the geometry
     * @param maxDistance the maximum distance to search for intersections
     * @return list of intersections or null if none found
     */
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> intersections = calcIntersectionsHelper(ray);
        if (intersections == null) {
            return null;
        }

        // Filter out intersections that are further than maxDistance
        List<Intersection> filtered = intersections.stream()
                .filter(gp -> primitives.Util.alignZero(gp.point.distance(ray.origin()) - maxDistance) <= 0)
                .collect(Collectors.toList());

        return filtered.isEmpty() ? null : filtered;
    }

    /**
     * Finds all intersection points with the geometry.
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