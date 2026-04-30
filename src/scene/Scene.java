package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * Scene class holding all the objects and lighting data.
 * This class follows the PDS (Plain Data Structure) approach.
 * * @author Miri and Yael
 */
public class Scene {
    /**
     * Scene name
     */
    public final String name;
    /**
     * Background color, default is Black
     */
    public Color background = Color.BLACK;
    /**
     * Ambient light, default is NONE
     */
    public AmbientLight ambientLight = AmbientLight.NONE;
    /**
     * Collection of geometries in the scene
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructor for Scene.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Set the background color of the scene.
     *
     * @param background the background color
     * @return the scene object itself (Fluent API)
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Set the ambient light of the scene.
     *
     * @param ambientLight the ambient light
     * @return the scene object itself (Fluent API)
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Set the geometries collection of the scene.
     *
     * @param geometries the collection of geometries
     * @return the scene object itself (Fluent API)
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}
