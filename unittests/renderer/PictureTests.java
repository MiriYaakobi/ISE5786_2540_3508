package renderer;

import java.util.Random;

import geometries.impl.Cylinder;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
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
 * Final presentation tests including Stage 8 requirements and Bonuses 1 & 2.
 * High-Detail "Magic Castle" scene. Features 3D thick bridge planks, continuous ropes,
 * seamless wall-to-tower connections, and perfectly matched unified colors.
 * Now fully supporting Stage 9 (Multi-threading + Adaptive Super-Sampling).
 *
 * @author Miri and Yael
 */
public class PictureTests {

    public PictureTests() {
    }

    private void buildWoodenBridge(Scene scene, Color woodColor, Color ropeColor, Material woodMat) {
        int planks = 24;
        double zStart = 2;
        double zEnd = 160;
        double zStep = (zEnd - zStart) / planks;

        scene.geometries.add(new Cylinder(1.2, new Ray(new Point(-11, 0, zStart), new Vector(0, 0, 1)), zEnd - zStart).setEmission(woodColor).setMaterial(woodMat));
        scene.geometries.add(new Cylinder(1.2, new Ray(new Point(11, 0, zStart), new Vector(0, 0, 1)), zEnd - zStart).setEmission(woodColor).setMaterial(woodMat));

        for (int i = 0; i < planks; i++) {
            double z = zStart + i * zStep;

            scene.geometries.add(new Polygon(
                    new Point(-13.5, 2.5, z + 0.8), new Point(13.5, 2.5, z + 0.8),
                    new Point(13.5, 2.5, z + zStep - 0.8), new Point(-13.5, 2.5, z + zStep - 0.8)
            ).setEmission(woodColor).setMaterial(woodMat));

            scene.geometries.add(new Polygon(
                    new Point(-13.5, 1.5, z + 0.8), new Point(13.5, 1.5, z + 0.8),
                    new Point(13.5, 2.5, z + 0.8), new Point(-13.5, 2.5, z + 0.8)
            ).setEmission(woodColor).setMaterial(woodMat));

            scene.geometries.add(new Polygon(
                    new Point(-13.5, 1.5, z + zStep - 0.8), new Point(13.5, 1.5, z + zStep - 0.8),
                    new Point(13.5, 2.5, z + zStep - 0.8), new Point(-13.5, 2.5, z + zStep - 0.8)
            ).setEmission(woodColor).setMaterial(woodMat));
        }

        for (int i = 0; i <= planks; i += 4) {
            double zPost = zStart + i * zStep;

            scene.geometries.add(new Cylinder(1.2, new Ray(new Point(-12.5, -2, zPost), new Vector(0, 1, 0)), 12d).setEmission(woodColor).setMaterial(woodMat));
            scene.geometries.add(new Cylinder(1.2, new Ray(new Point(12.5, -2, zPost), new Vector(0, 1, 0)), 12d).setEmission(woodColor).setMaterial(woodMat));

            if (i + 4 <= planks) {
                double nextZ = zStart + (i + 4) * zStep;
                Vector ropeDirLeft = new Point(-12.5, 9.5, nextZ).subtract(new Point(-12.5, 9.5, zPost));
                scene.geometries.add(new Cylinder(0.6, new Ray(new Point(-12.5, 9.5, zPost), ropeDirLeft.normalize()), ropeDirLeft.length()).setEmission(ropeColor).setMaterial(woodMat));

                Vector ropeDirRight = new Point(12.5, 9.5, nextZ).subtract(new Point(12.5, 9.5, zPost));
                scene.geometries.add(new Cylinder(0.6, new Ray(new Point(12.5, 9.5, zPost), ropeDirRight.normalize()), ropeDirRight.length()).setEmission(ropeColor).setMaterial(woodMat));
            }
        }
    }

