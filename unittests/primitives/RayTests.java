package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link primitives.Ray} class.
 * author: Miri and Yael
 */
class RayTests {

    /**
     * Error message for incorrect ray construction or getters
     */
    private static final String ERROR_CONSTRUCTOR = "ERROR: Ray constructor/getter failed";
    /**
     * Error message for incorrect equality check
     */
    private static final String ERROR_EQUALS = "ERROR: Ray equals() wrong result";
    /**
     * Error message for incorrect point calculation
     */
    private static final String ERROR_GET_POINT = "ERROR: Ray getPoint() calculation is incorrect";

    /**
     * Test method for {@link primitives.Ray#Ray(Point, Vector)}.
     */
    @Test
    void testConstructor() {
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(0, 2, 0);
        Ray ray = new Ray(p, v);

        // ============ Equivalence Partitions Tests =============
        // EP01: Check if origin point is assigned correctly
        assertEquals(p, ray.origin(), ERROR_CONSTRUCTOR);

        // EP02: Check if direction vector is properly normalized during construction
        assertEquals(new Vector(0, 1, 0), ray.direction(), ERROR_CONSTRUCTOR);
    }

    /**
     * Test method for {@link primitives.Ray#getPoint(double)}.
     */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==================
        // TC01: Positive t (Point should be on the ray's line in the forward direction)
        assertEquals(new Point(3, 1, 1), ray.getPoint(2), ERROR_GET_POINT);

        // TC02: Negative t (Point should be on the ray's line in the backward direction)
        assertEquals(new Point(0, 1, 1), ray.getPoint(-1), ERROR_GET_POINT);

        // =============== Boundary Values Tests ==================
        // TC11: t is zero (Should return the origin point)
        assertEquals(new Point(1, 1, 1), ray.getPoint(0), "ERROR: getPoint(0) should return the ray's origin");
    }

    /**
     * Test method for {@link primitives.Ray#equals(Object)}.
     */
    @Test
    void testEquals() {
        Ray r1 = new Ray(new Point(1, 2, 3), new Vector(1, 0, 0));
        Ray r2 = new Ray(new Point(1, 2, 3), new Vector(1, 0, 0));
        Ray r3 = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        Ray r4 = new Ray(new Point(1, 2, 3), new Vector(0, 1, 0));

        // ============ Equivalence Partitions Tests =============
        // EP01: Test identical rays
        assertEquals(r1, r2, ERROR_EQUALS);

        // EP02: Test rays with different origin points
        assertNotEquals(r1, r3, ERROR_EQUALS);

        // EP03: Test rays with different direction vectors
        assertNotEquals(r1, r4, ERROR_EQUALS);
    }

    /**
     * Test method for {@link primitives.Ray#toString()}.
     */
    @Test
    void testToString() {
        Ray ray = new Ray(new Point(1, 2, 3), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests =============
        // EP01: Check if toString contains the expected output format
        String result = ray.toString();
        assertNotNull(result, "ERROR: toString() returned null");
        assertTrue(result.contains("origin=") && result.contains("direction="),
                "ERROR: toString() format is incorrect");
    }
}