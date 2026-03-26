package geometries.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link geometries.impl.Plane} class.
 *
 * @author Miri and Yael
 */
class PlaneTests {
    /**
     * Basic default constructor to satisfy documentation tools
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
        Plane plane = new Plane(P1, P2, P3);

        // ============ Equivalence Partitions Tests =============
        // EP01: Normal at a point in the plane (not the reference point)
        // Let's use a point inside the triangle formed by P1, P2, P3
        Point pInPlane = new Point(0.33, 0.33, 0.34);
        Vector normal = plane.getNormal(pInPlane);

        // Ensure the calculated normal is a unit vector
        assertEquals(1d, normal.length(), DELTA, "ERROR: Plane normal is not a unit vector");

        // Ensure normal is orthogonal to vectors on the plane
        Vector v1 = P2.subtract(P1);
        assertEquals(0d, normal.dotProduct(v1), DELTA, "ERROR: Plane normal is not orthogonal to the plane");

        // =============== Boundary Values Tests ==================
        // BV01: Normal at the reference point itself
        assertEquals(normal, plane.getNormal(P1), "ERROR: Normal at reference point is different");
    }

    @Test
    void testFindIntersections1() {
        Plane plane = new Plane(P1, P2, P3);
        Ray ray = new Ray(Point.ZERO, new Vector(0, 0, 1));
        assertEquals(1, plane.findIntersections(ray).size(), "ERROR: Plane intersection count is incorrect");
        assertEquals(P1, plane.findIntersections(ray).getFirst(), "ERROR: Plane intersection point is incorrect");
    }

    @Test
    void testFindIntersections2() {
        Plane plane = new Plane(P1, P2, P3);

        // TC01 - Ray is parallel to the plane

        // TC02 - Ray is perpendicular to the plane

        // TC03 - Ray is not parallel to the plane

        // TC04 - Ray is not perpendicular to the plane

        // TC05 - Ray is on the plane

        // TC06 - Ray is behind the plane
        Ray ray = new Ray(Point.ZERO, new Vector(0, 0, -1));
        Assertions.assertNull(plane.findIntersections(ray), "ERROR: Plane intersection count is incorrect");
    }
}
