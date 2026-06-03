package renderer;

import java.util.Random;

import geometries.impl.Cylinder;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import geometries.impl.Tube;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * Final presentation tests including Stage 8 requirements and Bonuses 1 &amp; 2.
 * Showcases a complex scene ("Magic Night Castle") with all geometry types,
 * shadows, transparency, reflection, and multiple light sources.
 *
 * @author Miri and Yael
 */
public class PictureTests {
    /**
     * Default constructor for PictureTests.
     */
    public PictureTests() {
    }

    /**
     * Builds a complex magical scene including a castle, lake, bridge, and celestial elements.
     * The scene incorporates various geometries, materials, and light sources to showcase rendering capabilities.
     *
     * @return the constructed magical scene
     */
    private Scene buildMagicCastleScene() {
        Scene scene = new Scene("Magic Night Castle");

        // --- 1. Background and Ambient Light (Twilight Sky) ---
        scene.setBackground(new Color(25, 20, 45)); // Lighter twilight purple-blue
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 30)));

        // --- 2. Materials ---
        Material waterMat = new Material().setKD(0.2).setKS(0.5).setShininess(100).setKR(0.25);
        Material wallMat = new Material().setKD(0.6).setKS(0.2).setShininess(30);
        Material roofMat = new Material().setKD(0.5).setKS(0.5).setShininess(80);
        Material bridgeMat = new Material().setKD(0.7).setKS(0.1).setShininess(10);
        Material glassMat = new Material().setKD(0.05).setKS(0.9).setShininess(120).setKT(0.85);
        Material glowMat = new Material().setKD(0).setKS(0).setShininess(0);

        // --- 3. Colors ---
        Color castleColor = new Color(90, 110, 150); // Lighter, dreamy slate blue
        Color roofColor = new Color(90, 50, 160); // Deep magical purple
        Color bridgeColor = new Color(70, 50, 40); // Wooden/Stone bridge
        Color waterColor = new Color(15, 20, 40);
        Color glassColor = new Color(20, 40, 50);
        Color starColor = new Color(255, 255, 255);

        // --- 4. Geometries ---

        // The Lake
        Polygon lake = (Polygon) new Polygon(
                new Point(-1000, 0, 1000), new Point(1000, 0, 1000),
                new Point(1000, 0, -1000), new Point(-1000, 0, -1000)
        ).setEmission(waterColor).setMaterial(waterMat);

        // --- THE BRIDGE (Shortened slightly to Z=150) ---
        // Top surface
        Polygon bridgeTop = (Polygon) new Polygon(
                new Point(-15, 2, 1), new Point(15, 2, 1),
                new Point(25, 2, 150), new Point(-25, 2, 150)
        ).setEmission(bridgeColor).setMaterial(bridgeMat);

        // Left wall of the bridge going into the water
        Polygon bridgeLeft = (Polygon) new Polygon(
                new Point(-15, 2, 1), new Point(-25, 2, 150),
                new Point(-25, -5, 150), new Point(-15, -5, 1)
        ).setEmission(bridgeColor).setMaterial(bridgeMat);

        // Right wall of the bridge going into the water
        Polygon bridgeRight = (Polygon) new Polygon(
                new Point(15, 2, 1), new Point(25, 2, 150),
                new Point(25, -5, 150), new Point(15, -5, 1)
        ).setEmission(bridgeColor).setMaterial(bridgeMat);


        // --- CASTLE BASE ---
        Polygon frontWall = (Polygon) new Polygon(
                new Point(-60, 0, 0), new Point(60, 0, 0),
                new Point(60, 50, 0), new Point(-60, 50, 0)
        ).setEmission(castleColor).setMaterial(wallMat);

        Polygon leftWall = (Polygon) new Polygon(
                new Point(-60, 0, 0), new Point(-60, 50, 0),
                new Point(-60, 50, -40), new Point(-60, 0, -40)
        ).setEmission(castleColor).setMaterial(wallMat);

        Polygon rightWall = (Polygon) new Polygon(
                new Point(60, 0, 0), new Point(60, 50, 0),
                new Point(60, 50, -40), new Point(60, 0, -40)
        ).setEmission(castleColor).setMaterial(wallMat);

        Polygon gate = (Polygon) new Polygon(
                new Point(-15, 0, 1), new Point(15, 0, 1),
                new Point(15, 25, 1), new Point(-15, 25, 1)
        ).setEmission(new Color(10, 5, 15)).setMaterial(wallMat);


        // --- TOWERS ---
        Cylinder mainTower = (Cylinder) new Cylinder(
                25d, new Ray(new Point(0, 0, -20), new Vector(0, 1, 0)), 100d
        ).setEmission(castleColor).setMaterial(wallMat);

        Cylinder leftTower = (Cylinder) new Cylinder(
                16d, new Ray(new Point(-60, 0, 0), new Vector(0, 1, 0)), 70d
        ).setEmission(castleColor).setMaterial(wallMat);

        Cylinder rightTower = (Cylinder) new Cylinder(
                16d, new Ray(new Point(60, 0, 0), new Vector(0, 1, 0)), 70d
        ).setEmission(castleColor).setMaterial(wallMat);


        // --- PURPLE ROOFS ---
        Point mTip = new Point(0, 150, -20);
        Point mP1 = new Point(25, 100, 5);
        Point mP2 = new Point(25, 100, -45);
        Point mP3 = new Point(-25, 100, -45);
        Point mP4 = new Point(-25, 100, 5);

        Triangle mRoof1 = (Triangle) new Triangle(mP4, mP1, mTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle mRoof2 = (Triangle) new Triangle(mP1, mP2, mTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle mRoof3 = (Triangle) new Triangle(mP2, mP3, mTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle mRoof4 = (Triangle) new Triangle(mP3, mP4, mTip).setEmission(roofColor).setMaterial(roofMat);

        Point lTip = new Point(-60, 110, 0);
        Point lP1 = new Point(-44, 70, 16);
        Point lP2 = new Point(-44, 70, -16);
        Point lP3 = new Point(-76, 70, -16);
        Point lP4 = new Point(-76, 70, 16);

        Triangle lRoof1 = (Triangle) new Triangle(lP4, lP1, lTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle lRoof2 = (Triangle) new Triangle(lP1, lP2, lTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle lRoof3 = (Triangle) new Triangle(lP2, lP3, lTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle lRoof4 = (Triangle) new Triangle(lP3, lP4, lTip).setEmission(roofColor).setMaterial(roofMat);

        Point rTip = new Point(60, 110, 0);
        Point rP1 = new Point(76, 70, 16);
        Point rP2 = new Point(76, 70, -16);
        Point rP3 = new Point(44, 70, -16);
        Point rP4 = new Point(44, 70, 16);

        Triangle rRoof1 = (Triangle) new Triangle(rP4, rP1, rTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle rRoof2 = (Triangle) new Triangle(rP1, rP2, rTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle rRoof3 = (Triangle) new Triangle(rP2, rP3, rTip).setEmission(roofColor).setMaterial(roofMat);
        Triangle rRoof4 = (Triangle) new Triangle(rP3, rP4, rTip).setEmission(roofColor).setMaterial(roofMat);

        // --- MAGIC ELEMENTS ---
        Sphere magicBubble1 = (Sphere) new Sphere(new Point(-60, 120, 40), 12)
                .setEmission(glassColor).setMaterial(glassMat);
        Sphere magicBubble2 = (Sphere) new Sphere(new Point(70, 100, 50), 9)
                .setEmission(glassColor).setMaterial(glassMat);

        // Two crossing magic rings (Tubes)
        Tube magicRing1 = (Tube) new Tube(
                1.2d, new Ray(new Point(-200, 130, 80), new Vector(1, 0.1, -0.2))
        ).setEmission(new Color(0, 255, 200)).setMaterial(glowMat); // Cyan ring

        Tube magicRing2 = (Tube) new Tube(
                0.8d, new Ray(new Point(200, 100, 50), new Vector(-1, 0.15, -0.1))
        ).setEmission(new Color(255, 0, 150)).setMaterial(glowMat); // Pink ring


        // Add core geometries
        scene.geometries.add(lake, bridgeTop, bridgeLeft, bridgeRight, frontWall, leftWall, rightWall, gate,
                mainTower, leftTower, rightTower,
                mRoof1, mRoof2, mRoof3, mRoof4,
                lRoof1, lRoof2, lRoof3, lRoof4,
                rRoof1, rRoof2, rRoof3, rRoof4,
                magicBubble1, magicBubble2, magicRing1, magicRing2);

        // --- 50 STARS LOOP ---
        Random rnd = new Random(42); // Fixed seed so it looks exactly the same every run
        for (int i = 0; i < 300; i++) {
            double x = rnd.nextDouble() * 2000 - 1000;
            double y = rnd.nextDouble() * 500;
            double z = rnd.nextDouble() * -800 - 200;
            double radius = rnd.nextDouble() * 0.8 + 0.3;

            scene.geometries.add(new Sphere(new Point(x, y, z), radius).setEmission(starColor).setMaterial(glowMat));
        }

        // --- LIGHTS ---
        scene.lights.add(new DirectionalLight(new Color(60, 70, 90), new Vector(-1, -1, -0.5)));

        scene.lights.add(new PointLight(new Color(0, 255, 200), new Point(-60, 120, 40))
                .setKl(0.005).setKq(0.0001));

        scene.lights.add(new PointLight(new Color(255, 0, 150), new Point(70, 100, 50))
                .setKl(0.008).setKq(0.0002));

        scene.lights.add(new SpotLight(new Color(200, 200, 255), new Point(0, 40, 200), new Vector(0, 0, -1))
                .setKl(0.001).setKq(0.00005).setNarrowBeam(2));

        return scene;
    }

    /**
     * Generates a front-view showcase image of the Magic Night Castle scene.
     * Renders and saves the image to "magicCastle_Front_Final_V2.png".
     */
    @Test
    void testMagicCastleShowcase() {
        Scene scene = buildMagicCastleScene();

        Camera.Builder cameraBuilder = Camera.getBuilder()
                // MOVED BACK TO FIT EVERYTHING (Z = 600, Y = 70)
                .setLocation(new Point(0, 70, 600))
                .setDirection(new Point(0, 45, 0))
                .setVpDistance(400)
                .setVpSize(220, 220)
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.SIMPLE);

        cameraBuilder.build()
                .renderImage()
                .writeToImage("magicCastle_Front_Final_V2");
    }

    /**
     * Generates multi-angle views of the Magic Night Castle scene, including a right-side view
     * and a low dramatic tilted view.
     * Renders and saves the images to "magicCastle_RightAngle_Final_V2.png" and "magicCastle_LowDramaticTilt_Final_V2.png".
     */
    @Test
    void testMagicCastleMultiAngle() {
        Scene scene = buildMagicCastleScene();

        Camera.Builder cameraBuilderRight = Camera.getBuilder()
                .setLocation(new Point(300, 80, 400))
                .setDirection(new Point(-30, 45, -20))
                .setVpDistance(400)
                .setVpSize(220, 220)
                .setResolution(600, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE);

        cameraBuilderRight.build()
                .renderImage()
                .writeToImage("magicCastle_RightAngle_Final_V2");

        Camera.Builder cameraBuilderLow = Camera.getBuilder()
                .setLocation(new Point(-200, 25, 450))
                .setDirection(new Point(0, 50, 0))
                .setVpDistance(400)
                .setVpSize(220, 220)
                .setResolution(600, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .rotate(5); // Slight dramatic tilt

        cameraBuilderLow.build()
                .renderImage()
                .writeToImage("magicCastle_LowDramaticTilt_Final_V2");
    }
}