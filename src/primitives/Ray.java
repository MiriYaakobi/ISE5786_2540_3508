package primitives;

import java.util.List;

/**
 * This class represents a ray in 3D space, defined by a starting point and a direction.
 *
 * @author Miri and Yael
 */
public class Ray {
    /**
     * The starting point of the ray
     */
    private final Point _origin;
    /**
     * The normalized direction vector of the ray
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
     * Finds the point closest to the ray's origin from a list of points.
     *
     * @param points list of points to check
     * @return the closest point, or null if the list is empty/null
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null) {
            return null;
        }

        Point closestPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Point point : points) {
            // Using distanceSquared for performance efficiency
            double distance = point.distanceSquared(_origin);

            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = point;
            }
        }

        return closestPoint;
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