package geometries.api;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors; // Added for Collectors.toList()

import primitives.Material; // Added import for Material
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
     * Finds all intersection points between a given ray and the geometry,
     * returning a list of Intersection objects.
     * This method is final and calls the protected abstract helper method.
     *
     * @param ray the ray to check for intersections
     * @return a list of Intersection objects, or null if there are no intersections
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Protected abstract helper method to find all intersection points between a given ray and the geometry.
     * Concrete geometries must implement this method.
     *
     * @param ray the ray to check for intersections
     * @return a list of Intersection objects, or null if there are no intersections
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);


    /**
     * Find all intersection points between a given ray and the geometry,
     * returning a list of Point objects.
     * This method is final and uses the calcIntersections method and Stream API.
     *
     * @param ray the ray to check for intersections
     * @return a list of intersection points, or null if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .collect(Collectors.toList()); // Changed to collect(Collectors.toList()) for compatibility
    }

    /**
     * Nested static class representing an intersection point with a geometric body.
     * This is a PDS (Plain Data Structure) class.
     */
    public static final class Intersection {
        /**
         * The geometric body that was intersected.
         */
        public final Geometry geometry;
        /**
         * The point of intersection.
         */
        public final Point point;
        /**
         * The material properties of the intersected geometry.
         */
        public final Material material; // Added material field

        /**
         * Constructor to initialize an Intersection object.
         *
         * @param geometry the geometric body
         * @param point    the intersection point
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            // Initialize material from geometry, or to a new default Material if geometry is null
            this.material = (geometry == null) ? new Material() : geometry.getMaterial();
        }

        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    ", material=" + material + // Added material to toString
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Intersection that = (Intersection) o;
            // Comparing geometry and point using Objects.equals for null-safety and proper object comparison
            return Objects.equals(geometry, that.geometry) &&
                    Objects.equals(point, that.point) &&
                    Objects.equals(material, that.material); // Added material to equals
        }

        @Override
        public int hashCode() {
            return Objects.hash(geometry, point, material); // Added material to hashCode
        }
    }
}
