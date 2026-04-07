package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link geometries.impl.Cylinder} class.
 * This suite covers EP and BVA cases for normals and intersections,
 * including side surface and both top/bottom bases.
 * * @author Miri and Yael
 */
class CylinderTests {

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for incorrect normal calculation
     */
    private static final String ERROR_NORMAL = "ERROR: Cylinder normal calculation is incorrect";

    /**
     * Error message for incorrect intersection calculation
     */
    private static final String ERROR_INTERSECTION = "ERROR: Cylinder intersection calculation is incorrect";

    /**
     * Basic default constructor to satisfy documentation tools
     */
    public CylinderTests() {
    }

    /**
     * Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cylinder = new Cylinder(1d, axis, 2d);

        // ============ Equivalence Partitions Tests =============

        // EP01: Point on the side surface
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(new Point(1, 0, 1)), ERROR_NORMAL);

        // EP02: Point on the top base (Z=2)
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0.5, 0, 2)), ERROR_NORMAL);

        // EP03: Point on the bottom base (Z=0)
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0.5, 0, 0)), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: Point at the center of the bottom base (P0)
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)), ERROR_NORMAL);

        // BV02: Point at the center of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)), ERROR_NORMAL);

        // BV03: Point on the edge of the bottom base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)), ERROR_NORMAL);

        // BV04: Point on the edge of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)), ERROR_NORMAL);
    }

    /**
     * Helper method to safely count intersections (returns 0 if list is null)
     *
     * @param result the list of intersection points
     * @return the count of intersections, or 0 if the list is null
     */
    private int countIntersections(List<Point> result) {
        return result == null ? 0 : result.size();
    }

    /**
     * Test method for {@link geometries.impl.Cylinder#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Cylinder cylinder = new Cylinder(1d, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 2d);

        // ============ Equivalence Partitions Tests ==================

        // TC01: Ray hits the side surface (2 points)
        assertEquals(2, countIntersections(cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0)))),
                "Should intersect side surface at 2 points");

        // TC02: Ray hits the bottom base and the side (2 points)
        assertEquals(2, countIntersections(cylinder.findIntersections(new Ray(new Point(0.5, 0, -1), new Vector(0.5, 0, 2)))),
                "Should hit bottom base and side surface");

        // TC03: Ray hits the bottom and top bases (2 points)
        assertEquals(2, countIntersections(cylinder.findIntersections(new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1)))),
                "Should hit both top and bottom bases");

        // TC04: Ray starts inside the cylinder (1 point)
        assertEquals(1, countIntersections(cylinder.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(0, 0, 1)))),
                "Ray starting inside should hit one base/side");

        // =============== Boundary Values Tests ==================

        // TC11: Ray is parallel inside (2 points - from base to base)
        assertEquals(2, countIntersections(cylinder.findIntersections(new Ray(new Point(0.2, 0, -1), new Vector(0, 0, 1)))),
                "Parallel ray inside should hit both bases");

        // TC12: Ray is parallel on the surface (0 points)
        assertNull(cylinder.findIntersections(new Ray(new Point(1, 0, -1), new Vector(0, 0, 1))),
                "Parallel ray on surface edge should be null");

        // TC13: Ray starts at bottom base and goes inside (1 point)
        assertEquals(1, countIntersections(cylinder.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 0, 1)))),
                "Ray starting at base going in");

        // TC14: Ray starts at bottom base and goes outside (0 points)
        assertNull(cylinder.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 0, -1))),
                "Ray starting at base going out");

        // TC15: Tangent ray to side (0 points)
        assertNull(cylinder.findIntersections(new Ray(new Point(1.1, 0, 1), new Vector(0, 1, 0))),
                "Tangent ray should be null");
    }
}