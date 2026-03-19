package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link primitives.Vector} class.
 *
 * @author Miri and Yael
 */
class VectorTests {

    /**
     * Basic default constructor to satisfy documentation tools
     */
    public VectorTests() {
    }

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for incorrect constructor behavior
     */
    private static final String ERROR_CONSTRUCTOR = "ERROR: Vector constructor failed";
    /**
     * Error message for incorrect vector addition
     */
    private static final String ERROR_ADD = "ERROR: Vector add() wrong result";
    /**
     * Error message for incorrect vector subtraction
     */
    private static final String ERROR_SUBTRACT = "ERROR: Vector subtract() wrong result";
    /**
     * Error message for incorrect scaling
     */
    private static final String ERROR_SCALE = "ERROR: Vector scale() wrong result";
    /**
     * Error message for incorrect dot product
     */
    private static final String ERROR_DOT_PRODUCT = "ERROR: Vector dotProduct() wrong result";
    /**
     * Error message for incorrect cross product
     */
    private static final String ERROR_CROSS_PRODUCT = "ERROR: Vector crossProduct() wrong result";
    /**
     * Error message for incorrect length calculation
     */
    private static final String ERROR_LENGTH = "ERROR: Vector length calculation wrong result";
    /**
     * Error message for incorrect normalization
     */
    private static final String ERROR_NORMALIZE = "ERROR: Vector normalize() wrong result";

    /**
     * Default vector used across multiple tests
     */
    private static final Vector V1 = new Vector(1, 2, 3);

    /**
     * Test method for Vector constructors.
     */
    @Test
    void testConstructors() {
        // ============ Equivalence Partitions Tests =============
        // EP01: Correct vector construction
        assertDoesNotThrow(() -> new Vector(1, 2, 3), ERROR_CONSTRUCTOR);

        // =============== Boundary Values Tests ==================
        // BV01: Zero vector construction - using three doubles
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                "ERROR: Constructed a zero vector (3 doubles)");

        // BV02: Zero vector construction - using Double3
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO),
                "ERROR: Constructed a zero vector (Double3)");
    }

    /**
     * Test method for {@link primitives.Vector#add(Vector)}.
     */
    @Test
    void testAdd() {
        Vector v1Opposite = new Vector(-1, -2, -3);

        // ============ Equivalence Partitions Tests =============
        // EP01: Simple vector addition
        assertEquals(new Vector(2, 4, 6), V1.add(V1), ERROR_ADD);

        // =============== Boundary Values Tests ==================
        // BV01: Addition that results in zero vector
        assertThrows(IllegalArgumentException.class, () -> V1.add(v1Opposite),
                "ERROR: Vector add() to zero does not throw exception");
    }

    /**
     * Test method for {@link primitives.Point#subtract(Point)}.
     */
    @Test
    void testSubtract() {
        Vector v2 = new Vector(2, 4, 6);

        // ============ Equivalence Partitions Tests =============
        // EP01: Simple vector subtraction
        assertEquals(new Vector(1, 2, 3), v2.subtract(V1), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================
        // BV01: Subtraction that results in zero vector
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1),
                "ERROR: Vector subtract() to zero does not throw exception");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests =============
        // EP01: Scale by a positive scalar
        assertEquals(new Vector(2, 4, 6), V1.scale(2), ERROR_SCALE);

        // EP02: Scale by a negative scalar
        assertEquals(new Vector(-1, -2, -3), V1.scale(-1), ERROR_SCALE);

        // =============== Boundary Values Tests ==================
        // BV01: Scale by zero
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0),
                "ERROR: Vector scale() by zero does not throw exception");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        Vector v2 = new Vector(-2, -4, -6);
        Vector v3 = new Vector(0, 3, -2);

        // ============ Equivalence Partitions Tests =============
        // EP01: Simple dot product between vectors
        assertEquals(-28d, V1.dotProduct(v2), DELTA, ERROR_DOT_PRODUCT);

        // =============== Boundary Values Tests ==================
        // BV01: Dot product of orthogonal vectors (must be zero)
        assertEquals(0d, V1.dotProduct(v3), DELTA, "ERROR: dotProduct() for orthogonal vectors is not zero");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(Vector)}.
     */
    @Test
    void testCrossProduct() {
        Vector v2 = new Vector(0, 3, -2);
        Vector v3 = V1.crossProduct(v2);

        // ============ Equivalence Partitions Tests =============
        // EP01: Check length of cross product result
        assertEquals(V1.length() * v2.length(), v3.length(), DELTA, ERROR_CROSS_PRODUCT);

        // EP02: Check orthogonality to operands
        assertEquals(0d, v3.dotProduct(V1), DELTA, "ERROR: crossProduct() result not orthogonal to v1");
        assertEquals(0d, v3.dotProduct(v2), DELTA, "ERROR: crossProduct() result not orthogonal to v2");

        // =============== Boundary Values Tests ==================
        // BV01: Cross product of parallel vectors
        Vector v4 = new Vector(-2, -4, -6);
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(v4),
                "ERROR: crossProduct() for parallel vectors does not throw exception");
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests =============
        // EP01: Simple length squared test
        assertEquals(14d, V1.lengthSquared(), DELTA, ERROR_LENGTH);
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        Vector vTest = new Vector(0, 3, 4);

        // ============ Equivalence Partitions Tests =============
        // EP01: Simple length test
        assertEquals(5d, vTest.length(), DELTA, ERROR_LENGTH);
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        Vector u = V1.normalize();

        // ============ Equivalence Partitions Tests =============
        // EP01: Check if normalized vector is a unit vector
        assertEquals(1d, u.length(), DELTA, ERROR_NORMALIZE);

        // EP02: Check if normalization keeps the original direction
        assertTrue(V1.dotProduct(u) > 0, "ERROR: normalized vector direction is opposite");

        // =============== Boundary Values Tests ==================
        // BV01: Check that normalized vector is parallel to original
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(u),
                "ERROR: normalized vector is not parallel to original");
    }
}