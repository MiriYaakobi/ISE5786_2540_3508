package geometries.impl;

import java.util.List;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 *
 * @author Dan Zilberstein, Miri and Yael
 */
public class Polygon extends Geometry {
    /**
     * Ordered list of polygon vertices
     */
    protected final List<Point> _vertices;
    /**
     * Plane containing the polygon
     */
    protected final Plane _plane;
    /**
     * Number of vertices
     */
    private final int _size;

    /**
     * Constructs a convex polygon from ordered vertices.
     *
     * @param vertices polygon vertices in edge order
     * @throws IllegalArgumentException if the vertices do not form a valid convex polygon
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        _vertices = List.of(vertices);
        _size = vertices.length;

        _plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3) return;

        Vector n = _plane.getNormal(vertices[0]);
        Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");

            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // Step 1: Find intersection with the plane containing the polygon using the original method
        List<Point> planeIntersections = _plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point intersectionPoint = planeIntersections.getFirst();

        // Step 2: Check if the intersection point is inside the polygon
        Vector n = _plane.getNormal(null);

        Vector v1;
        Vector v2;
        try {
            v1 = _vertices.get(_size - 1).subtract(intersectionPoint);
            v2 = _vertices.getFirst().subtract(intersectionPoint);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Vector crossProduct1;
        try {
            crossProduct1 = v1.crossProduct(v2);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (isZero(crossProduct1.lengthSquared())) return null;

        double sign = alignZero(n.dotProduct(crossProduct1));
        if (isZero(sign)) return null;

        boolean isPositive = sign > 0;

        for (int i = 1; i < _size; i++) {
            v1 = v2;
            try {
                v2 = _vertices.get(i).subtract(intersectionPoint);
            } catch (IllegalArgumentException e) {
                return null;
            }

            Vector crossProductI;
            try {
                crossProductI = v1.crossProduct(v2);
            } catch (IllegalArgumentException e) {
                return null;
            }
            if (isZero(crossProductI.lengthSquared())) return null;

            sign = alignZero(n.dotProduct(crossProductI));

            if (isZero(sign) || (sign > 0) != isPositive) return null;
        }

        // Return a new Intersection object containing this polygon and the intersected point
        return List.of(new Intersection(this, intersectionPoint));
    }

    @Override
    public String toString() {
        return "Polygon: vertices=" + _vertices + ", plane=" + _plane;
    }
}