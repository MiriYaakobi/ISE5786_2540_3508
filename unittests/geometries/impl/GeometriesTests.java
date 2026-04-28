package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for geometries.impl.Geometries class.
 * author: Miri and Yael
 */
public class GeometriesTests {

    /**
     * Default constructor for GeometriesTests.
     */
    public GeometriesTests() {
    }

    /**
     * Test method for {@link geometries.impl.Geometries#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        // Arrange: Build a standard collection of geometries for multiple tests
        // Sphere: center(1,0,0), radius(1) -> X range [0,2]
        // Plane: Z=1 (point(0,0,1), normal(0,0,1))
        // Triangle: (1,0,0), (0,1,0), (0,0,1) -> Plane: x+y+z=1
        Geometries geometries = new Geometries(
                new Sphere(new Point(1, 0, 0), 1d),
                new Plane(new Point(0, 0, 1), new Vector(0, 0, 1)),
                new Triangle(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1))
        );

        // ============ Equivalence Partitions Tests ==================

        // TC01: Some geometries intersect (but not all)
        // Ray through sphere(2 points) and plane(1 point), misses triangle
        Ray raySome = new Ray(new Point(0.5, 0.5, -1), new Vector(0, 0, 1));
        List<Point> resultSome = geometries.findIntersections(raySome);
        assertNotNull(resultSome, "Some geometries should intersect");
        assertEquals(3, resultSome.size(), "Wrong number of points for partial intersections");


        // =============== Boundary Values Tests ==================

        // TC11: Empty collection (0 geometries)
        // Testing the behavior when no geometries are added to the composite
        Geometries emptyGeometries = new Geometries();
        assertNull(emptyGeometries.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                "Empty collection must return null");

        // TC12: No geometry is intersected by the Ray
        // Ray heading away from all objects
        Ray rayNone = new Ray(new Point(10, 10, 10), new Vector(1, 1, 1));
        assertNull(geometries.findIntersections(rayNone), "No intersections should return null");

        // TC13: Only one geometry is intersected
        // Ray hits only the plane at Z=1, far from sphere and triangle
        Ray rayOne = new Ray(new Point(5, 5, 0.5), new Vector(0, 0, 1));
        List<Point> resultOne = geometries.findIntersections(rayOne);
        assertNotNull(resultOne, "One geometry should intersect");
        assertEquals(1, resultOne.size(), "Should find exactly 1 point (Plane)");

        // TC14: All geometries are intersected
        // Ray passing through sphere(2), triangle(1), and plane(1)
        Ray rayAll = new Ray(new Point(0.2, 0.2, -1), new Vector(0, 0, 1));
        List<Point> resultAll = geometries.findIntersections(rayAll);
        assertNotNull(resultAll, "All geometries should intersect");
        assertEquals(4, resultAll.size(), "All geometries should be intersected (Sphere+Plane+Triangle)");
    }
}