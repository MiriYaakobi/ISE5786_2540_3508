package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
     * Error message for incorrect triangle intersections
     */
    private static final String ERROR_INTERSECTION = "ERROR: Triangle intersection is incorrect";

    /**
     * Vertex (1,0,0) used in triangle tests
     */
    private static final Point P1 = new Point(1, 0, 0);
    /**
     * Vertex (0,1,0) used in triangle tests
     */
    private static final Point P2 = new Point(0, 1, 0);
    /**
     * Vertex (0,0,1) used in triangle tests
     */
    private static final Point P3 = new Point(0, 0, 1);

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
        // Arrange
        Triangle triangle = new Triangle(P1, P2, P3);
        Point pInside = new Point(1d / 3, 1d / 3, 1d / 3);

        // Act
        Vector normal = triangle.getNormal(pInside);

        // Assert
        // EP01: Test normal at a point inside the triangle
        assertEquals(1d, normal.length(), DELTA, "ERROR: Triangle normal is not a unit vector");

        // Ensure normal is orthogonal to the triangle edges
        assertEquals(0d, normal.dotProduct(P2.subtract(P1)), DELTA, "ERROR: Triangle normal is not orthogonal to edge");
        assertEquals(0d, normal.dotProduct(P3.subtract(P1)), DELTA, "ERROR: Triangle normal is not orthogonal to edge");
    }

    /**
     * Test method for {@link geometries.impl.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(P1, P2, P3);

        // ============ Equivalence Partitions Tests ==================

        // TC01: Inside triangle (1 point)
        List<Point> result = triangle.findIntersections(new Ray(new Point(1, 1, 1), new Vector(-1, -1, -1)));
        assertNotNull(result, ERROR_INTERSECTION);
        assertEquals(1, result.size(), ERROR_INTERSECTION);
        assertEquals(new Point(1d / 3, 1d / 3, 1d / 3), result.get(0), ERROR_INTERSECTION);

        // TC02: Outside against edge (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(1, 1, 1))),
                "Ray hits outside against edge");

        // TC03: Outside against vertex (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(-1, -1, 1))),
                "Ray hits outside against vertex");

        // =============== Boundary Values Tests ==================

        // TC11: On edge (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0.5, 0.5, 1))),
                "Ray hits on edge should return null");

        // TC12: In vertex (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(1, 0, 1))),
                "Ray hits on vertex should return null");

        // TC13: On edge continuation (0 points)
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(2, -1, 1))),
                "Ray hits on edge continuation should return null");

        // =============== Plane-Related Tests (BVA of Plane) ==================

        // TC21: Ray is parallel to the triangle's plane
        assertNull(triangle.findIntersections(new Ray(new Point(1, 1, 2), new Vector(1, -1, 0))),
                "Parallel ray should return null");

        // TC22: Ray starts in the triangle's plane
        assertNull(triangle.findIntersections(new Ray(new Point(0.5, 0.5, 0), new Vector(1, 1, 1))),
                "Ray starting in the plane should return null");

        // TC23: Ray in opposite direction to the plane
        assertNull(triangle.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                "Ray in opposite direction should return null");
    }
}