    private void buildBrickTower(Scene scene, double cx, double cy, double cz, double radius, double height, Color brick1, Color brick2, Material mat) {
        double hStep = 3.5;
        int layers = (int) (height / hStep);
        int segments = 24;
        double angleStep = 2 * Math.PI / segments;

        for (int l = 0; l < layers; l++) {
            double yBase = cy + l * hStep;
            double angleOffset = (l % 2 == 0) ? 0 : angleStep / 2.0;

            for (int s = 0; s < segments; s++) {
                double angle = s * angleStep + angleOffset;
                double nextAngle = angle + angleStep;

                double x1 = cx + radius * Math.cos(angle);
                double z1 = cz + radius * Math.sin(angle);
                double x2 = cx + radius * Math.cos(nextAngle);
                double z2 = cz + radius * Math.sin(nextAngle);

                Point p1 = new Point(x1, yBase, z1);
                Point p2 = new Point(x2, yBase, z2);
                Point p3 = new Point(x2, yBase + hStep, z2);
                Point p4 = new Point(x1, yBase + hStep, z1);

                Color currentColor = (s % 2 == 0) ? brick1 : brick2;
                scene.geometries.add(new Polygon(p1, p2, p3, p4).setEmission(currentColor).setMaterial(mat));
            }
        }
    }

    private void buildBrickWall(Scene scene, double startX, double endX, double zPos, double height, Color brick1, Color brick2, Material mat) {
        double wStep = 6.0;
        double hStep = 3.5;
        int rows = (int) (height / hStep);
        int cols = (int) (Math.abs(endX - startX) / wStep) + 1;

        for (int r = 0; r < rows; r++) {
            double yBase = r * hStep;
            double xOffset = (r % 2 == 0) ? 0 : wStep / 2.0;

            for (int c = 0; c < cols; c++) {
                double dir = (startX < endX) ? 1 : -1;
                double xBase = startX + dir * (c * wStep + xOffset);
                double nextX = xBase + dir * wStep;

                Point p1 = new Point(xBase, yBase, zPos);
                Point p2 = new Point(nextX, yBase, zPos);
                Point p3 = new Point(nextX, yBase + hStep, zPos);
                Point p4 = new Point(xBase, yBase + hStep, zPos);

                Color currentColor = (c % 2 == 0) ? brick1 : brick2;
                scene.geometries.add(new Polygon(p1, p2, p3, p4).setEmission(currentColor).setMaterial(mat));
            }
        }
    }

    private void addRecessedWindow(Scene scene, double cx, double cz, double towerRadius, double angle, double yBottom, double yTop, Color lightColor, Color frameColor, Material wallMat, Material glowMat) {
        double w = 3.5;
        double nx = Math.cos(angle);
        double nz = Math.sin(angle);
        double tx = -Math.sin(angle);
        double tz = Math.cos(angle);

        double innerRadius = towerRadius - 1.5;
        double frameRadius = towerRadius + 0.5;

        double inX = cx + innerRadius * nx;
        double inZ = cz + innerRadius * nz;
        double wtX = w * tx;
        double wtZ = w * tz;

        Point g1 = new Point(inX + wtX, yBottom, inZ + wtZ);
        Point g2 = new Point(inX - wtX, yBottom, inZ - wtZ);
        Point g3 = new Point(inX - wtX, yTop, inZ - wtZ);
        Point g4 = new Point(inX + wtX, yTop, inZ + wtZ);
        Point gTop = new Point(inX, yTop + 3.0, inZ);

        scene.geometries.add(new Polygon(g1, g2, g3, g4).setEmission(lightColor).setMaterial(glowMat));
        scene.geometries.add(new Triangle(g3, g4, gTop).setEmission(lightColor).setMaterial(glowMat));

        double fw = w + 0.8;
        double frX = cx + frameRadius * nx;
        double frZ = cz + frameRadius * nz;
        double fwtX = fw * tx;
        double fwtZ = fw * tz;

        Point f1 = new Point(frX + fwtX, yBottom - 0.5, frZ + fwtZ);
        Point f2 = new Point(frX - fwtX, yBottom - 0.5, frZ - fwtZ);
        Point f3 = new Point(frX - fwtX, yTop + 0.5, frZ - fwtZ);
        Point f4 = new Point(frX + fwtX, yTop + 0.5, frZ + fwtZ);
        Point fTop = new Point(frX, yTop + 4.0, frZ);

        scene.geometries.add(new Polygon(f1, f2, g2, g1).setEmission(frameColor).setMaterial(wallMat));
        scene.geometries.add(new Polygon(f2, f3, g3, g2).setEmission(frameColor).setMaterial(wallMat));
        scene.geometries.add(new Polygon(f4, f1, g1, g4).setEmission(frameColor).setMaterial(wallMat));
        scene.geometries.add(new Triangle(f3, fTop, gTop).setEmission(frameColor).setMaterial(wallMat));
        scene.geometries.add(new Triangle(f4, fTop, gTop).setEmission(frameColor).setMaterial(wallMat));
    }

