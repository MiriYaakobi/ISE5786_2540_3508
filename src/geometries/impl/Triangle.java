package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) { // Renamed and changed return type/access
        // Step 1: Check if the ray intersects the plane containing the triangle
        List<Intersection> planeIntersections = _plane.calcIntersections(ray); // Call calcIntersections on Plane
        if (planeIntersections == null) return null;

        // A triangle can only have one intersection with its plane
        Point intersectionPoint = planeIntersections.getFirst().point; // Replaced get(0) with getFirst()

        // Step 2: Check if the intersection point is inside the triangle
        // Vectors from the intersection point to the vertices
        Vector v1;
        Vector v2;
        Vector v3;
        try {
            v1 = _vertices.get(0).subtract(intersectionPoint);
            v2 = _vertices.get(1).subtract(intersectionPoint);
            v3 = _vertices.get(2).subtract(intersectionPoint);
        } catch (IllegalArgumentException e) {
            return null; // intersectionPoint is a vertex of the triangle
        }

        // Normals for the three "side planes" created by the edges and the intersection point
        Vector crossProduct1; // Removed redundant null initializer
        try {
            crossProduct1 = v1.crossProduct(v2);
        } catch (IllegalArgumentException e) {
            return null; // v1 and v2 are parallel or one is zero vector, point is on edge/vertex
        }
        if (isZero(crossProduct1.lengthSquared())) return null;
        Vector n1 = crossProduct1.normalize();

        Vector crossProduct2; // Removed redundant null initializer
        try {
            crossProduct2 = v2.crossProduct(v3);
        } catch (IllegalArgumentException e) {
            return null; // v2 and v3 are parallel or one is zero vector, point is on edge/vertex
        }
        if (isZero(crossProduct2.lengthSquared())) return null;
        Vector n2 = crossProduct2.normalize();

        Vector crossProduct3; // Removed redundant null initializer
        try {
            crossProduct3 = v3.crossProduct(v1);
        } catch (IllegalArgumentException e) {
            return null; // v3 and v1 are parallel or one is zero vector, point is on edge/vertex
        }
        if (isZero(crossProduct3.lengthSquared())) return null;
        Vector n3 = crossProduct3.normalize();

        // Check the sign of the dot product of the ray direction with each normal
        // The ray direction 'v' is used here, as per the original logic, to determine if the point is "in front" of the side planes
        Vector v = ray.direction();
        double s1 = alignZero(v.dotProduct(n1));
        double s2 = alignZero(v.dotProduct(n2));
        double s3 = alignZero(v.dotProduct(n3));

        // The point is inside if all dot products have the same sign (all > 0 or all < 0)
        // If any dot product is zero, the point is on an edge or vertex (return null)
        return ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0))
                ? planeIntersections : null; // Return the Intersection object from the plane
    }
}
