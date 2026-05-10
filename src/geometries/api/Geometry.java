package geometries.api;

import primitives.Color; // Added import
import primitives.Material; // Added import
import primitives.Point;
import primitives.Vector;

/**
 * This abstract class serves as the base class for all geometric bodies.
 *
 * @author Miri and Yael
 */
public abstract class Geometry extends Intersectable {

    /**
     * The emission color of the geometry.
     * Default is black (no emission).
     */
    private Color _emission = Color.BLACK; // Added emission field

    /**
     * The material properties of the geometry.
     * Default is a new Material object.
     */
    private Material _material = new Material(); // Added material field

    /**
     * Default constructor for Geometry
     */
    public Geometry() {
    }

    /**
     * Getter for the emission color.
     * @return the emission color
     */
    public Color getEmission() { // Added getter
        return _emission;
    }

    /**
     * Setter for the emission color.
     * @param emission the emission color to set
     * @return the Geometry object itself (for chaining)
     */
    public Geometry setEmission(Color emission) { // Added setter
        _emission = emission;
        return this;
    }

    /**
     * Getter for the material properties.
     * @return the material properties
     */
    public Material getMaterial() { // Added getter
        return _material;
    }

    /**
     * Setter for the material properties.
     * @param material the material properties to set
     * @return the Geometry object itself (for chaining)
     */
    public Geometry setMaterial(Material material) { // Added setter
        _material = material;
        return this;
    }

    /**
     * Calculates the normal vector to the geometry at a given point.
     *
     * @param point the point on the geometry surface
     * @return the normal vector to the geometry at the given point
     */
    public abstract Vector getNormal(Point point);
}
