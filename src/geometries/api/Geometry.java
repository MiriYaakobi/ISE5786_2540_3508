package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * This abstract class serves as the base class for all geometric bodies.
 *
 * @author Miri and Yael
 */
public abstract class Geometry {

    /**
     * Default constructor for Geometry
     */
    public Geometry() {
    }

    /**
     * Calculates the normal vector to the geometry at a given point.
     *
     * @param point the point on the geometry surface
     * @return the normal vector to the geometry at the given point
     */
    public abstract Vector getNormal(Point point);
}