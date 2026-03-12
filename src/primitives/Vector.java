package primitives;

/**
 * This class represents a vector in 3D space, which is a direction and magnitude.
 * Inherits from Point.
 * * @author Miri and Yael
 */
public class Vector extends Point {

    /**
     * Constructor to initialize a Vector with three coordinate values.
     * Throws an exception if the zero vector is created.
     * * @param x the X coordinate
     *
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @throws IllegalArgumentException if the created vector is the zero vector
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (Double3.ZERO.equals(this.xyz)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /**
     * Constructor to initialize a Vector with a given Double3 object.
     * Throws an exception if the zero vector is created.
     * * @param xyz the Double3 coordinates
     *
     * @throws IllegalArgumentException if the created vector is the zero vector
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (Double3.ZERO.equals(this.xyz)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /**
     * Adds a given vector to this vector to create a new vector.
     * * @param v the vector to add
     *
     * @return a new Vector resulting from the vector addition
     */
    public Vector add(Vector v) {
        return new Vector(this.xyz.add(v.xyz));
    }

    /**
     * Scales the vector by a scalar number.
     * * @param scalar the scaling factor
     *
     * @return a new Vector scaled by the given number
     */
    public Vector scale(double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    /**
     * Calculates the dot product (scalar product) of this vector and another vector.
     * * @param v the other vector
     *
     * @return the dot product value
     */
    public double dotProduct(Vector v) {
        return this.xyz._d1() * v.xyz._d1() +
                this.xyz._d2() * v.xyz._d2() +
                this.xyz._d3() * v.xyz._d3();
    }

    /**
     * Calculates the cross product (vector product) of this vector and another vector.
     * * @param v the other vector
     *
     * @return a new Vector that is orthogonal to both vectors
     */
    public Vector crossProduct(Vector v) {
        double u1 = this.xyz._d1();
        double u2 = this.xyz._d2();
        double u3 = this.xyz._d3();
        double v1 = v.xyz._d1();
        double v2 = v.xyz._d2();
        double v3 = v.xyz._d3();

        return new Vector(
                u2 * v3 - u3 * v2,
                u3 * v1 - u1 * v3,
                u1 * v2 - u2 * v1
        );
    }

    /**
     * Calculates the squared length of the vector.
     * * @return the squared length
     */
    public double lengthSquared() {
        return this.dotProduct(this);
    }

    /**
     * Calculates the exact length of the vector.
     * * @return the length of the vector
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector (changes its length to 1) and returns it as a new vector.
     * * @return a new normalized Vector
     */
    public Vector normalize() {
        double len = length();
        return new Vector(this.xyz.divide(len));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "->" + super.toString();
    }
}