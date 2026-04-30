package lighting;

import primitives.Color;

/**
 * Ambient Light for the entire scene.
 * This class is immutable.
 *
 * @author Miri and Yael
 */
public class AmbientLight {
    /**
     * The intensity of the light
     */
    private final Color _intensity;

    /**
     * Constant representing no ambient light (Black intensity)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor for AmbientLight.
     *
     * @param intensity the color intensity of the light
     */
    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Getter for the light intensity.
     *
     * @return the final intensity color
     */
    public Color getIntensity() {
        return _intensity;
    }
}