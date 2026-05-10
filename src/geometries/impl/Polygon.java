package geometries.impl;

import java.util.List;

import geometries.api.Geometry;
import geometries.api.Intersectable.Intersection; // Added import
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
    protected List<Intersection> calcIntersectionsHelper(Ray ray) { // Renamed and changed return type/access
        // Step 1: Find intersection with the plane containing the polygon
        var planeIntersections = _plane.calcIntersections(ray); // Call calcIntersections on Plane
        if (planeIntersections == null) return null;

        // A polygon can only have one intersection with its plane
        Point intersectionPoint = planeIntersections.get(0).point;

        // Step 2: Check if the intersection point is inside the polygon
        // Using the "same side" test for convex polygons
        Vector n = _plane.getNormal(null); // Normal of the polygon's plane

        // Check the first edge
        Vector v1;
        Vector v2;
        try {
            v1 = _vertices.get(_size - 1).subtract(intersectionPoint);
            v2 = _vertices.get(0).subtract(intersectionPoint);
        } catch (IllegalArgumentException e) {
            return null; // intersectionPoint is a vertex of the polygon
        }
        
        Vector crossProduct1 = null;
        try {
            crossProduct1 = v1.crossProduct(v2);
        } catch (IllegalArgumentException e) {
            return null; // v1 and v2 are parallel or one is zero vector, point is on edge/vertex
        }
        if (isZero(crossProduct1.lengthSquared())) return null; // Cross product is zero vector

        double sign = alignZero(n.dotProduct(crossProduct1));

        if (isZero(sign)) return null; // Point is on an edge or vertex (consider it outside)
        boolean isPositive = sign > 0;

        // Check all other edges
        for (int i = 1; i < _size; i++) {
            v1 = v2; // Previous v2 becomes current v1
            try {
                v2 = _vertices.get(i).subtract(intersectionPoint); // New v2 to current vertex
            } catch (IllegalArgumentException e) {
                return null; // intersectionPoint is a vertex of the polygon
            }
            
            Vector crossProductI = null;
            try {
                crossProductI = v1.crossProduct(v2);
            } catch (IllegalArgumentException e) {
                return null; // v1 and v2 are parallel or one is zero vector, point is on edge/vertex
            }
            if (isZero(crossProductI.lengthSquared())) return null; // Cross product is zero vector

            sign = alignZero(n.dotProduct(crossProductI));

            // If sign is 0, the point is on an edge.
            // If the sign differs from the first one, the point is outside.
            if (isZero(sign) || (sign > 0) != isPositive) return null;
        }

        // If all edges produced the same sign, the point is inside
        return planeIntersections; // Return the Intersection object from the plane
    }

    @Override
    public String toString() {
        return "Polygon: vertices=" + _vertices + ", plane=" + _plane;
    }
}
