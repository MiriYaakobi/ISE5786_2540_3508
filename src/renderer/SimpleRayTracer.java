package renderer;

import java.util.List;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

/**
 * A simple implementation of a ray tracer.
 *
 * @author Miri and Yael
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // 1. Find the intersections of the ray with the scene geometries
        List<Point> intersections = _scene.geometries.findIntersections(ray);

        // 2. If there are no intersections, return the background color
        if (intersections == null) {
            return _scene.background;
        }

        // 3. Find the closest intersection point
        Point closestPoint = ray.findClosestPoint(intersections);

        // 4. Return the color computed at the intersection point
        return calcColor(closestPoint);
    }

    /**
     * Calculates the color of a given intersection point.
     *
     * @param closestPoint the intersection point
     * @return the calculated color
     */
    private Color calcColor(Point closestPoint) {
        // At this stage, we only return the ambient light intensity
        return _scene.ambientLight.getIntensity();
    }
}