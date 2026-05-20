package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import scene.Scene;

/**
 * Abstract base class for ray tracing in a scene.
 * <p>
 * Defines the common infrastructure for all ray tracer implementations.
 * </p>
 *
 * @author Miri and Yael
 */
public abstract class RayTracerBase {
    /**
     * The scene to trace rays in
     */
    protected final Scene _scene;

    /**
     * Constructor for RayTracerBase.
     *
     * @param scene the scene to be rendered
     */
    public RayTracerBase(Scene scene) {
        _scene = scene;
    }

    /**
     * Traces the ray and calculates the color of the point it hits.
     *
     * @param ray the ray to trace
     * @return the color of the point
     */
    public abstract Color traceRay(Ray ray);

    /**
     * Pre-calculates the variables that depend on the intersection and the ray direction.
     *
     * @param intersection the intersection point data
     * @param rayDirection the direction of the camera ray
     * @return true if the ray and normal are not orthogonal (nv != 0)
     */
    protected boolean preprocessIntersection(Intersection intersection, Vector rayDirection) {
        intersection.v = rayDirection;
        intersection.n = intersection.geometry.getNormal(intersection.point);
        intersection.nv = Util.alignZero(intersection.n.dotProduct(intersection.v));

        // The ray is valid for shading only if it's not perfectly parallel to the surface
        return !Util.isZero(intersection.nv);
    }

    /**
     * Pre-calculates the variables that depend on the light source.
     *
     * @param intersection the intersection point data
     * @param lightSource  the external light source
     * @return true if the light hits the geometry from the correct side
     */
    protected boolean preprocessLightSource(Intersection intersection, LightSource lightSource) {
        intersection.l = lightSource.getL(intersection.point);
        intersection.nl = Util.alignZero(intersection.n.dotProduct(intersection.l));

        // The light affects the point only if both the light and the camera
        // hit the surface from the same side (sign of dot products match)
        return Util.alignZero(intersection.nl * intersection.nv) > 0;
    }
}