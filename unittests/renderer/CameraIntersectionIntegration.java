package renderer;

import java.util.List;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for Camera rays and Geometry intersections.
 *
 * @author Miri and Yael
 */
public class CameraIntersectionIntegration {

    /**
     * Helper method to count total intersections for a given camera and body.
     *
     * @param camera        the camera object
     * @param body          the intersectable geometric body
     * @param expectedCount the expected number of intersections
     * @param testName      the name of the test case
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body, int expectedCount, String testName) {
        int totalIntersections = 0;
        int nX = camera.getNx(); // Use camera's resolution
        int nY = camera.getNy(); // Use camera's resolution

        for (int i = 0; i < nY; ++i) { // Iterate over rows (yIndex)
            for (int j = 0; j < nX; ++j) { // Iterate over columns (xIndex)
                Ray ray = camera.constructRay(j, i); // xIndex, yIndex
                List<Point> intersections = body.findIntersections(ray);
                if (intersections != null) {
                    totalIntersections += intersections.size();
                }
            }
        }
        assertEquals(expectedCount, totalIntersections, "Wrong number of intersections in: " + testName);
    }

    @Test
    public void testCameraRaySphereIntegration() {
        // Camera builder setup
        Camera.Builder builder = Camera.getBuilder()
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3);

        // TC01: Sphere r=1 (2 intersections)
        assertIntersectionsCount(builder.setLocation(Point.ZERO).build(), 
                new Sphere(new Point(0, 0, -3), 1), 2, "Sphere TC01");

        // TC02: Large Sphere r=2.5 (18 intersections) - Camera at (0,0,0.5)
        assertIntersectionsCount(builder.setLocation(new Point(0, 0, 0.5)).build(),
                new Sphere(new Point(0, 0, -2.5), 2.5), 18, "Sphere TC02");

        // TC03: Medium Sphere r=2 (10 intersections) - Camera at (0,0,0.5)
        assertIntersectionsCount(builder.setLocation(new Point(0, 0, 0.5)).build(), 
                new Sphere(new Point(0, 0, -2), 2), 10, "Sphere TC03");

        // TC04: Camera inside Sphere (9 intersections)
        assertIntersectionsCount(builder.setLocation(Point.ZERO).build(), 
                new Sphere(new Point(0, 0, -1), 4), 9, "Sphere TC04");

        // TC05: Sphere behind Camera (0 intersections)
        assertIntersectionsCount(builder.build(), 
                new Sphere(new Point(0, 0, 1), 0.5), 0, "Sphere TC05");
    }

    @Test
    public void testCameraRayPlaneIntegration() {
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .build();

        // TC01: Plane parallel to View Plane (9 intersections)
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 9, "Plane TC01");

        // TC02: Tilted plane (9 intersections)
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -1.5), new Vector(0, -0.5, 1)), 9, "Plane TC02");

        // TC03: Very tilted plane (6 intersections)
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -5), new Vector(0, -1, 1)), 6, "Plane TC03");

        // TC04: Plane behind camera (0 intersections)
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, 1), new Vector(0, 0, 1)), 0, "Plane TC04");
    }

    @Test
    public void testCameraRayTriangleIntegration() {
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .build();

        // TC01: Small triangle (1 intersection)
        assertIntersectionsCount(camera, new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 1, "Triangle TC01");

        // TC02: Large triangle (2 intersections)
        assertIntersectionsCount(camera, new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 2, "Triangle TC02");

        // TC03: Triangle behind camera (0 intersections)
        assertIntersectionsCount(camera, new Triangle(new Point(0, 1, 2), new Point(1, -1, 2), new Point(-1, -1, 2)), 0, "Triangle TC03");
    }
}
