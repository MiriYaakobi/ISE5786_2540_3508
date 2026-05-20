package lighting;

import primitives.Color;

/**
 * Abstract base class for all lights in the scene.
 *
 * @author Miri and Yael
 */
public abstract class Light {
    /**
     * The intensity of the light
     */
    protected final Color _intensity;

    /**
     * Constructor for Light.
     *
     * @param intensity the color intensity of the light
     */
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Getter for the light intensity.
     *
     * @return the intensity color
     */
    public Color getIntensity() {
        return _intensity;
    }
}