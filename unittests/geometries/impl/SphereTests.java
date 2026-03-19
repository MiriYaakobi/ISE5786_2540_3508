package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Sphere} class.
 * The tests verify:
 * <ul>
 * <li>{@link geometries.impl.Sphere#getNormal(primitives.Point)} length and direction</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author Miri and Yael
 */
class SphereTests {

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for normal vector length
     */
    private static final String ERROR_NORMAL_LENGTH = "ERROR: Sphere normal is not a unit vector";
    /**
     * Error message for normal vector direction
     */
    private static final String ERROR_NORMAL_DIRECTION = "ERROR: Sphere normal points in the wrong direction";

    /**
     * Default center point for sphere tests
     */
    private static final Point CENTER = new Point(0, 0, 0);

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(CENTER, 1d);
        Point p = new Point(0, 0, 1);
        Vector expectedNormal = new Vector(0, 0, 1);

        // ============ Equivalence Partitions Tests =============

        // TC01: Simple normal calculation for a point on the sphere surface
        Vector normal = sphere.getNormal(p);

        // Ensure the calculated normal is a unit vector (length = 1)
        assertEquals(1d, normal.length(), DELTA, ERROR_NORMAL_LENGTH);

        // Ensure the normal direction is exactly the vector from the center to the point
        assertEquals(expectedNormal, normal, ERROR_NORMAL_DIRECTION);

        // =============== Boundary Values Tests ==================

        // Note: There are no true boundary values for a sphere's normal because
        // the surface is perfectly continuous and lacks edges, corners, or transitions.
    }
}