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
public class SimpleRayTracer extends RayTracerBase {

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
        List<Intersectable.Intersection> intersections = _scene.geometries.calcIntersections(ray);

        if (intersections == null) {
            return _scene.background;
        }

        Intersectable.Intersection closestIntersection = ray.findClosestIntersection(intersections);

        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color of a given intersection.
     * Evaluates the emission and ambient light scaled by the material's kA coefficient.
     *
     * @param intersection the intersection object containing geometry and point
     * @return the calculated final color at the intersection point
     */
    private Color calcColor(Intersectable.Intersection intersection) {
        Color emissionColor = intersection.geometry.getEmission();
        Color ambientLightIntensity = _scene.ambientLight.getIntensity();
        primitives.Double3 kA = intersection.material.kA;

        Color ambientColorContribution = ambientLightIntensity.scale(kA);
        return ambientColorContribution.add(emissionColor);
    }
}