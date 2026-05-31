package renderer;

import java.util.List;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import scene.Scene;

/**
 * A simple implementation of a ray tracer.
 * Includes Phong reflection model (diffuse and specular), shadows, transparency, and recursive global effects.
 * <p>
 * This class extends {@link RayTracerBase} and provides a basic implementation
 * for tracing rays and calculating colors using the Phong model.
 * </p>
 *
 * @author Miri and Yael
 */
public class SimpleRayTracer extends RayTracerBase {

    // Collided / Removed lines from previous stage (DELTA moved to Ray class per Section 4 instructions):
    // private static final double DELTA = 0.1;

    /**
     * Minimum attenuation factor threshold for transparency and global color calculations.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /**
     * Maximum level of recursion for global color calculations.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /**
     * Initial attenuation factor for recursion.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

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
        Intersection closestIntersection = findClosestIntersection(ray);
        return closestIntersection == null ? _scene.background : calcColor(closestIntersection, ray);
    }

    /**
     * Calculates the color of a given intersection.
     * Evaluates the ambient light and initiates recursive local and global color calculations.
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

        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K)
                .add(_scene.ambientLight.getIntensity().scale(intersection.material.kA));
    }

    /**
     * Recursive method to calculate the color at an intersection point.
     * Sums emission, local effects, and global effects.
     *
     * @param intersection the intersection point
     * @param level        the remaining recursion level
     * @param k            the current cumulative attenuation factor
     * @return the calculated color
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = intersection.geometry.getEmission()
                .add(calcLocalEffects(intersection, k))
                .add(calcGlobalEffects(intersection, intersection.v, level, k));
        return color;
    }

    /**
     * Calculates the color contribution from all external light sources, scaled by transparency and attenuation.
     *
     * @param intersection the intersection cache object
     * @param k            the cumulative attenuation factor
     * @return the combined color from all lights
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = Color.BLACK;

        for (LightSource lightSource : _scene.lights) {
            // Only process the light if it hits the correct side of the geometry
            if (preprocessLightSource(intersection, lightSource)) {
                // Calculate cumulative transparency factor from the light source to the point
                Double3 ktr = transparency(intersection, lightSource);

                // Using product with cumulative factor k and existing !isLowerThan method instead of missing isGreaterThan
                if (!ktr.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);

                    // Add Diffuse and Specular contributions for this light
                    color = color.add(
                            lightIntensity.scale(calcDiffuse(intersection)),
                            lightIntensity.scale(calcSpecular(intersection))
                    );
                }
            }
        }
        return color;
    }

    /**
     * Evaluates the global effects (reflection and transmission) by summing their color contributions.
     *
     * @param intersection the intersection point
     * @param v            the ray direction vector
     * @param level        the remaining recursion level
     * @param k            the cumulative attenuation factor
     * @return the combined global effects color
     */
    private Color calcGlobalEffects(Intersection intersection, Vector v, int level, Double3 k) {
        Color color = Color.BLACK;

        // Add refracted/transmitted light effect
        Ray refractionRay = constructRefractionRay(intersection, v);
        color = color.add(calcGlobalEffect(refractionRay, level, k, intersection.material.kT));

        // Add reflected light effect
        Ray reflectionRay = constructReflectionRay(intersection, v);
        if (reflectionRay != null) {
            color = color.add(calcGlobalEffect(reflectionRay, level, k, intersection.material.kR));
        }

        return color;
    }

    /**
     * Calculates a single global effect color contribution from a secondary ray.
     *
     * @param ray   the secondary ray (reflection or refraction)
     * @param level the remaining recursion level
     * @param k     the current cumulative attenuation factor
     * @param kx    the material coefficient for this specific effect (kR or kT)
     * @return the calculated global effect color
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = kx.product(k);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K) || level <= 1) {
            return Color.BLACK;
        }

        Intersection closestIntersection = findClosestIntersection(ray);
        if (closestIntersection == null) {
            return _scene.background.scale(kx);
        }

        if (!preprocessIntersection(closestIntersection, ray.direction())) {
            return _scene.background.scale(kx);
        }

        return calcColor(closestIntersection, level - 1, kkx).scale(kx);
    }

    /**
     * Finds the closest intersection point for a given ray.
     *
     * @param ray the ray to trace
     * @return the closest intersection point, or null if no intersections found
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null ? null : ray.findClosestIntersection(intersections);
    }

    /**
     * Constructs a refraction ray pointing in the same direction as the incoming ray (Updated in Section 4).
     *
     * @param intersection the intersection point
     * @param v            the incoming ray direction vector
     * @return the constructed refraction ray
     */
    private Ray constructRefractionRay(Intersection intersection, Vector v) {
        // Collided / Removed lines from previous stage (Replaced with the new Ray constructor):
        // Vector normalShift = intersection.n.scale(intersection.nv > 0 ? DELTA : -DELTA);
        // Point rayOrigin = intersection.point.add(normalShift);
        // return new Ray(rayOrigin, v);

        return new Ray(intersection.point, v, intersection.n);
    }

    /**
     * Constructs a reflection ray mirrored across the surface normal vector (Updated in Section 4).
     *
     * @param intersection the intersection point
     * @param v            the incoming ray direction vector
     * @return the constructed reflection ray, or null if orthogonal
     */
    private Ray constructReflectionRay(Intersection intersection, Vector v) {
        double vn = v.dotProduct(intersection.n);
        if (Util.isZero(vn)) {
            return null;
        }
        // r = v - 2 * (v * n) * n
        Vector r = v.subtract(intersection.n.scale(2 * vn)).normalize();

        // Collided / Removed lines from previous stage (Replaced with the new Ray constructor):
        // Vector normalShift = intersection.n.scale(vn > 0 ? -DELTA : DELTA);
        // Point rayOrigin = intersection.point.add(normalShift);
        // return new Ray(rayOrigin, r);

        return new Ray(intersection.point, r, intersection.n);
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

        // Collided / Removed lines from previous stage (Replaced with the new Ray constructor per Section 4):
        // Vector normalShift = intersection.n.scale(intersection.nl > 0 ? -DELTA : DELTA);
        // Point rayOrigin = intersection.point.add(normalShift);
        // Ray shadowRay = new Ray(rayOrigin, lightDirection);

        Ray shadowRay = new Ray(intersection.point, lightDirection, intersection.n);

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