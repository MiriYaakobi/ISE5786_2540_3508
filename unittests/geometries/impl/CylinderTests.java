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
 * Unit tests for {@link geometries.impl.Cylinder} class.
 * author: Miri and Yael
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
        // BV01: Point at the center of the bottom base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)), ERROR_NORMAL);

        // BV02: Point at the center of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)), ERROR_NORMAL);

        // BV03: Point on the edge of the bottom base (between base and side)
        // According to instructions, should belong to base normal or side, here base.
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)), ERROR_NORMAL);

        // BV04: Point on the edge of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)), ERROR_NORMAL);
    }

    /**
     * Test method for {@link geometries.impl.Cylinder#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Cylinder cylinder = new Cylinder(1d, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 2d);

        // ============ Equivalence Partitions Tests ==================

        // TC01: Ray hits the side surface (2 points)
        Ray raySide = new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0));
        List<Point> resultSide = cylinder.findIntersections(raySide);
        assertNotNull(resultSide, ERROR_INTERSECTION);
        assertEquals(2, resultSide.size(), ERROR_INTERSECTION);

        // TC02: Ray hits the bottom base and the side (2 points)
        Ray rayBaseSide = new Ray(new Point(0.5, 0, -1), new Vector(0.5, 0, 2));
        assertEquals(2, cylinder.findIntersections(rayBaseSide).size(), "Should hit bottom base and side");

        // TC03: Ray hits the bottom and top bases (2 points)
        Ray rayBases = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        assertEquals(2, cylinder.findIntersections(rayBases).size(), "Should hit both bases");

        // TC04: Ray starts inside the cylinder (1 point)
        Ray rayInside = new Ray(new Point(0.5, 0, 1), new Vector(0, 0, 1));
        assertEquals(1, cylinder.findIntersections(rayInside).size(), "Ray starting inside hits one base/side");


        // =============== Boundary Values Tests ==================

        // **** Category: Ray is parallel to the axis
        // TC11: Ray is parallel inside (2 points - from base to base)
        assertEquals(2, cylinder.findIntersections(new Ray(new Point(0.2, 0, -1), new Vector(0, 0, 1))).size(),
                "Parallel ray inside hits both bases");

        // TC12: Ray is parallel on the surface (0 points)
        assertNull(cylinder.findIntersections(new Ray(new Point(1, 0, -1), new Vector(0, 0, 1))),
                "Parallel ray on surface edge");

        // **** Category: Ray starts at base
        // TC13: Ray starts at bottom base and goes inside (1 point - the other side/base)
        assertEquals(1, cylinder.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 0, 1))).size(),
                "Ray starting at base going in");

        // TC14: Ray starts at bottom base and goes outside (0 points)
        assertNull(cylinder.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 0, -1))),
                "Ray starting at base going out");

        // **** Category: Tangency and Edges
        // TC15: Ray is tangent to the side (0 points)
        assertNull(cylinder.findIntersections(new Ray(new Point(1, -1, 1), new Vector(0, 1, 0))),
                "Tangent ray to cylinder");

        // TC16: Ray hits exactly the junction of base and side (0 points per BVA rules)
        assertNull(cylinder.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0))),
                "Ray hits the edge of the base");
    }
}