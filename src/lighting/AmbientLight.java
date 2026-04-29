package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * Ambient Light for the entire scene.
 * This class is immutable.
 * * @author Miri and Yael
 */
public class AmbientLight {
    /**
     * The intensity of the light after scaling
     */
    private final Color _intensity;

    /**
     * Constant representing no ambient light (Black intensity)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK, Double3.ZERO);

    /**
     * Constructor for AmbientLight using Double3 coefficient.
     * Calculated as: I = i0 * ka
     * * @param i0 the base color of the light
     *
     * @param ka the intensity factor (coefficient)
     */
    public AmbientLight(Color i0, Double3 ka) {
        this._intensity = i0.scale(ka);
    }

    /**
     * Constructor for AmbientLight using double coefficient.
     * Calculated as: I = i0 * ka
     * * @param i0 the base color of the light
     *
     * @param ka the intensity factor (coefficient)
     */
    public AmbientLight(Color i0, double ka) {
        this._intensity = i0.scale(ka);
    }

    /**
     * Getter for the light intensity.
     * * @return the final intensity color
     */
    public Color getIntensity() {
        return _intensity;
    }
}