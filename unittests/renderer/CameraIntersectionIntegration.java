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
 * These tests ensure that rays constructed by the camera correctly intersect scene geometries.
 *
 * @author Miri and Yael
 */
public class CameraIntersectionIntegration {
    /**
     * Default constructor for CameraIntersectionIntegration.
     */
    public CameraIntersectionIntegration() {
    }

    // Common cameras prepared to avoid duplicate instantiation in the module
    /**
     * First camera instance for integration tests
     */
    private final Camera camera1 = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(3, 3)
            .build();

    /**
     * Second camera instance for integration tests, positioned slightly off-center
     */
    private final Camera camera2 = Camera.getBuilder()
            .setLocation(new Point(0, 0, 0.5))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpDistance(1)
            .setVpSize(3, 3)
            .setResolution(3, 3)
            .build();

    /**
     * Helper method to count total intersections for a given camera and geometry.
     *
     * @param camera        the camera instance generating rays
     * @param body          the geometric body to intersect with
     * @param expectedCount the expected total number of intersection points
     * @param testName      the name of the specific test case
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body, int expectedCount, String testName) {
        int totalIntersections = 0;
        int nX = camera.getNx();
        int nY = camera.getNy();

        for (int i = 0; i < nY; ++i) {
            for (int j = 0; j < nX; ++j) {
                Ray ray = camera.constructRay(j, i);
                List<Point> intersections = body.findIntersections(ray);
                if (intersections != null) {
                    totalIntersections += intersections.size();
                }
            }
        }
        assertEquals(expectedCount, totalIntersections, "Wrong number of intersections in: " + testName);
    }

    /**
     * Integration tests for Sphere intersections with Camera rays.
     */
    @Test
    public void testCameraRaySphereIntegration() {
        // TC01: Sphere r=1 (2 intersections)
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -3), 1), 2, "Sphere TC01");

        // TC02: Large Sphere r=2.5 (18 intersections)
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2.5), 2.5), 18, "Sphere TC02");

        // TC03: Medium Sphere r=2 (10 intersections)
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2), 2), 10, "Sphere TC03");

        // TC04: Camera inside Sphere (9 intersections)
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -1), 4), 9, "Sphere TC04");

        // TC05: Sphere behind Camera (0 intersections)
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, 1), 0.5), 0, "Sphere TC05");
    }

    /**
     * Integration tests for Plane intersections with Camera rays.
     */
    @Test
    public void testCameraRayPlaneIntegration() {
        // TC01: Plane parallel to View Plane (9 intersections)
        assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 9, "Plane TC01");

        // TC02: Tilted plane (9 intersections)
        assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -1.5), new Vector(0, -0.5, 1)), 9, "Plane TC02");

        // TC03: Very tilted plane (6 intersections)
        assertIntersectionsCount(camera1, new Plane(new Point(0, 0, -5), new Vector(0, -1, 1)), 6, "Plane TC03");

        // TC04: Plane behind camera (0 intersections)
        assertIntersectionsCount(camera1, new Plane(new Point(0, 0, 1), new Vector(0, 0, 1)), 0, "Plane TC04");
    }

    /**
     * Integration tests for Triangle intersections with Camera rays.
     */
    @Test
    public void testCameraRayTriangleIntegration() {
        // TC01: Small triangle (1 intersection)
        assertIntersectionsCount(camera1, new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 1, "Triangle TC01");

        // TC02: Large triangle (2 intersections)
        assertIntersectionsCount(camera1, new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 2, "Triangle TC02");

        // TC03: Triangle behind camera (0 intersections)
        assertIntersectionsCount(camera1, new Triangle(new Point(0, 1, 2), new Point(1, -1, 2), new Point(-1, -1, 2)), 0, "Triangle TC03");
    }
}