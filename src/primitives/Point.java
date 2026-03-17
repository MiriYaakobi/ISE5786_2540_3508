package primitives;

/**
 * This class represents a point in a 3D Cartesian coordinate system.
 *
 * @author Miri and Yael
 */
public class Point {
    /**
     * The 3D coordinates of the point
     */
    protected final Double3 _xyz;

    /**
     * The origin point (0,0,0)
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructor to initialize a Point with three coordinate values.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public Point(double x, double y, double z) {
        this._xyz = new Double3(x, y, z);
    }

    /**
     * Constructor to initialize a Point with a given Double3 object.
     *
     * @param xyz the Double3 coordinates
     */
    public Point(Double3 xyz) {
        this._xyz = xyz;
    }

    /**
     * Subtracts a given point from this point to create a new vector.
     *
     * @param other the point to subtract from this point
     * @return a new Vector representing the direction and distance from the other point to this point
     */
    public Vector subtract(Point other) {
        return new Vector(this._xyz.subtract(other._xyz));
    }

    /**
     * Adds a given vector to this point to create a new point.
     *
     * @param vector the vector to add to this point
     * @return a new Point resulting from the addition
     */
    public Point add(Vector vector) {
        return new Point(this._xyz.add(vector._xyz));
    }

    /**
     * Calculates the squared distance between this point and another given point.
     *
     * @param other the other point
     * @return the squared distance between the two points
     */
    public double distanceSquared(Point other) {
        double dx = this._xyz._d1() - other._xyz._d1();
        double dy = this._xyz._d2() - other._xyz._d2();
        double dz = this._xyz._d3() - other._xyz._d3();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the true distance between this point and another given point.
     *
     * @param other the other point
     * @return the distance between the two points
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return _xyz.equals(((Point) obj)._xyz);
    }

    @Override
    public String toString() {
        return "" + _xyz;
    }
}