    private void addRecessedGate(Scene scene, Color lightColor, Color frameColor, Material wallMat, Material glowMat) {
        Point g1 = new Point(-10, 0, -5);
        Point g2 = new Point(10, 0, -5);
        Point g3 = new Point(10, 26, -5);
        Point g4 = new Point(-10, 26, -5);
        scene.geometries.add(new Polygon(g1, g2, g3, g4).setEmission(lightColor).setMaterial(glowMat));

        scene.geometries.add(new Polygon(new Point(-12, 0, 1), new Point(-10, 0, 1), new Point(-10, 28, 1), new Point(-12, 28, 1)).setEmission(frameColor).setMaterial(wallMat));
        scene.geometries.add(new Polygon(new Point(10, 0, 1), new Point(12, 0, 1), new Point(12, 28, 1), new Point(10, 28, 1)).setEmission(frameColor).setMaterial(wallMat));
        scene.geometries.add(new Polygon(new Point(-12, 26, 1), new Point(12, 26, 1), new Point(12, 28, 1), new Point(-12, 28, 1)).setEmission(frameColor).setMaterial(wallMat));
    }

    private void buildShingledRoof(Scene scene, double cx, double cy, double cz, double radius, double height, Color roofColor, Material roofMat) {
        int layers = 12;
        double hStep = height / layers;
        double rStep = radius / layers;

        for (int i = 0; i < layers; i++) {
            double currentY = cy + i * hStep;
            double currentRadius = radius - i * rStep;

            Point pTop = new Point(cx, currentY + hStep * 1.7, cz);
            Point p1 = new Point(cx + currentRadius, currentY, cz + currentRadius);
            Point p2 = new Point(cx + currentRadius, currentY, cz - currentRadius);
            Point p3 = new Point(cx - currentRadius, currentY, cz - currentRadius);
            Point p4 = new Point(cx - currentRadius, currentY, cz + currentRadius);

            scene.geometries.add(new Triangle(p4, p1, pTop).setEmission(roofColor).setMaterial(roofMat));
            scene.geometries.add(new Triangle(p1, p2, pTop).setEmission(roofColor).setMaterial(roofMat));
            scene.geometries.add(new Triangle(p2, p3, pTop).setEmission(roofColor).setMaterial(roofMat));
            scene.geometries.add(new Triangle(p3, p4, pTop).setEmission(roofColor).setMaterial(roofMat));
        }
    }

