package lighting;

import primitives.Color;

/**
 * Ambient Light for the entire scene.
 * This class is immutable.
 *
 * @author Miri and Yael
 */
public class AmbientLight extends Light {

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
        super(intensity);
    }
}