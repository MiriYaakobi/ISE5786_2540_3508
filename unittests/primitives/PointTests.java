package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link primitives.Point} class.
 * The tests verify:
 * <ul>
 * <li>Constructors validity</li>
 * <li>Point subtraction results in correct Vector</li>
 * <li>Point addition with Vector results in correct Point</li>
 * <li>Distance calculations between points</li>
 * <li>Equality and String representation</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author Miri and Yael
 */
class PointTests {

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for incorrect point addition
     */
    private static final String ERROR_ADD = "ERROR: Point add() wrong result";
    /**
     * Error message for incorrect point subtraction
     */
    private static final String ERROR_SUBTRACT = "ERROR: Point subtract() wrong result";
    /**
     * Error message for incorrect distance calculation
     */
    private static final String ERROR_DISTANCE = "ERROR: Point distance calculation wrong result";
    /**
     * Error message for incorrect equality check
     */
    private static final String ERROR_EQUALS = "ERROR: Point equals() wrong result";

    /**
     * Test method for Point constructors.
     */
    @Test
    void testConstructors() {
        // ============ Equivalence Partitions Tests =============
        // TC01: Simple point construction
        assertDoesNotThrow(() -> new Point(1, 2, 3),
                "ERROR: Failed constructing a correct point");
    }

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 6);

        // ============ Equivalence Partitions Tests =============
        // TC01: Simple subtract test resulting in a valid vector
        assertEquals(new Vector(1, 2, 3), p2.subtract(p1), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================
        // TC11: Subtract point from itself (should throw exception for zero vector)
        assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1),
                "ERROR: subtract() for same point does not throw exception for zero vector");
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        Point p1 = new Point(1, 2, 3);
        Vector v1 = new Vector(-1, -2, -3);

        // ============ Equivalence Partitions Tests =============
        // TC01: Simple add test combining a point and a vector
        assertEquals(new Point(0, 0, 0), p1.add(v1), ERROR_ADD);
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(3, 4, 4);

        // ============ Equivalence Partitions Tests =============
        // TC01: Simple distance squared test between two different points
        assertEquals(9d, p1.distanceSquared(p2), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================
        // TC11: Distance squared from a point to itself should be zero
        assertEquals(0d, p1.distanceSquared(p1), DELTA, ERROR_DISTANCE);
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(3, 4, 4);

        // ============ Equivalence Partitions Tests =============
        // TC01: Simple distance test between two different points
        assertEquals(3d, p1.distance(p2), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================
        // TC11: Distance from a point to itself should be zero
        assertEquals(0d, p1.distance(p1), DELTA, ERROR_DISTANCE);
    }

    /**
     * Test method for {@link primitives.Point#equals(Object)}.
     */
    @Test
    void testEquals() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(1, 2, 3);
        Point p3 = new Point(0, 0, 0);

        // ============ Equivalence Partitions Tests =============
        // TC01: Test identical points
        assertEquals(p1, p2, ERROR_EQUALS);

        // TC02: Test different points
        assertNotEquals(p1, p3, ERROR_EQUALS);
    }

    /**
     * Test method for {@link primitives.Point#toString()}.
     */
    @Test
    void testToString() {
        Point p1 = new Point(1, 2, 3);

        // ============ Equivalence Partitions Tests =============
        // TC01: Check if toString contains the expected coordinates
        String result = p1.toString();
        assertTrue(result.contains("1.0") && result.contains("2.0") && result.contains("3.0"),
                "ERROR: toString() format is incorrect");
    }
}