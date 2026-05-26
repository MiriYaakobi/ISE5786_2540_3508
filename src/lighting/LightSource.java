package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface for all external light sources in the scene.
 * Defines the necessary methods to calculate lighting on geometric bodies.
 *
 * @author Miri and Yael
 */
public interface LightSource {

    /**
     * Gets the intensity of the light at a specific geometric point.
     *
     * @param p the point in the scene
     * @return the color/intensity of the light reaching the point
     */
    Color getIntensity(Point p);

    /**
     * Gets the normalized direction vector (L) from the light source to a specific point.
     *
     * @param p the point in the scene
     * @return the normalized direction vector pointing towards the geometry
     */
    Vector getL(Point p);

    /**
     * Gets the distance from the light source to a specific geometric point.
     *
     * @param p the point in the scene
     * @return the distance from the light source to the point
     */
    double getDistance(Point p);
}