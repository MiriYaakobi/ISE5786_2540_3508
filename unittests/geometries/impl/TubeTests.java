package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Tube} class.
 * The tests verify:
 * <ul>
 * <li>{@link geometries.impl.Tube#getNormal(primitives.Point)} length and direction</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author Miri and Yael
 */
class TubeTests {

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for normal vector length
     */
    private static final String ERROR_NORMAL_LENGTH = "ERROR: Tube normal is not a unit vector";
    /**
     * Error message for normal vector direction
     */
    private static final String ERROR_NORMAL_DIRECTION = "ERROR: Tube normal points in the wrong direction";

    /**
     * Default ray for tube tests (starting at origin, pointing up the Z axis)
     */
    private static final Ray RAY = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
    /**
     * Default tube for tests with radius 1
     */
    private static final Tube TUBE = new Tube(1d, RAY);

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests =============

        // TC01: Normal calculation for a point on the tube where the projection
        // onto the axis is not the ray's origin.
        // Point is at height Z=1, which projects to (0,0,1) on the axis.
        Vector normal1 = TUBE.getNormal(new Point(1, 0, 1));

        // Ensure the calculated normal is a unit vector (length = 1)
        assertEquals(1d, normal1.length(), DELTA, ERROR_NORMAL_LENGTH);
        // Ensure the normal direction is exactly outward from the axis
        assertEquals(new Vector(1, 0, 0), normal1, ERROR_NORMAL_DIRECTION);

        // =============== Boundary Values Tests ==================

        // TC11: Normal calculation for a point on the tube where the projection
        // onto the axis is EXACTLY the ray's origin (dot product is zero).
        // Point is at height Z=0, which projects to (0,0,0) (the Ray's P0).
        Vector normal2 = TUBE.getNormal(new Point(1, 0, 0));

        // Ensure the calculated normal is a unit vector (length = 1)
        assertEquals(1d, normal2.length(), DELTA, ERROR_NORMAL_LENGTH);
        // Ensure the normal direction is exactly outward from the axis
        assertEquals(new Vector(1, 0, 0), normal2, ERROR_NORMAL_DIRECTION);
    }
}