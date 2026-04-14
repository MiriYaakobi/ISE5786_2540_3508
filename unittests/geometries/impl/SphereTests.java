package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link geometries.impl.Sphere} class.
 * author: Miri and Yael
 */
class SphereTests {

    /**
     * Default constructor for SphereTests.
     */
    public SphereTests() {
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;
    /**
     * Error message for incorrect sphere normal
     */
    private static final String ERROR_NORMAL = "ERROR: Sphere normal calculation is incorrect";
    /**
     * Error message for incorrect sphere intersections
     */
    private static final String ERROR_SPHERE_INTERSECTION = "ERROR: Sphere intersection calculation is incorrect";

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 1), 1d);
        Point p = new Point(0, 0, 2);

        // ============ Equivalence Partitions Tests =============
        // EP01: Simple normal calculation for a point on the sphere surface
        Vector normal = sphere.getNormal(p);

        // Ensure the calculated normal is a unit vector (length = 1)
        assertEquals(1d, normal.length(), DELTA, "ERROR: Sphere normal is not a unit vector");

        // Ensure the normal direction is exactly from center to point
        assertEquals(new Vector(0, 0, 1), normal, ERROR_NORMAL);
    }

    /**
     * Test method for {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);

        // ============ Equivalence Partitions Tests ==================

        // TC01: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 1, 0))),
                "Ray's line out of sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        Point p1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        Point p2 = new Point(1.53484692283495, 0.844948974278318, 0);
        Ray ray2 = new Ray(new Point(-1, 0, 0), new Vector(3, 1, 0));
        List<Point> result = sphere.findIntersections(ray2);

        assertNotNull(result, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result.size(), ERROR_SPHERE_INTERSECTION);

        // Sort the points by distance from ray origin to ensure consistent comparison
        // without relying on missing getX() method
        if (result.get(0).distanceSquared(ray2.origin()) > result.get(1).distanceSquared(ray2.origin())) {
            result = List.of(result.get(1), result.get(0));
        }
        assertEquals(List.of(p1, p2), result, ERROR_SPHERE_INTERSECTION);

        // TC03: Ray starts inside the sphere (1 point)
        List<Point> result3 = sphere.findIntersections(new Ray(new Point(0.5, 0.5, 0), new Vector(1, 0, 0)));
        assertNotNull(result3, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result3.size(), ERROR_SPHERE_INTERSECTION);

        // TC04: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 1, 0))),
                "Ray starts after sphere");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // TC11: Ray starts at sphere and goes inside (1 points)
        List<Point> result11 = sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, 1)));
        assertNotNull(result11, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result11.size(), ERROR_SPHERE_INTERSECTION);

        // TC12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 1))),
                "Ray starts at sphere and goes outside");

        // **** Group 2: Ray's line goes through the center
        // TC13: Ray starts before the sphere (2 points)
        List<Point> result13 = sphere.findIntersections(new Ray(new Point(1, -2, 0), new Vector(0, 1, 0)));
        assertNotNull(result13, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result13.size(), ERROR_SPHERE_INTERSECTION);

        // TC14: Ray starts at sphere and goes inside (1 points)
        List<Point> result14 = sphere.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, 1, 0)));
        assertNotNull(result14, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result14.size(), ERROR_SPHERE_INTERSECTION);

        // TC15: Ray starts inside (1 points)
        List<Point> result15 = sphere.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0)));
        assertNotNull(result15, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result15.size(), ERROR_SPHERE_INTERSECTION);

        // TC16: Ray starts at the center (1 points)
        List<Point> result16 = sphere.findIntersections(new Ray(new Point(1, 0, 0), new Vector(0, 1, 0)));
        assertNotNull(result16, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result16.size(), ERROR_SPHERE_INTERSECTION);

        // TC17: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))),
                "Ray starts at sphere and goes outside through center line");

        // TC18: Ray starts after sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(0, 1, 0))),
                "Ray starts after sphere through center line");

        // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
        // TC19: Ray starts before the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray starts before");

        // TC20: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray starts at point");

        // TC21: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray starts after");

        // **** Group 4: Special cases
        // TC22: Ray's line is outside, ray is orthogonal to ray start to sphere center line
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(1, 0, 0))),
                "Ray is outside and orthogonal");
    }
}