package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Cylinder} class.
 * The tests verify:
 * <ul>
 * <li>{@link geometries.impl.Cylinder#getNormal(primitives.Point)}</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author Miri and Yael
 */
class CylinderTests {

    /**
     * Error message for incorrect normal vector
     */
    private static final String ERROR_NORMAL = "ERROR: Cylinder getNormal() wrong result";

    /**
     * Default ray for cylinder tests (starting at origin, pointing up the Z axis)
     */
    private static final Ray RAY = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));

    /**
     * Default cylinder for tests: radius 1, height 5
     */
    private static final Cylinder CYLINDER = new Cylinder(1d, RAY, 5d);

    /**
     * Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests =============

        // TC01: Point on the round surface of the cylinder
        // The normal should be strictly horizontal, ignoring the Z axis
        assertEquals(new Vector(1, 0, 0), CYLINDER.getNormal(new Point(1, 0, 2)), ERROR_NORMAL);

        // TC02: Point on the top base of the cylinder
        // The normal should point strictly in the direction of the ray (Z axis)
        assertEquals(new Vector(0, 0, 1), CYLINDER.getNormal(new Point(0.5, 0, 5)), ERROR_NORMAL);

        // TC03: Point on the bottom base of the cylinder
        // The normal should point strictly in the opposite direction of the ray (-Z axis)
        assertEquals(new Vector(0, 0, -1), CYLINDER.getNormal(new Point(0.5, 0, 0)), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // TC11: Point exactly at the center of the top base
        // Mathematical edge case because vector subtraction from center yields zero vector
        assertEquals(new Vector(0, 0, 1), CYLINDER.getNormal(new Point(0, 0, 5)), ERROR_NORMAL);

        // TC12: Point exactly at the center of the bottom base (origin of the ray)
        // Mathematical edge case for the same reason
        assertEquals(new Vector(0, 0, -1), CYLINDER.getNormal(new Point(0, 0, 0)), ERROR_NORMAL);
    }
}