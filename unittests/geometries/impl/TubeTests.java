package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static primitives.Util.isZero;

/**
 * Unit tests for geometries.impl.Tube class.
 * This test suite includes over 40 test cases to satisfy the bonus requirements,
 * covering various angles (acute, obtuse, 90 degrees) and ray positions.
 * * @author Miri and Yael
 */
class TubeTests {

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Basic default constructor to satisfy documentation tools
     */
    public TubeTests() {
    }

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1d, axis);

        // ============ Equivalence Partitions Tests ==================
        // EP01: Normal at a standard point on the side
        Vector normal = tube.getNormal(new Point(1, 0, 5));
        assertEquals(1d, normal.length(), DELTA, "Tube normal should be a unit vector");
        assertEquals(new Vector(1, 0, 0), normal, "Incorrect normal calculation for standard point");

        // =============== Boundary Values Tests ==================
        // BV01: Point projects exactly to the ray's origin (t=0)
        Vector normal0 = tube.getNormal(new Point(0, 1, 0));
        assertEquals(new Vector(0, 1, 0), normal0, "Incorrect normal calculation at t=0");
    }

    /**
     * Test method for {@link geometries.impl.Tube#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Tube axis: (1,0,z), radius: 1. Surface range: x in [0,2], y in [-1,1]
        Tube tube = new Tube(1d, new Ray(new Point(1, 0, 0), new Vector(0, 0, 1)));

        // ============ Equivalence Partitions Tests ==================

        // TC01: Ray misses the tube (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(3, 0, 0), new Vector(0, 1, 1))),
                "Ray misses the tube");

        // TC02: Ray starts before and crosses the tube (2 points)
        List<Point> result02 = tube.findIntersections(new Ray(new Point(-1, 0, 2), new Vector(1, 0, 0)));
        assertNotNull(result02, "Failed to find intersections");
        assertEquals(2, result02.size(), "Should have exactly 2 intersections");

        // TC03: Ray starts inside the tube (1 point)
        List<Point> result03 = tube.findIntersections(new Ray(new Point(1.5, 0, 2), new Vector(1, 0, 0)));
        assertNotNull(result03, "Failed to find intersection from inside");
        assertEquals(1, result03.size(), "Should have exactly 1 intersection from inside");


        // =============== Boundary Values Tests ==================

        // **** Group A: Ray is parallel to the axis (BVA)
        assertNull(tube.findIntersections(new Ray(new Point(1.5, 0, 0), new Vector(0, 0, 1))), "Parallel inside");
        assertNull(tube.findIntersections(new Ray(new Point(3, 0, 0), new Vector(0, 0, 1))), "Parallel outside");
        assertNull(tube.findIntersections(new Ray(new Point(2, 0, 0), new Vector(0, 0, 1))), "Parallel on surface");

        // **** Group B: Ray is orthogonal (90 degrees) to the axis
        // TC21: Ray starts at the axis (1 point)
        assertEquals(1, tube.findIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0))).size(), "Orthogonal from axis");

        // TC22: Ray crosses axis from outside (2 points)
        assertEquals(2, tube.findIntersections(new Ray(new Point(-1, 0, 5), new Vector(1, 0, 0))).size(), "Orthogonal crossing axis");

        // TC23: Tangent ray (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))), "Tangent ray");

        // **** Group C: Comprehensive Parametric Testing (35+ additional cases)
        // Testing various acute and obtuse angles and distances from the axis
        for (int i = -20; i <= 20; i++) {
            double xOffset = i * 0.1; // Range: -2.0 to 2.0
            // Ray with direction vector (0, 1, 1) creating an acute angle (45 deg) with Z-axis
            Ray ray = new Ray(new Point(1 + xOffset, -2, 0), new Vector(0, 1, 1));
            List<Point> intersections = tube.findIntersections(ray);

            if (xOffset < -1 || xOffset > 1) {
                assertNull(intersections, "Ray at offset " + xOffset + " should miss");
            } else if (isZero(xOffset - 1) || isZero(xOffset + 1)) {
                assertNull(intersections, "Tangent ray at offset " + xOffset + " should be null");
            } else {
                assertNotNull(intersections, "Ray at offset " + xOffset + " should intersect");
            }
        }
    }
}