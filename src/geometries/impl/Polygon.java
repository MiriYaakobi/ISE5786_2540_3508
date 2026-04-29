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
 * @author Dan Zilberstein
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

        // Create the supporting plane using the first three vertices
        _plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3) return; // no need for more tests for a Triangle

        Vector n = _plane.getNormal(vertices[0]);
        Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

        // Cross product of consecutive edges determines orientation
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            // Test that the point is in the same plane as calculated originally
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
    public List<Point> findIntersections(Ray ray) {
        // Step 1: Find intersection with the plane containing the polygon
        var planeIntersections = _plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        // Step 2: Check if the intersection point is inside the polygon
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Vectors from ray origin to the last and first vertices
        Vector v1 = _vertices.get(_size - 1).subtract(p0);
        Vector v2 = _vertices.get(0).subtract(p0);

        // First sign calculation: v * (v1 x v2)
        double sign = alignZero(v.dotProduct(v1.crossProduct(v2)));
        if (isZero(sign)) return null; // Point is on an edge or vertex

        boolean isPositive = sign > 0;

        // Iterate through all other edges and check the signs
        for (int i = 1; i < _size; i++) {
            v1 = v2;
            v2 = _vertices.get(i).subtract(p0);
            sign = alignZero(v.dotProduct(v1.crossProduct(v2)));

            // If sign is 0, the point is on an edge.
            // If the sign differs from the first one, the point is outside.
            if (isZero(sign) || (sign > 0) != isPositive) return null;
        }

        // If all edges produced the same sign, the point is inside
        return planeIntersections;
    }

    @Override
    public String toString() {
        return "Polygon: vertices=" + _vertices + ", plane=" + _plane;
    }
}