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
 * Unit tests for {@link geometries.impl.Tube} class.
 * author: Miri and Yael
 */
class TubeTests {

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;
    /**
     * Error message for incorrect normal calculation
     */
    private static final String ERROR_NORMAL = "ERROR: Tube normal calculation is incorrect";
    /**
     * Error message for incorrect intersection calculation
     */
    private static final String ERROR_INTERSECTION = "ERROR: Tube intersection calculation is incorrect";

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1d, axis);

        // ============ Equivalence Partitions Tests =============
        // EP01: Normal at a standard point on the tube surface
        Vector normal1 = tube.getNormal(new Point(1, 0, 5));
        assertEquals(1d, normal1.length(), DELTA, "ERROR: Tube normal is not a unit vector");
        assertEquals(new Vector(1, 0, 0), normal1, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // BV01: Point projects exactly to the ray's origin (t=0)
        Vector normal2 = tube.getNormal(new Point(0, 1, 0));
        assertEquals(1d, normal2.length(), DELTA, "ERROR: Tube normal (t=0) is not a unit vector");
        assertEquals(new Vector(0, 1, 0), normal2, ERROR_NORMAL);
    }

    /**
     * Test method for {@link geometries.impl.Tube#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Tube axis at (1,0,z), radius 1. Surface is between x=0 and x=2.
        Tube tube = new Tube(1d, new Ray(new Point(1, 0, 0), new Vector(0, 0, 1)));

        // ============ Equivalence Partitions Tests ==================

        // TC01: Ray misses the tube
        assertNull(tube.findIntersections(new Ray(new Point(3, 0, 0), new Vector(0, 1, 1))),
                "Ray misses the tube");

        // TC02: Ray starts before and crosses the tube (2 points)
        List<Point> result02 = tube.findIntersections(new Ray(new Point(-1, 0, 2), new Vector(1, 0, 0)));
        assertNotNull(result02, ERROR_INTERSECTION);
        assertEquals(2, result02.size(), ERROR_INTERSECTION);

        // TC03: Ray starts inside the tube (1 point)
        List<Point> result03 = tube.findIntersections(new Ray(new Point(1.5, 0, 2), new Vector(1, 0, 0)));
        assertNotNull(result03, ERROR_INTERSECTION);
        assertEquals(1, result03.size(), ERROR_INTERSECTION);


        // =============== Boundary Values Tests ==================

        // **** Category: Ray is parallel to the axis
        // TC11: Ray is inside and parallel (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1.5, 0, 0), new Vector(0, 0, 1))),
                "Parallel ray inside");

        // **** Category: Ray is orthogonal to the axis
        // TC14: Ray starts at the axis (1 point)
        List<Point> result14 = tube.findIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0)));
        assertNotNull(result14);
        assertEquals(1, result14.size(), "Ray from axis should have 1 intersection");

        // TC15: Ray starts OUTSIDE and crosses the axis (2 points)
        // Fixed: Moved origin from (0,0,5) to (-1,0,5) to avoid starting on the boundary
        List<Point> result15 = tube.findIntersections(new Ray(new Point(-1, 0, 5), new Vector(1, 0, 0)));
        assertNotNull(result15, "Ray crossing axis should intersect");
        assertEquals(2, result15.size(), "Ray crossing axis should have 2 intersections");

        // **** Category: Tangency
        // TC16: Ray is tangent to the tube (starts before)
        assertNull(tube.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray should be ignored");
    }
}