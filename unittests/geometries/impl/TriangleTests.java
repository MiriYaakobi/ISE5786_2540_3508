package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link geometries.impl.Triangle} class.
 *
 * @author Miri and Yael
 */
class TriangleTests {
    /**
     * Basic default constructor to satisfy documentation tools
     */
    public TriangleTests() {
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;
    /**
     * Error message for triangle constructor failure
     */
    private static final String ERROR_CONSTRUCTOR = "ERROR: Triangle constructor failed";
    /**
     * Error message for incorrect triangle normal
     */
    private static final String ERROR_NORMAL = "ERROR: Triangle normal is incorrect";

    /**
     * Vertex (0,0,1) used in triangle tests
     */
    private static final Point P1 = new Point(0, 0, 1);
    /**
     * Vertex (1,0,0) used in triangle tests
     */
    private static final Point P2 = new Point(1, 0, 0);
    /**
     * Vertex (0,1,0) used in triangle tests
     */
    private static final Point P3 = new Point(0, 1, 0);

    /**
     * Test method for {@link geometries.impl.Triangle#Triangle(Point, Point, Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests =============
        // EP01: Correct triangle construction
        assertDoesNotThrow(() -> new Triangle(P1, P2, P3), ERROR_CONSTRUCTOR);

        // =============== Boundary Values Tests ==================
        // BV01: Identical points
        assertThrows(IllegalArgumentException.class, () -> new Triangle(P1, P1, P3),
                "ERROR: Constructed a triangle with identical points");

        // BV02: Collinear points
        assertThrows(IllegalArgumentException.class, () -> new Triangle(
                        new Point(1, 1, 1),
                        new Point(2, 2, 2),
                        new Point(3, 3, 3)),
                "ERROR: Constructed a triangle with collinear points");
    }

    /**
     * Test method for {@link geometries.impl.Triangle#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Triangle triangle = new Triangle(P1, P2, P3);

        // ============ Equivalence Partitions Tests =============
        // EP01: Test normal at a point inside the triangle
        Point pInside = new Point(1d / 3, 1d / 3, 1d / 3);
        Vector normal = triangle.getNormal(pInside);

        // Ensure the normal is a unit vector
        assertEquals(1d, normal.length(), DELTA, "ERROR: Triangle normal is not a unit vector");

        // Ensure normal is orthogonal to the triangle edges
        assertEquals(0d, normal.dotProduct(P2.subtract(P1)), DELTA, ERROR_NORMAL);
        assertEquals(0d, normal.dotProduct(P3.subtract(P1)), DELTA, ERROR_NORMAL);
    }
}