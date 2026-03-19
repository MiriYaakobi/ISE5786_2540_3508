package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link geometries.impl.Triangle} class.
 * The tests verify:
 * <ul>
 * <li>Constructors validity (preventing collinear or identical points)</li>
 * <li>{@link geometries.impl.Triangle#getNormal(primitives.Point)} length and direction</li>
 * </ul>
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 *
 * @author Miri and Yael
 */
class TriangleTests {

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for incorrect constructor behavior
     */
    private static final String ERROR_CONSTRUCTOR = "ERROR: Triangle constructor failed";
    /**
     * Error message for normal vector length
     */
    private static final String ERROR_NORMAL_LENGTH = "ERROR: Triangle normal is not a unit vector";
    /**
     * Error message for normal vector orthogonality
     */
    private static final String ERROR_NORMAL_ORTHOGONAL = "ERROR: Triangle normal is not orthogonal to the triangle plane";

    /**
     * Shared point 1 for tests
     */
    private static final Point P1 = new Point(0, 0, 1);
    /**
     * Shared point 2 for tests
     */
    private static final Point P2 = new Point(1, 0, 0);
    /**
     * Shared point 3 for tests
     */
    private static final Point P3 = new Point(0, 1, 0);

    /**
     * Test method for {@link geometries.impl.Triangle#Triangle(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests =============

        // TC01: Correct triangle construction with three non-collinear points
        assertDoesNotThrow(() -> new Triangle(P1, P2, P3), ERROR_CONSTRUCTOR);

        // =============== Boundary Values Tests ==================

        // TC11: First and second points are exactly the same
        assertThrows(IllegalArgumentException.class, () -> new Triangle(P1, P1, P3),
                "ERROR: Constructed a triangle with two identical points");

        // TC12: Three points are collinear (on the same line)
        assertThrows(IllegalArgumentException.class, () -> new Triangle(
                        new Point(1, 2, 3),
                        new Point(2, 4, 6),
                        new Point(3, 6, 9)),
                "ERROR: Constructed a triangle with three collinear points");
    }

    /**
     * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Triangle triangle = new Triangle(P1, P2, P3);
        Vector normal = triangle.getNormal(P1);

        // ============ Equivalence Partitions Tests =============

        // TC01: Ensure the calculated normal is a unit vector (length = 1)
        assertEquals(1d, normal.length(), DELTA, ERROR_NORMAL_LENGTH);

        // TC02: Ensure the normal is orthogonal to vectors on the triangle's plane
        Vector v1 = P2.subtract(P1);
        Vector v2 = P3.subtract(P1);

        assertEquals(0d, normal.dotProduct(v1), DELTA, ERROR_NORMAL_ORTHOGONAL);
        assertEquals(0d, normal.dotProduct(v2), DELTA, ERROR_NORMAL_ORTHOGONAL);
    }
}