package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a directional light source (like the sun).
 * The light has a direction and intensity, but no specific position.
 * The intensity is not attenuated by distance.
 *
 * @author Miri and Yael
 */
public class DirectionalLight extends Light implements LightSource {

    /**
     * The direction of the light
     */
    private final Vector _direction;

    /**
     * Constructor for DirectionalLight.
     *
     * @param intensity the color intensity of the light
     * @param direction the direction of the light
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        // We normalize the direction vector in the constructor to save calculations later
        this._direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        // Directional light intensity doesn't change with distance
        return this.getIntensity();
    }

    @Override
    public Vector getL(Point p) {
        // The direction of the light is the same for every point in the scene
        return _direction;
    }
}