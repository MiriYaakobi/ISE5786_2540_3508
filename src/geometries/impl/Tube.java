package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * This class represents a tube (an infinite cylinder) in 3D space.
 *
 * @author Miri and Yael
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis ray of the tube
     */
    protected final Ray _axis;

    /**
     * Constructor to initialize a tube with a given radius and central axis.
     *
     * @param radius the radius of the tube
     * @param _axis  the central axis ray of the tube
     */
    public Tube(double radius, Ray _axis) {
        super(radius);
        this._axis = _axis;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}