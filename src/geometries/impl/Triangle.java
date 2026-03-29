package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;

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
    public List<Point> findIntersections(Ray ray) {
        // Step 1: Check if the ray intersects the plane containing the triangle
        List<Point> intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        // Step 2: Check if the intersection point is inside the triangle
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Vectors from the ray origin to the vertices
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);
        Vector v3 = _vertices.get(2).subtract(p0);

        // Normals for the three "side planes" created by the edges and the ray origin
        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        // Check the sign of the dot product of the ray direction with each normal
        double s1 = alignZero(v.dotProduct(n1));
        double s2 = alignZero(v.dotProduct(n2));
        double s3 = alignZero(v.dotProduct(n3));

        // The point is inside if all dot products have the same sign (all > 0 or all < 0)
        // If any dot product is zero, the point is on an edge or vertex (return null)
        return ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0))
                ? intersections : null;
    }
}