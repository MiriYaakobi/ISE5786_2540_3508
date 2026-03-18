package primitives;

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
    public Point origin() { // Added missing getter
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