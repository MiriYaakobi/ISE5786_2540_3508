package renderer;

import java.util.List;

import geometries.api.Intersectable;
import primitives.Color;
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
        // Changed from findIntersections to calcIntersections, returning List<Intersection>
        List<Intersectable.Intersection> intersections = _scene.geometries.calcIntersections(ray);

        // 2. If there are no intersections, return the background color
        if (intersections == null) {
            return _scene.background;
        }

        // 3. Find the closest intersection point
        // Changed from findClosestPoint to findClosestIntersection, returning Intersection
        Intersectable.Intersection closestIntersection = ray.findClosestIntersection(intersections);

        // 4. Return the color computed at the intersection point
        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color of a given intersection.
     * Updated to receive an Intersection object and include emission and ambient light scaled by material's kA.
     *
     * @param intersection the intersection object
     * @return the calculated color
     */
    private Color calcColor(Intersectable.Intersection intersection) {
        // Get the emission color from the intersected geometry
        Color emissionColor = intersection.geometry.getEmission();

        // Get the ambient light intensity from the scene
        Color ambientLightIntensity = _scene.ambientLight.getIntensity();

        // Get the ambient reflection coefficient (kA) from the material of the intersected geometry
        primitives.Double3 kA = intersection.material.kA;

        // Calculate the ambient color contribution: ambientLightIntensity * kA
        // CORRECTED: Use scale(Double3) method from Color class
        Color ambientColorContribution = ambientLightIntensity.scale(kA);

        // The final color is the sum of ambient color contribution and emission color
        Color finalColor = ambientColorContribution.add(emissionColor);

        return finalColor;
    }
}
