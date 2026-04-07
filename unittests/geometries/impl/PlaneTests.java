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
 * Unit tests for {@link geometries.impl.Plane} class.
 *
 * @author Miri and Yael
 */
class PlaneTests {

    /**
     * Default constructor for PlaneTests.
     */
    public PlaneTests() {
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;
    /**
     * Error message for plane constructor failure
     */
    private static final String ERROR_CONSTRUCTOR = "ERROR: Plane constructor failed";
    /**
     * Error message for incorrect plane normal
     */
    private static final String ERROR_NORMAL = "ERROR: Plane normal is incorrect";
    /**
     * Error message for incorrect plane intersections
     */
    private static final String ERROR_INTERSECTION = "ERROR: Plane intersection is incorrect";

    /**
     * Point (0,0,1) used in plane tests
     */
    private static final Point P1 = new Point(0, 0, 1);
    /**
     * Point (1,0,0) used in plane tests
     */
    private static final Point P2 = new Point(1, 0, 0);
    /**
     * Point (0,1,0) used in plane tests
     */
    private static final Point P3 = new Point(0, 1, 0);

    /**
     * Test method for {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests =============
        // EP01: Correct plane construction with three non-collinear points
        assertDoesNotThrow(() -> new Plane(P1, P2, P3), ERROR_CONSTRUCTOR);

        // =============== Boundary Values Tests ==================
        // BV01: First and second points are exactly the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P3),
                "ERROR: Constructed a plane with two identical points");

        // BV02: Three points are collinear
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(1, 2, 3),
                        new Point(2, 4, 6),
                        new Point(3, 6, 9)),
                "ERROR: Constructed a plane with three collinear points");
    }

    /**
     * Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // Arrange
        Plane plane = new Plane(P1, P2, P3);

        // ============ Equivalence Partitions Tests =============
        // EP01: Normal at a point in the plane (not the reference point)
        Point pInPlane = new Point(0.33, 0.33, 0.34);

        // Act
        Vector normal = plane.getNormal(pInPlane);

        // Assert
        // Ensure the calculated normal is a unit vector
        assertEquals(1d, normal.length(), DELTA, "ERROR: Plane normal is not a unit vector");

        // Ensure normal is orthogonal to vectors on the plane
        Vector v1 = P2.subtract(P1);
        assertEquals(0d, normal.dotProduct(v1), DELTA, "ERROR: Plane normal is not orthogonal to the plane");

        // =============== Boundary Values Tests ==================
        // BV01: Normal at the reference point itself
        assertEquals(normal, plane.getNormal(P1), "ERROR: Normal at reference point is different");
    }

    /**
     * Test method for {@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Arrange
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==================

        // TC01: Ray intersects the plane (1 point)
        // Act
        List<Point> result = plane.findIntersections(new Ray(new Point(0, 1, 0), new Vector(0, 0, 1)));
        // Assert
        assertNotNull(result, ERROR_INTERSECTION);
        assertEquals(1, result.size(), ERROR_INTERSECTION);
        assertEquals(new Point(0, 1, 1), result.get(0), ERROR_INTERSECTION);

        // TC02: Ray does not intersect the plane (0 points)
        // Act & Assert
        assertNull(plane.findIntersections(new Ray(new Point(0, 1, 2), new Vector(0, 1, 1))),
                "Ray in opposite direction should not intersect");

        // =============== Boundary Values Tests ==================

        // **** Group: Ray is parallel to the plane
        // BV11: Ray included in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0))),
                "Parallel ray included in the plane should return null");

        // BV12: Ray not included in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(1, 0, 0))),
                "Parallel ray not included in the plane should return null");

        // **** Group: Ray is orthogonal to the plane
        // BV13: Ray starts before the plane (1 point)
        // Act
        result = plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));
        // Assert
        assertNotNull(result, ERROR_INTERSECTION);
        assertEquals(1, result.size(), ERROR_INTERSECTION);
        assertEquals(new Point(0, 0, 1), result.get(0), ERROR_INTERSECTION);

        // BV14: Ray starts in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 0, 1))),
                "Orthogonal ray starting in the plane should return null");

        // BV15: Ray starts after the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                "Orthogonal ray starting after the plane should return null");

        // **** Group: Special cases
        // BV16: Ray is neither orthogonal nor parallel and starts at the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 2, 1), new Vector(1, 1, 1))),
                "Ray starting at the plane should return null");

        // BV17: Ray starts at the reference point of the plane (_point) (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 1))),
                "Ray starting at the reference point should return null");
    }
}