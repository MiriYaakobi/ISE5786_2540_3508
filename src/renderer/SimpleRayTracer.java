package renderer;

import java.util.LinkedList;
import java.util.List;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import renderer.sampling.JitterSampler;
import renderer.sampling.TargetAreaSampler;
import scene.Scene;

/**
 * A simple implementation of a ray tracer.
 * Includes Phong reflection model (diffuse and specular), shadows, transparency,
 * recursive global effects, and Stage 9 Glossy Surfaces / Diffuse Glass (Blurry).
 *
 * @author Miri and Yael
 */
public class SimpleRayTracer extends RayTracerBase {

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
     * Number of rays in the beam for Glossy/Blurry effects.
     */
    private int beamRays = 1;

    /**
     * Distance to the virtual target area for Glossy/Blurry effects.
     */
    private double beamDistance = 50;

    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Sets the number of rays in the beam for blurry effects.
     *
     * @param rays the number of rays
     * @return the SimpleRayTracer itself
     */
    public SimpleRayTracer setBeamRays(int rays) {
        if (rays < 1) throw new IllegalArgumentException("Beam rays must be at least 1");
        this.beamRays = rays;
        return this;
    }

    /**
     * Sets the target distance for the virtual target area in blurry effects.
     *
     * @param distance the distance to the target area
     * @return the SimpleRayTracer itself
     */
    public SimpleRayTracer setBeamDistance(double distance) {
        if (distance <= 0) throw new IllegalArgumentException("Beam distance must be positive");
        this.beamDistance = distance;
        return this;
    }

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
        return intersection.geometry.getEmission()
                .add(calcLocalEffects(intersection, k))
                .add(calcGlobalEffects(intersection, intersection.v, level, k));
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
            if (preprocessLightSource(intersection, lightSource)) {
                Double3 ktr = transparency(intersection, lightSource);

                if (!ktr.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);

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
     * Stage 9: Evaluates the global effects (reflection and transmission).
     * Now supports generating a beam of rays for Glossy Surfaces and Diffuse Glass if material blur > 0.
     * Includes a safeguard to prevent combinatorial explosion on deep recursion.
     *
     * @param intersection the intersection point
     * @param v            the incoming ray direction vector
     * @param level        the remaining recursion level
     * @param k            the cumulative attenuation factor
     * @return the combined global effects color
     */
    private Color calcGlobalEffects(Intersection intersection, Vector v, int level, Double3 k) {
        Color color = Color.BLACK;

        // SAFEGUARD: Only shoot a full beam on the first reflection/refraction hit.
        // Subsequent recursive bounces just use 1 ray to prevent combinatorial explosion!
        int actualBeamRays = (level == MAX_CALC_COLOR_LEVEL) ? beamRays : 1;

        // --- Refraction (Transparency / Diffuse Glass) ---
        if (!intersection.material.kT.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
            Ray idealRefractionRay = constructRefractionRay(intersection, v);
            double blurRadius = intersection.material.blur;

            if (blurRadius > 0 && actualBeamRays > 1) {
                List<Ray> beam = constructBeam(idealRefractionRay, blurRadius, intersection.n, actualBeamRays);
                Color averageColor = Color.BLACK;
                for (Ray r : beam) {
                    averageColor = averageColor.add(calcGlobalEffect(r, level, k, intersection.material.kT));
                }
                color = color.add(averageColor.reduce(beam.size()));
            } else {
                color = color.add(calcGlobalEffect(idealRefractionRay, level, k, intersection.material.kT));
            }
        }

        // --- Reflection (Mirror / Glossy Surface) ---
        if (!intersection.material.kR.product(k).isLowerThan(MIN_CALC_COLOR_K)) {
            Ray idealReflectionRay = constructReflectionRay(intersection, v);
            if (idealReflectionRay != null) {
                double blurRadius = intersection.material.blur;

                if (blurRadius > 0 && actualBeamRays > 1) {
                    List<Ray> beam = constructBeam(idealReflectionRay, blurRadius, intersection.n, actualBeamRays);
                    Color averageColor = Color.BLACK;
                    for (Ray r : beam) {
                        averageColor = averageColor.add(calcGlobalEffect(r, level, k, intersection.material.kR));
                    }
                    color = color.add(averageColor.reduce(beam.size()));
                } else {
                    color = color.add(calcGlobalEffect(idealReflectionRay, level, k, intersection.material.kR));
                }
            }
        }

        return color;
    }

    /**
     * Stage 9: Constructs a beam of rays around an ideal ray for Glossy/Blurry effects.
     * Uses the TargetAreaSampler infrastructure to ensure DRY and RDD.
     * Pure OOP implementation using cross-products instead of raw coordinate getters.
     *
     * @param idealRay       the central (ideal) ray
     * @param blurRadius     the radius of the target area (level of blur)
     * @param normal         the normal vector of the surface (used to filter out rays going the wrong way)
     * @param actualBeamRays the number of rays to shoot
     * @return a list of rays representing the beam
     */
    private List<Ray> constructBeam(Ray idealRay, double blurRadius, Vector normal, int actualBeamRays) {
        List<Ray> beam = new LinkedList<>();
        Point p0 = idealRay.origin();
        Vector vTo = idealRay.direction();

        // Calculate the center of the virtual target area
        Point targetCenter = p0.add(vTo.scale(beamDistance));

        // Create the local coordinate system for the target area (Pure OOP vector math)
        Vector vUp;
        try {
            // Try to create an orthogonal vector using the global Y axis
            vUp = vTo.crossProduct(new Vector(0, 1, 0)).normalize();
        } catch (IllegalArgumentException e) {
            // If vTo is parallel to the Y axis, use the global X axis instead
            vUp = vTo.crossProduct(new Vector(1, 0, 0)).normalize();
        }
        Vector vRight = vTo.crossProduct(vUp).normalize();

        // Use the generic sampling infrastructure
        int gridSize = (int) Math.ceil(Math.sqrt(actualBeamRays));
        TargetAreaSampler sampler = new JitterSampler(gridSize);
        // Using blurRadius as width and height for the sampling area
        List<TargetAreaSampler.Point2D> offsets = sampler.generatePoints(blurRadius, blurRadius);
        List<Point> targetPoints = sampler.mapPointsTo3D(targetCenter, vUp, vRight, offsets);

        // Construct rays and filter out those that cross the surface (wrong side)
        for (Point p : targetPoints) {
            Vector dir = p.subtract(p0);
            // Check if the ray and the normal point in the same general direction
            if (Util.alignZero(dir.dotProduct(normal) * vTo.dotProduct(normal)) > 0) {
                beam.add(new Ray(p0, dir));
            }
        }

        // Ensure there's at least one ray if all were filtered out
        if (beam.isEmpty()) {
            beam.add(idealRay);
        }

        return beam;
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
     * Constructs a refraction ray pointing in the same direction as the incoming ray.
     *
     * @param intersection the intersection point
     * @param v            the incoming ray direction vector
     * @return the constructed refraction ray
     */
    private Ray constructRefractionRay(Intersection intersection, Vector v) {
        return new Ray(intersection.point, v, intersection.n);
    }

    /**
     * Constructs a reflection ray mirrored across the surface normal vector.
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
        Vector r = v.subtract(intersection.n.scale(2 * vn));
        return new Ray(intersection.point, r, intersection.n);
    }

    /**
     * Evaluates the transparency/shadow factor between a light source and the intersection point.
     * Uses the maxDistance parameter (Bonus) to optimize intersection calculations.
     *
     * @param intersection the intersection point
     * @param lightSource  an external light source
     * @return the transparency coefficient Double3
     */
    private Double3 transparency(Intersection intersection, LightSource lightSource) {
        Vector lightDirection = intersection.l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, lightDirection, intersection.n);

        // Distance from the point to the light source calculated BEFORE intersections
        double lightDistance = lightSource.getDistance(intersection.point);

        // Pass lightDistance to calcIntersections (Bonus 3 Optimization)
        List<Intersection> intersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);

        if (intersections == null) {
            return Double3.ONE;
        }

        Double3 ktr = Double3.ONE;

        for (Intersection gp : intersections) {
            // Check distance to ensure geometry is between point and light (Safety check)
            if (Util.alignZero(gp.point.distance(intersection.point) - lightDistance) <= 0) {
                ktr = ktr.product(gp.material.kT);
                if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
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
        Vector r = intersection.l.subtract(intersection.n.scale(2 * intersection.nl)).normalize();
        double minusVR = Util.alignZero(-intersection.v.dotProduct(r));

        if (minusVR <= 0) {
            return Double3.ZERO;
        }

        double factor = Math.pow(minusVR, intersection.material.nShininess);
        return intersection.material.kS.scale(factor);
    }
}