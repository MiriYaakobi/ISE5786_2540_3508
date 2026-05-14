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
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // Step 1: Check if the ray intersects the plane containing the triangle
        List<Point> planeIntersections = _plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point intersectionPoint = planeIntersections.getFirst();

        // Step 2: Check if the intersection point is inside the triangle
        Vector v1;
        Vector v2;
        Vector v3;
        try {
            v1 = _vertices.get(0).subtract(intersectionPoint);
            v2 = _vertices.get(1).subtract(intersectionPoint);
            v3 = _vertices.get(2).subtract(intersectionPoint);
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
        Vector n1 = crossProduct1.normalize();

        Vector crossProduct2;
        try {
            crossProduct2 = v2.crossProduct(v3);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (isZero(crossProduct2.lengthSquared())) return null;
        Vector n2 = crossProduct2.normalize();

        Vector crossProduct3;
        try {
            crossProduct3 = v3.crossProduct(v1);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (isZero(crossProduct3.lengthSquared())) return null;
        Vector n3 = crossProduct3.normalize();

        Vector v = ray.direction();
        double s1 = alignZero(v.dotProduct(n1));
        double s2 = alignZero(v.dotProduct(n2));
        double s3 = alignZero(v.dotProduct(n3));

        // Create and return a new Intersection with 'this' geometry if the point is inside
        return ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0))
                ? List.of(new Intersection(this, intersectionPoint)) : null;
    }
}