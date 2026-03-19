package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Tube} class.
 */
class TubeTests {
    /**
     * Basic default constructor to satisfy documentation tools
     */
    public TubeTests() {
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1d, axis);

        // ============ Equivalence Partitions Tests =============
        // EP01: Normal at a standard point on the tube surface
        Vector normal1 = tube.getNormal(new Point(1, 0, 5));
        assertEquals(1d, normal1.length(), DELTA, "ERROR: Tube normal is not a unit vector");
        assertEquals(new Vector(1, 0, 0), normal1, "ERROR: Tube normal is incorrect");

        // =============== Boundary Values Tests ==================
        // BV01: Point projects exactly to the ray's origin (t=0)
        Vector normal2 = tube.getNormal(new Point(0, 1, 0));
        assertEquals(1d, normal2.length(), DELTA, "ERROR: Tube normal (t=0) is not a unit vector");
        assertEquals(new Vector(0, 1, 0), normal2, "ERROR: Tube normal (t=0) is incorrect");
    }
}