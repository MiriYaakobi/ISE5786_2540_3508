package primitives;


public class Ray {
    private final Point _origin; // Changed from origin to _origin
    private final Vector _direction; // Changed from direction to _direction

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