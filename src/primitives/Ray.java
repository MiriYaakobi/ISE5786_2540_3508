package primitives;

import static primitives.Util.isZero;

/**
 * This class represents a ray in 3D space, defined by a starting point and a direction.
 * author: Miri and Yael
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
     * Calculate a point on the ray at a given distance t.
     * P = P0 + t * v
     *
     * @param t distance from the ray origin (positive, negative or zero)
     * @return the calculated point
     */
    public Point getPoint(double t) {
        // Use isZero to prevent Vector(0,0,0) exception when scaling
        return isZero(t) ? _origin : _origin.add(_direction.scale(t));
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