    private Scene buildMagicCastleScene() {
        Scene scene = new Scene("Magic Detailed Castle");

        scene.setBackground(new Color(15, 10, 30));
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));

        Material waterMat = new Material().setKD(0.1).setKS(0.8).setShininess(100).setKR(0.5);
        Material wallMat = new Material().setKD(0.7).setKS(0.1).setShininess(5);
        Material roofMat = new Material().setKD(0.8).setKS(0.0).setShininess(0);
        Material woodMat = new Material().setKD(0.6).setKS(0.2).setShininess(15);
        Material clearGlassMat = new Material().setKD(0.05).setKS(0.9).setShininess(120).setKT(0.85);
        Material frostyGlassMat = new Material().setKD(0.2).setKS(0.5).setShininess(50).setKT(0.5).setKR(0.2);
        Material heavyGlassMat = new Material().setKD(0.3).setKS(0.6).setShininess(30).setKT(0.2).setKR(0.4);
        Material glowMat = new Material().setKD(0).setKS(0).setShininess(0);

        // --- UNIFIED COLOR PALETTE ---
        // Changed to warm, natural browns that blend the bridge and castle beautifully together
        Color brick1 = new Color(155, 115, 95);     // Warm natural stone
        Color brick2 = new Color(140, 100, 80);     // Darker warm stone
        Color frameColor = new Color(100, 70, 50);  // Dark brown window frames
        Color roofColor = new Color(50, 35, 75);    // Deep violet-night roof (contrast with warm stone)
        Color woodColor = new Color(120, 80, 55);   // Rich dark wood for the bridge
        Color ropeColor = new Color(90, 60, 40);    // Dark aged ropes
        Color waterColor = new Color(15, 20, 40);   // Deep lake water

        Color windowLight = new Color(255, 190, 60); // Bright, warm candle/fire glow

        Polygon lake = (Polygon) new Polygon(
                new Point(-1000, 0, 1000), new Point(1000, 0, 1000),
                new Point(1000, 0, -1000), new Point(-1000, 0, -1000)
        ).setEmission(waterColor).setMaterial(waterMat);
        scene.geometries.add(lake);

        buildWoodenBridge(scene, woodColor, ropeColor, woodMat);

        buildBrickTower(scene, 0, 0, -30, 30d, 105d, brick1, brick2, wallMat);
        buildBrickTower(scene, -65, 0, 0, 20d, 70d, brick1, brick2, wallMat);
        buildBrickTower(scene, 65, 0, 0, 20d, 70d, brick1, brick2, wallMat);

        buildBrickWall(scene, -65, 0, -15, 55, brick1, brick2, wallMat);
        buildBrickWall(scene, 0, 65, -15, 55, brick1, brick2, wallMat);

        addRecessedGate(scene, windowLight, frameColor, wallMat, glowMat);

        addRecessedWindow(scene, 0, -30, 30, Math.PI / 2 + 0.35, 40, 52, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(scene, 0, -30, 30, Math.PI / 2 - 0.35, 40, 52, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(scene, 0, -30, 30, Math.PI / 2 + 0.35, 75, 87, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(scene, 0, -30, 30, Math.PI / 2 - 0.35, 75, 87, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(scene, 0, -30, 30, 0, 55, 67, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(scene, 0, -30, 30, Math.PI, 55, 67, windowLight, frameColor, wallMat, glowMat);

        addRecessedWindow(scene, -65, 0, 20, Math.PI / 2, 35, 48, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(scene, -65, 0, 20, Math.PI, 35, 48, windowLight, frameColor, wallMat, glowMat);

        addRecessedWindow(scene, 65, 0, 20, Math.PI / 2, 35, 48, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(scene, 65, 0, 20, 0, 35, 48, windowLight, frameColor, wallMat, glowMat);

        buildShingledRoof(scene, 0, 105, -30, 34, 55, roofColor, roofMat);
        scene.geometries.add(new Cylinder(0.4, new Ray(new Point(0, 160, -30), new Vector(0, 1, 0)), 30d).setEmission(roofColor).setMaterial(wallMat));
        scene.geometries.add(new Triangle(new Point(0, 185, -30), new Point(0, 172, -30), new Point(22, 178, -30)).setEmission(new Color(100, 200, 255)).setMaterial(glowMat));

        buildShingledRoof(scene, -65, 70, 0, 23, 40, roofColor, roofMat);
        scene.geometries.add(new Cylinder(0.3, new Ray(new Point(-65, 110, 0), new Vector(0, 1, 0)), 20d).setEmission(roofColor).setMaterial(wallMat));
        scene.geometries.add(new Triangle(new Point(-65, 125, 0), new Point(-65, 115, 0), new Point(-50, 120, 0)).setEmission(new Color(100, 200, 255)).setMaterial(glowMat));

        buildShingledRoof(scene, 65, 70, 0, 23, 40, roofColor, roofMat);
        scene.geometries.add(new Cylinder(0.3, new Ray(new Point(65, 110, 0), new Vector(0, 1, 0)), 20d).setEmission(roofColor).setMaterial(wallMat));
        scene.geometries.add(new Triangle(new Point(65, 125, 0), new Point(65, 115, 0), new Point(80, 120, 0)).setEmission(new Color(100, 200, 255)).setMaterial(glowMat));

        scene.geometries.add(new Sphere(new Point(-120, 160, 40), 20).setEmission(new Color(20, 40, 50)).setMaterial(clearGlassMat));
        scene.geometries.add(new Sphere(new Point(-80, 130, 80), 12).setEmission(new Color(40, 20, 50)).setMaterial(frostyGlassMat));
        scene.geometries.add(new Sphere(new Point(100, 140, 60), 16).setEmission(new Color(20, 40, 50)).setMaterial(clearGlassMat));
        scene.geometries.add(new Sphere(new Point(140, 100, 30), 10).setEmission(new Color(50, 50, 50)).setMaterial(heavyGlassMat));
        scene.geometries.add(new Sphere(new Point(-30, 190, -40), 14).setEmission(new Color(40, 50, 40)).setMaterial(frostyGlassMat));
        scene.geometries.add(new Sphere(new Point(-160, 110, 10), 15).setEmission(new Color(30, 30, 60)).setMaterial(heavyGlassMat));

        Random rnd = new Random(42);
        for (int i = 0; i < 400; i++) {
            double t = i * 0.08;
            scene.geometries.add(new Sphere(new Point(-800 + i * 4, 180 + 35 * Math.sin(t), -100 + 40 * Math.cos(t)), rnd.nextDouble() * 1.5 + 0.5).setEmission(new Color(0, 255, 255)).setMaterial(glowMat));
            scene.geometries.add(new Sphere(new Point(800 - i * 4, 130 + 45 * Math.cos(t), -50 + 30 * Math.sin(t)), rnd.nextDouble() * 1.5 + 0.5).setEmission(new Color(255, 50, 200)).setMaterial(glowMat));
            scene.geometries.add(new Sphere(new Point(-600 + i * 3, 200 + 20 * Math.sin(t * 1.5), -150 + 60 * Math.cos(t * 1.2)), rnd.nextDouble() * 1.2 + 0.4).setEmission(new Color(255, 200, 100)).setMaterial(glowMat));

            double starX = (rnd.nextDouble() - 0.5) * 4000;
            double starY = rnd.nextDouble() * 800;
            double starZ = -2500 + rnd.nextDouble() * 1500;
            double starRadius = rnd.nextDouble() * 1.0 + 0.2;
            scene.geometries.add(new Sphere(new Point(starX, starY, starZ), starRadius).setEmission(new Color(255, 255, 255)).setMaterial(glowMat));
        }

        scene.lights.add(new DirectionalLight(new Color(60, 50, 110), new Vector(-0.5, -1, -0.5)));

        // --- FIXED: Lights moved OUTSIDE the towers to illuminate the bridge, water, and outer bricks! ---
        // Main tower light, floating just in front of the window (Z=15 instead of -30)
        scene.lights.add(new PointLight(windowLight, new Point(0, 50, 15)).setKl(0.003).setKq(0.0001));

        // Left tower light, floating just outside (Z=25 instead of 0)
        scene.lights.add(new PointLight(windowLight, new Point(-65, 42, 25)).setKl(0.004).setKq(0.0002));

        // Right tower light, floating just outside (Z=25 instead of 0)
        scene.lights.add(new PointLight(windowLight, new Point(65, 42, 25)).setKl(0.004).setKq(0.0002));

        // Gate SpotLight
        scene.lights.add(new SpotLight(windowLight, new Point(0, 13, 60), new Vector(0, 0, -1))
                .setKl(0.001).setKq(0.00005).setNarrowBeam(2));

        return scene;
    }

    @Test
    void testMagicCastleShowcase() {
        Scene scene = buildMagicCastleScene();

        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 80, 650))
                .setDirection(new Point(0, 35, 0))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(800, 800)
                // --- STAGE 9 MULTI-THREADING ---
                .setMultithreading(Runtime.getRuntime().availableProcessors())
                .setDebugPrint(0.1)
                // --- STAGE 9 ANTI-ALIASING WITH ADAPTIVE SUPER-SAMPLING ---
                // Now perfectly smooth AND runs fast because Adaptive skips the background!
                .setAntiAliasingRays(9)
                .setAdaptive(true)
                .setRayTracer(scene, RayTracerType.SIMPLE);

        cameraBuilder.build()
                .renderImage()
                .writeToImage("magicDetailedCastle_Front");
    }

    @Test
    void testMagicCastleMultiAngle() {
        Scene scene = buildMagicCastleScene();

        Camera.Builder cameraBuilderRight = Camera.getBuilder()
                .setLocation(new Point(350, 90, 450))
                .setDirection(new Point(-35, 35, -20))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(600, 600)
                .setMultithreading(Runtime.getRuntime().availableProcessors())
                .setDebugPrint(0.1)
                .setAntiAliasingRays(9)
                .setAdaptive(true)
                .setRayTracer(scene, RayTracerType.SIMPLE);

        cameraBuilderRight.build()
                .renderImage()
                .writeToImage("magicDetailedCastle_RightAngle");

        Camera.Builder cameraBuilderLow = Camera.getBuilder()
                .setLocation(new Point(-250, 30, 500))
                .setDirection(new Point(0, 45, 0))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(600, 600)
                .setMultithreading(Runtime.getRuntime().availableProcessors())
                .setDebugPrint(0.1)
                .setAntiAliasingRays(9)
                .setAdaptive(true)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .rotate(5);

        cameraBuilderLow.build()
                .renderImage()
                .writeToImage("magicDetailedCastle_LowDramaticTilt");
    }
}