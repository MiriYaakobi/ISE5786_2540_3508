package primitives;

/**
 * This class represents a vector in 3D space, which is a direction and magnitude.
 * Inherits from Point.
 *
 * @author Miri and Yael
 */
public class Vector extends Point {

    /**
     * Vector representing the X axis
     */
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    /**
     * Vector representing the Y axis
     */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    /**
     * Vector representing the Z axis
     */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructor to initialize a Vector with three coordinate values.
     * Throws an exception if the zero vector is created.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @throws IllegalArgumentException if the created vector is the zero vector
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /**
     * Constructor to initialize a Vector with a given Double3 object.
     * Throws an exception if the zero vector is created.
     *
     * @param xyz the Double3 coordinates
     * @throws IllegalArgumentException if the created vector is the zero vector
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /**
     * Adds a given vector to this vector to create a new vector.
     *
     * @param v the vector to add
     * @return a new Vector resulting from the vector addition
     */
    public Vector add(Vector v) {
        return new Vector(_xyz.add(v._xyz));
    }

    /**
     * Scales the vector by a scalar number.
     *
     * @param scalar the scaling factor
     * @return a new Vector scaled by the given number
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Calculates the dot product (scalar product) of this vector and another vector.
     *
     * @param v the other vector
     * @return the dot product value
     */
    public double dotProduct(Vector v) {
        return _xyz._d1() * v._xyz._d1() +
                _xyz._d2() * v._xyz._d2() +
                _xyz._d3() * v._xyz._d3();
    }

    /**
     * Calculates the cross product (vector product) of this vector and another vector.
     *
     * @param v the other vector
     * @return a new Vector that is orthogonal to both vectors
     */
    public Vector crossProduct(Vector v) {
        double ax = _xyz._d1();
        double ay = _xyz._d2();
        double az = _xyz._d3();
        double bx = v._xyz._d1();
        double by = v._xyz._d2();
        double bz = v._xyz._d3();

        return new Vector(
                ay * bz - az * by,
                az * bx - ax * bz,
                ax * by - ay * bx
        );
    }

    /**
     * Calculates the squared length of the vector.
     *
     * @return the squared length
     */
    public double lengthSquared() {
        double xx = _xyz._d1() * _xyz._d1();
        double yy = _xyz._d2() * _xyz._d2();
        double zz = _xyz._d3() * _xyz._d3();
        return xx + yy + zz;
    }

    /**
     * Calculates the exact length of the vector.
     *
     * @return the length of the vector
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector (changes its length to 1) and returns it as a new vector.
     *
     * @return a new normalized Vector
     */
    public Vector normalize() {
        double len = length();
        return new Vector(_xyz.divide(len)); // Double3 often has reduce or scale(1/len)
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Vector) && super.equals(obj);
    }
}