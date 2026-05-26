package renderer;

import java.util.List;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import scene.Scene;

/**
 * A simple implementation of a ray tracer.
 * Includes Phong reflection model (diffuse and specular), shadows, and transparency support.
 * <p>
 * This class extends {@link RayTracerBase} and provides a basic implementation
 * for tracing rays and calculating colors using the Phong model.
 * </p>
 *
 * @author Miri and Yael
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constant for the ray head displacement to avoid self-intersection.
     */
    private static final double DELTA = 0.1;

    /**
     * Minimum attenuation factor threshold for transparency calculations.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces the ray and calculates the color of the point it hits.
     *
     * @param ray the ray to trace
     * @return the color of the point
     */
    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);

        if (intersections == null) {
            return _scene.background;
        }

        Intersection closestIntersection = ray.findClosestIntersection(intersections);

        return calcColor(closestIntersection, ray);
    }

    /**
     * Calculates the color of a given intersection.
     * Evaluates the emission, ambient light, and local external light sources (Phong).
     *
     * @param intersection the intersection object containing geometry and point
     * @param ray          the intersecting ray
     * @return the calculated final color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        // Pre-calculate n, v, and nv. If orthogonal, return background.
        if (!preprocessIntersection(intersection, ray.direction())) {
            return _scene.background;
        }

        Color emissionColor = intersection.geometry.getEmission();
        Color ambientLightIntensity = _scene.ambientLight.getIntensity();
        Double3 kA = intersection.material.kA;

        Color ambientColorContribution = ambientLightIntensity.scale(kA);

        // Return Ambient Light + Emission + External Light Sources (Diffuse + Specular)
        return ambientColorContribution.add(emissionColor).add(calcLocalEffects(intersection));
    }

    /**
     * Calculates the color contribution from all external light sources.
     *
     * @param intersection the intersection cache object
     * @return the combined color from all lights
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = Color.BLACK;

        for (LightSource lightSource : _scene.lights) {
            // Only process the light if it hits the correct side of the geometry
            if (preprocessLightSource(intersection, lightSource)) {
                // Calculate cumulative transparency factor from the light source to the point
                Double3 ktr = transparency(intersection, lightSource);

                // Using existing !isLowerThan method instead of missing isGreaterThan
                if (!ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);

                    // Add Diffuse and Specular contributions for this light
                    color = color.add(
                            lightIntensity.scale(calcDiffuse(intersection)),
                            lightIntensity.scale(calcSpecular(intersection))
                    );
                }

                // Collided / Removed lines from previous stage:
                // if (unshaded(intersection, lightSource)) {
                //     Color lightIntensity = lightSource.getIntensity(intersection.point);
                //     color = color.add(lightIntensity.scale(calcDiffuse(intersection)), lightIntensity.scale(calcSpecular(intersection)));
                // }
            }
        }
        return color;
    }

    /**
     * Evaluates the transparency/shadow factor between a light source and the intersection point.
     *
     * @param intersection the intersection point
     * @param lightSource  the external light source
     * @return the transparency coefficient Double3
     */
    private Double3 transparency(Intersection intersection, LightSource lightSource) {
        Vector lightDirection = intersection.l.scale(-1); // direction from point to light source

        // Shift the ray origin slightly to avoid self-intersection
        Vector normalShift = intersection.n.scale(intersection.nl > 0 ? -DELTA : DELTA);
        Point rayOrigin = intersection.point.add(normalShift);
        Ray shadowRay = new Ray(rayOrigin, lightDirection);

        List<Intersection> intersections = _scene.geometries.calcIntersections(shadowRay);
        if (intersections == null) {
            return Double3.ONE;
        }

        Double3 ktr = Double3.ONE;
        double lightDistance = lightSource.getDistance(intersection.point);

        for (Intersection gp : intersections) {
            // Check if the intersection is between the point and the light source
            if (Util.alignZero(gp.point.distance(intersection.point) - lightDistance) < 0) {
                // Using existing product method instead of missing multiply
                ktr = ktr.product(gp.material.kT);
                if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO; // Completely shaded / light fully attenuated
                }
            }
        }
        return ktr;
    }

    /**
     * Calculates the diffuse component of the light reflection.
     *
     * @param intersection the intersection cache object
     * @return the diffuse scaling factor
     */
    private Double3 calcDiffuse(Intersection intersection) {
        double absNl = Math.abs(intersection.nl);
        return intersection.material.kD.scale(absNl);
    }

    /**
     * Calculates the specular component of the light reflection.
     *
     * @param intersection the intersection cache object
     * @return the specular scaling factor
     */
    private Double3 calcSpecular(Intersection intersection) {
        // r = l - 2 * (l * n) * n
        Vector r = intersection.l.subtract(intersection.n.scale(2 * intersection.nl)).normalize();

        // minusVR = -v * r (since v points towards geometry, -v points towards camera)
        double minusVR = Util.alignZero(-intersection.v.dotProduct(r));

        if (minusVR <= 0) {
            return Double3.ZERO; // Reflection points away from camera
        }

        double factor = Math.pow(minusVR, intersection.material.nShininess);
        return intersection.material.kS.scale(factor);
    }
}