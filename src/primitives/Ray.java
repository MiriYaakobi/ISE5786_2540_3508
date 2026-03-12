package primitives;

/**
 * This class represents a ray in 3D space, defined by an origin point and a direction vector.
 *
 * @author Miri and Yael
 */
public class Ray {
    /**
     * The origin point of the ray
     */
    private final Point origin;
    /**
     * The direction vector of the ray, must be normalized
     */
    private final Vector direction;

    /**
     * Constructor to initialize a Ray with an origin point and a direction vector.
     * The direction vector is normalized before being stored.
     *
     * @param origin    the origin point
     * @param direction the direction vector
     */
    public Ray(Point origin, Vector direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    /**
     * Returns the direction vector of the ray.
     *
     * @return the normalized direction vector
     */
    public Vector direction() {
        return direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray other = (Ray) obj;
        return this.origin.equals(other.origin) && this.direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Ray:" + origin + direction;
    }
}