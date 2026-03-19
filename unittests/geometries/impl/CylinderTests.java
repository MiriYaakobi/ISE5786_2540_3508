package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Cylinder} class.
 */
class CylinderTests {
    /**
     * Basic default constructor to satisfy documentation tools
     */
    public CylinderTests() {
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;
    /**
     * Error message for incorrect cylinder normal
     */
    private static final String ERROR_NORMAL = "ERROR: Cylinder normal is incorrect";

    /**
     * Test method for {@link geometries.impl.Cylinder#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cylinder = new Cylinder(1d, axis, 2d);

        // ============ Equivalence Partitions Tests =============
        // EP01: Point on the side surface
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(new Point(1, 0, 1)), ERROR_NORMAL);

        // EP02: Point on the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0.5, 0, 2)), ERROR_NORMAL);

        // EP03: Point on the bottom base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0.5, 0, 0)), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // BV01: Point at the center of the bottom base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)), ERROR_NORMAL);

        // BV02: Point at the center of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)), ERROR_NORMAL);

        // BV03: Point on the edge of the bottom base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)), ERROR_NORMAL);

        // BV04: Point on the edge of the top base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)), ERROR_NORMAL);
    }
}