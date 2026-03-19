package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Sphere} class.
 *
 * @author Miri and Yael
 */
class SphereTests {
    /**
     * Basic default constructor to satisfy documentation tools
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
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1d);
        Point p = new Point(0, 0, 1);

        // ============ Equivalence Partitions Tests =============
        // EP01: Simple normal calculation for a point on the sphere surface
        Vector normal = sphere.getNormal(p);

        // Ensure the calculated normal is a unit vector (length = 1)
        assertEquals(1d, normal.length(), DELTA, "ERROR: Sphere normal is not a unit vector");

        // Ensure the normal direction is exactly from center to point
        assertEquals(new Vector(0, 0, 1), normal, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // Note: As specified in instructions, sphere has no boundary cases for getNormal.
    }
}