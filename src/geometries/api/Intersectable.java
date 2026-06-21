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
 * Updated for Stage 10 to support Axis-Aligned Bounding Box (AABB) acceleration.
 *
 * @author Miri and Yael
 */
public abstract class Intersectable {

    /**
     * Global static switch to enable or disable Bounding Box (AABB) acceleration.
     */
    private static boolean s_aabbEnabled = false;

    // Axis-Aligned Bounding Box coordinates
    protected double _minX = Double.NEGATIVE_INFINITY;
    protected double _maxX = Double.POSITIVE_INFINITY;
    protected double _minY = Double.NEGATIVE_INFINITY;
    protected double _maxY = Double.POSITIVE_INFINITY;
    protected double _minZ = Double.NEGATIVE_INFINITY;
    protected double _maxZ = Double.POSITIVE_INFINITY;

    /**
     * Default constructor for Intersectable.
     */
    protected Intersectable() {
    }

    /**
     * Enables or disables AABB acceleration globally.
     *
     * @param enable true to enable, false to disable
     */
    public static void setAabbEnabled(boolean enable) {
        s_aabbEnabled = enable;
    }

    /**
     * Checks if AABB acceleration is enabled globally.
     *
     * @return true if enabled, false otherwise
     */
    public static boolean isAabbEnabled() {
        return s_aabbEnabled;
    }

    /**
     * Getter for minimum X bound.
     *
     * @return minimum X coordinate
     */
    public double getMinX() {
        return _minX;
    }

    /**
     * Getter for maximum X bound.
     *
     * @return maximum X coordinate
     */
    public double getMaxX() {
        return _maxX;
    }

    /**
     * Getter for minimum Y bound.
     *
     * @return minimum Y coordinate
     */
    public double getMinY() {
        return _minY;
    }

    /**
     * Getter for maximum Y bound.
     *
     * @return maximum Y coordinate
     */
    public double getMaxY() {
        return _maxY;
    }

    /**
     * Getter for minimum Z bound.
     *
     * @return minimum Z coordinate
     */
    public double getMinZ() {
        return _minZ;
    }

    /**
     * Getter for maximum Z bound.
     *
     * @return maximum Z coordinate
     */
    public double getMaxZ() {
        return _maxZ;
    }

    /**
     * Helper method to verify if a ray intersects this geometry's bounding box.
     * Implements Kay-Kajiya / Smits t-interval intersection algorithm.
     *
     * @param ray the ray to check
     * @return true if the ray hits the box or if bounds are infinite, false otherwise
     */
    public boolean intersectBox(Ray ray) {
        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        double oX = ray.origin().getX();
        double dX = ray.direction().getX();
        if (dX != 0) {
            double t1 = (_minX - oX) / dX;
            double t2 = (_maxX - oX) / dX;
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        } else if (oX < _minX || oX > _maxX) return false;

        double oY = ray.origin().getY();
        double dY = ray.direction().getY();
        if (dY != 0) {
            double t1 = (_minY - oY) / dY;
            double t2 = (_maxY - oY) / dY;
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        } else if (oY < _minY || oY > _maxY) return false;

        double oZ = ray.origin().getZ();
        double dZ = ray.direction().getZ();
        if (dZ != 0) {
            double t1 = (_minZ - oZ) / dZ;
            double t2 = (_maxZ - oZ) / dZ;
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        } else if (oZ < _minZ || oZ > _maxZ) return false;

        return tMax >= tMin && tMax >= 0;
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
        if (s_aabbEnabled && !intersectBox(ray)) {
            return null;
        }
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