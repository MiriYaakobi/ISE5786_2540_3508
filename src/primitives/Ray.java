package primitives;

import java.util.List;
import java.util.stream.Collectors;

import geometries.api.Intersectable;

/**
 * This class represents a ray in 3D space, defined by a starting point and a direction.
 *
 * @author Miri and Yael
 */
public class Ray {
    /**
     * Constant for the ray head displacement to avoid self-intersection.
     */
    private static final double DELTA = 0.1;

    /**
     * The starting point of the ray.
     */
    private final Point _origin;
    /**
     * The normalized direction vector of the ray.
     */
    private final Vector _direction;

    /**
     * Constructor to initialize Ray with origin point and direction vector.
     * The direction vector is normalized.
     *
     * @param origin    Starting point
     * @param direction Direction vector
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Constructor to initialize a Ray with origin point shifted along the normal vector
     * to avoid self-intersection (Refactoring step for Section 4).
     *
     * @param head      the original head point of the ray
     * @param direction the direction of the ray
     * @param normal    the normal vector at the surface
     */
    public Ray(Point head, Vector direction, Vector normal) {
        _direction = direction.normalize();
        double nv = normal.dotProduct(_direction);

        Vector normalShift = normal.scale(nv > 0 ? DELTA : -DELTA);
        _origin = head.add(normalShift);
    }

    /**
     * Getter for the origin point.
     *
     * @return origin point
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Getter for the direction vector.
     *
     * @return normalized direction vector
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Calculates a point on the ray at a distance t from the origin.
     *
     * @param t distance from the origin (positive, negative, or zero)
     * @return the point P = P0 + t * v
     */
    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException e) {
            return _origin;
        }
    }

    /**
     * Finds the closest intersection point to the ray's origin from a list of intersections.
     *
     * @param intersections list of intersections to check
     * @return the closest intersection, or null if the list is empty/null
     */
    public Intersectable.Intersection findClosestIntersection(List<Intersectable.Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }

        Intersectable.Intersection closestIntersection = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Intersectable.Intersection intersection : intersections) {
            double distanceSquared = intersection.point.distanceSquared(_origin);

            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closestIntersection = intersection;
            }
        }

        return closestIntersection;
    }

    /**
     * Finds the point closest to the ray's origin from a list of points.
     * This method uses findClosestIntersection internally.
     *
     * @param points list of points to check
     * @return the closest point, or null if the list is empty/null
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        List<Intersectable.Intersection> intersections = points.stream()
                .map(point -> new Intersectable.Intersection(null, point))
                .collect(Collectors.toList());

        Intersectable.Intersection closestIntersection = findClosestIntersection(intersections);

        return closestIntersection == null ? null : closestIntersection.point;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other) &&
                _origin.equals(other._origin) &&
                _direction.equals(other._direction);
    }

    @Override
    public String toString() {
        return "Ray: origin=" + _origin + ", direction=" + _direction;
    }
}