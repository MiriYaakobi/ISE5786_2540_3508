package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracing in a scene.
 *
 * @author Miri and Yael
 */
abstract class RayTracerBase {
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
}