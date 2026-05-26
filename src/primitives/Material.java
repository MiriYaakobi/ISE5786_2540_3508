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
     */
    public Double3 kA = Double3.ONE;

    /**
     * Diffuse reflection coefficient.
     */
    public Double3 kD = Double3.ZERO;

    /**
     * Specular reflection coefficient.
     */
    public Double3 kS = Double3.ZERO;

    /**
     * Transmission (transparency) coefficient.
     */
    public Double3 kT = Double3.ZERO;

    /**
     * Shininess level of the material.
     */
    public int nShininess = 0;

    /**
     * Default constructor.
     */
    public Material() {
    }

    /**
     * Sets the ambient reflection coefficient.
     *
     * @param kA the ambient reflection coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the ambient reflection coefficient.
     *
     * @param kA the ambient reflection coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    // --- שינינו ל-KD גדול כדי שיתאים לטסט של המרצה ---

    /**
     * Sets the diffuse reflection coefficient.
     *
     * @param kD the diffuse reflection coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient.
     *
     * @param kD the diffuse reflection coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    // --- שינינו ל-KS גדול כדי שיתאים לטסט של המרצה ---

    /**
     * Sets the specular reflection coefficient.
     *
     * @param kS the specular reflection coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the specular reflection coefficient.
     *
     * @param kS the specular reflection coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the transmission (transparency) coefficient.
     *
     * @param kT the transmission coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Sets the transmission (transparency) coefficient.
     *
     * @param kT the transmission coefficient
     * @return the material object itself (Fluent API)
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the shininess level of the material.
     *
     * @param nShininess the shininess level
     * @return the material object itself (Fluent API)
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}