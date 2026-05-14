package renderer;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;
import scene.SceneXmlParser;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

/**
 * Test rendering a basic image, including XML parsing tests.
 *
 * @author Dan, Miri and Yael
 */
@SuppressWarnings("java:S109")
class RenderStage6Tests {
    /**
     * Default constructor for the RenderStage6Tests class.
     * Required for Javadoc generation under strict flags.
     */
    RenderStage6Tests() {
    }

    /**
     * The Z-axis coordinate for all test points.
     */
    private static final double Z = -100D;
    /**
     * Top-left corner point for test polygons.
     */
    private static final Point P_LT = new Point(-100, 100, Z);
    /**
     * Left-middle point for test polygons.
     */
    private static final Point P_LM = new Point(-100, 0, Z);
    /**
     * Bottom-left corner point for test polygons.
     */
    private static final Point P_LB = new Point(-100, -100, Z);
    /**
     * Middle-top point for test polygons.
     */
    private static final Point P_MT = new Point(0, 100, Z);
    /**
     * Middle-bottom point for test polygons.
     */
    private static final Point P_MB = new Point(0, -100, Z);
    /**
     * Right-middle point for test polygons.
     */
    private static final Point P_RM = new Point(100, 0, Z);
    /**
     * Bottom-right corner point for test polygons.
     */
    private static final Point P_RB = new Point(100, -100, Z);
    /**
     * Origin point (0,0,Z) for test spheres.
     */
    private static final Point O = new Point(0, 0, Z);

    /**
     * Image resolution for all test renderings.
     */
    private static int RESOLUTION = 1000;

    /**
     * Helper method to create and save a rendered image of a scene.
     *
     * @param scene    the scene to render
     * @param fileName the name of the output image file
     */
    private static void createImage(Scene scene, String fileName) {
        Camera.getBuilder()
                .setResolution(RESOLUTION, RESOLUTION)
                .setLocation(Point.ZERO).setDirection(new Point(0, 0, -1), Vector.AXIS_Y)
                .setVpDistance(100).setVpSize(500, 500)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .printGrid(100, new Color(WHITE))
                .writeToImage(fileName);
    }

    /**
     * Test for rendering a scene with various emission colors assigned to geometries.
     * Verifies that the emission light component is correctly calculated.
     */
    @Test
    void testRenderEmissionColor() {
        Scene scene = new Scene("Emission color").setAmbientLight(new AmbientLight(new Color(51, 51, 51)));
        scene.geometries.add(
                new Sphere(O, 50),
                new Triangle(P_LM, P_MT, P_LT).setEmission(new Color(GREEN)),
                new Triangle(P_LM, P_MB, P_LB).setEmission(new Color(RED)),
                new Triangle(P_RM, P_MB, P_RB).setEmission(new Color(BLUE))
        );
        createImage(scene, "emission render test");
    }

    /**
     * Test for rendering a scene with ambient light and material attenuation (kA).
     * Verifies the integration of ambient lighting and material properties.
     */
    @Test
    void testRenderAmbientColor() {
        Scene scene = new Scene("Ambient colors").setAmbientLight(new AmbientLight(new Color(WHITE)));
        scene.geometries.add(
                new Sphere(O, 50).setMaterial(new Material().setKa(0.4)),
                new Triangle(P_LM, P_MT, P_LT).setMaterial(new Material().setKa(new primitives.Double3(0, 0.8, 0))),
                new Triangle(P_LM, P_MB, P_LB).setMaterial(new Material().setKa(new primitives.Double3(0.8, 0, 0))),
                new Triangle(P_RM, P_MB, P_RB).setMaterial(new Material().setKa(new primitives.Double3(0, 0, 0.8)))
        );
        createImage(scene, "ambient render test");
    }

    /**
     * Test rendering a scene loaded from an XML file.
     */
    @Test
    void testRenderXml() {
        // Load the scene from the XML file
        Scene scene = SceneXmlParser.parse("XML Test Scene", "xml/basicRenderTestTwoColors.xml");

        // Render the scene
        createImage(scene, "render test xml");
    }
}