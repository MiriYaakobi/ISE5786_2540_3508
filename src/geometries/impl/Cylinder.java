package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * This class represents a finite cylinder in 3D space.
 *
 * @author Miri and Yael
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder
     */
    private final double _height;

    /**
     * Constructor to initialize a cylinder with a given radius, central axis, and height.
     *
     * @param radius the radius of the cylinder
     * @param axis   the central axis ray of the cylinder
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this._height = height;
    }

    /**
     * Calculates the normal vector to the cylinder at a given point.
     *
     * @param point the point on the cylinder surface
     * @return the normal vector to the cylinder at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}