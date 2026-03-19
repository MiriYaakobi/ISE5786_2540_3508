package geometries.impl;

import primitives.Point;

/**
 * Class Triangle represents a two-dimensional triangle in 3D space.
 * Inherits from Polygon and uses its implementations.
 *
 * @author Miri and Yael
 */
public class Triangle extends Polygon {

    /**
     * Constructor to initialize a triangle with three vertices.
     *
     * @param p1 the first vertex
     * @param p2 the second vertex
     * @param p3 the third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }
}