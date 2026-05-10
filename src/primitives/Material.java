package primitives;

/**
 * Represents the material properties of a geometric body.
 * This is a PDS (Plain Data Structure) class.
 *
 * @author Miri and Yael
 */
public class Material {
    /**
     * Ambient reflection coefficient.
     * Default value is Double3.ONE.
     */
    public Double3 kA = Double3.ONE;

    /**
     * Default constructor (empty, to avoid Javadoc Generator warnings).
     */
    public Material() {
    }

    /**
     * Setter for ambient reflection coefficient.
     *
     * @param kA the ambient reflection coefficient
     * @return the Material object itself (for chaining)
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Setter for ambient reflection coefficient (accepts a double value).
     *
     * @param kA the ambient reflection coefficient
     * @return the Material object itself (for chaining)
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}
