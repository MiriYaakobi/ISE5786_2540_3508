package renderer;

import java.util.Random;

import geometries.impl.Cylinder;
import geometries.impl.Geometries;
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
 * Final presentation tests showcasing "Before and After" across 3 unique camera angles.
 * Now structurally refactored to support Manual Hierarchy, Flattening, and Automatic BVH
 * to strictly adhere to Stage 10 performance measurement requirements.
 *
 * @author Miri and Yael
 */
public class PictureTests {

    /**
     * Default test class constructor.
     * Initializes any required test fixtures (none required currently).
     */
    public PictureTests() {
    }

    /**
     * Builds a wooden bridge structure and adds it to the provided geometries group.
     *
     * @param geometries the Geometries collection to add bridge elements to
     * @param woodColor  the color used for wooden parts
     * @param ropeColor  the color used for rope elements
     * @param woodMat    the material to apply to wooden components
     */
    private void buildWoodenBridge(Geometries geometries, Color woodColor, Color ropeColor, Material woodMat) {
        int planks = 24;
        double zStart = 2;
        double zEnd = 160;
        double zStep = (zEnd - zStart) / planks;

        geometries.add(new Cylinder(1.2, new Ray(new Point(-11, 0, zStart), new Vector(0, 0, 1)), zEnd - zStart).setEmission(woodColor).setMaterial(woodMat));
        geometries.add(new Cylinder(1.2, new Ray(new Point(11, 0, zStart), new Vector(0, 0, 1)), zEnd - zStart).setEmission(woodColor).setMaterial(woodMat));

        for (int i = 0; i < planks; i++) {
            double z = zStart + i * zStep;

            geometries.add(new Polygon(
                    new Point(-13.5, 2.5, z + 0.8), new Point(13.5, 2.5, z + 0.8),
                    new Point(13.5, 2.5, z + zStep - 0.8), new Point(-13.5, 2.5, z + zStep - 0.8)
            ).setEmission(woodColor).setMaterial(woodMat));

            geometries.add(new Polygon(
                    new Point(-13.5, 1.5, z + 0.8), new Point(13.5, 1.5, z + 0.8),
                    new Point(13.5, 2.5, z + 0.8), new Point(-13.5, 2.5, z + 0.8)
            ).setEmission(woodColor).setMaterial(woodMat));

            geometries.add(new Polygon(
                    new Point(-13.5, 1.5, z + zStep - 0.8), new Point(13.5, 1.5, z + zStep - 0.8),
                    new Point(13.5, 2.5, z + zStep - 0.8), new Point(-13.5, 2.5, z + zStep - 0.8)
            ).setEmission(woodColor).setMaterial(woodMat));
        }

        for (int i = 0; i <= planks; i += 4) {
            double zPost = zStart + i * zStep;

            geometries.add(new Cylinder(1.2, new Ray(new Point(-12.5, -2, zPost), new Vector(0, 1, 0)), 12d).setEmission(woodColor).setMaterial(woodMat));
            geometries.add(new Cylinder(1.2, new Ray(new Point(12.5, -2, zPost), new Vector(0, 1, 0)), 12d).setEmission(woodColor).setMaterial(woodMat));

            if (i + 4 <= planks) {
                double nextZ = zStart + (i + 4) * zStep;
                Vector ropeDirLeft = new Point(-12.5, 9.5, nextZ).subtract(new Point(-12.5, 9.5, zPost));
                geometries.add(new Cylinder(0.6, new Ray(new Point(-12.5, 9.5, zPost), ropeDirLeft.normalize()), ropeDirLeft.length()).setEmission(ropeColor).setMaterial(woodMat));

                Vector ropeDirRight = new Point(12.5, 9.5, nextZ).subtract(new Point(12.5, 9.5, zPost));
                geometries.add(new Cylinder(0.6, new Ray(new Point(12.5, 9.5, zPost), ropeDirRight.normalize()), ropeDirRight.length()).setEmission(ropeColor).setMaterial(woodMat));
            }
        }
    }

    /**
     * Constructs a cylindrical brick tower made of polygon segments and adds it to the scene group.
     *
     * @param geometries the Geometries group to receive the tower
     * @param cx         center X coordinate of the tower
     * @param cy         base Y coordinate of the tower
     * @param cz         center Z coordinate of the tower
     * @param radius     radius of the tower
     * @param height     height of the tower
     * @param brick1     primary brick color
     * @param brick2     secondary brick color (for alternating pattern)
     * @param mat        material applied to the tower polygons
     */
    private void buildBrickTower(Geometries geometries, double cx, double cy, double cz, double radius, double height, Color brick1, Color brick2, Material mat) {
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
                geometries.add(new Polygon(p1, p2, p3, p4).setEmission(currentColor).setMaterial(mat));
            }
        }
    }

    /**
     * Builds a straight brick wall between two X coordinates at a given Z position and height.
     *
     * @param geometries the Geometries collection to add wall segments to
     * @param startX     starting X coordinate
     * @param endX       ending X coordinate
     * @param zPos       Z coordinate where the wall is placed
     * @param height     height of the wall
     * @param brick1     first brick color used in alternating pattern
     * @param brick2     second brick color used in alternating pattern
     * @param mat        material applied to the wall polygons
     */
    private void buildBrickWall(Geometries geometries, double startX, double endX, double zPos, double height, Color brick1, Color brick2, Material mat) {
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
                geometries.add(new Polygon(p1, p2, p3, p4).setEmission(currentColor).setMaterial(mat));
            }
        }
    }

    /**
     * Adds a recessed window (frame and glow) into a tower geometry.
     *
     * @param geometries  the Geometries group to add the window to
     * @param cx          tower center X coordinate
     * @param cz          tower center Z coordinate
     * @param towerRadius radius of the tower where the window is recessed
     * @param angle       angular position around the tower (radians)
     * @param yBottom     bottom Y coordinate of the window
     * @param yTop        top Y coordinate of the window
     * @param lightColor  emission color for the glowing inner window
     * @param frameColor  color used for the window frame
     * @param wallMat     material used for the wall/frame
     * @param glowMat     material used for the glowing inner window
     */
    private void addRecessedWindow(Geometries geometries, double cx, double cz, double towerRadius, double angle, double yBottom, double yTop, Color lightColor, Color frameColor, Material wallMat, Material glowMat) {
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

        geometries.add(new Polygon(g1, g2, g3, g4).setEmission(lightColor).setMaterial(glowMat));
        geometries.add(new Triangle(g3, g4, gTop).setEmission(lightColor).setMaterial(glowMat));

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

        geometries.add(new Polygon(f1, f2, g2, g1).setEmission(frameColor).setMaterial(wallMat));
        geometries.add(new Polygon(f2, f3, g3, g2).setEmission(frameColor).setMaterial(wallMat));
        geometries.add(new Polygon(f4, f1, g1, g4).setEmission(frameColor).setMaterial(wallMat));
        geometries.add(new Triangle(f3, fTop, gTop).setEmission(frameColor).setMaterial(wallMat));
        geometries.add(new Triangle(f4, fTop, gTop).setEmission(frameColor).setMaterial(wallMat));
    }

    /**
     * Adds a recessed gate (large glowing opening with frame) to the provided geometries.
     *
     * @param geometries the Geometries group to add gate elements to
     * @param lightColor  emission color for the gate aperture
     * @param frameColor  color used for the surrounding frame
     * @param wallMat     material for the frame and walls
     * @param glowMat     material for the glowing gate surface
     */
    private void addRecessedGate(Geometries geometries, Color lightColor, Color frameColor, Material wallMat, Material glowMat) {
        Point g1 = new Point(-10, 0, -5);
        Point g2 = new Point(10, 0, -5);
        Point g3 = new Point(10, 26, -5);
        Point g4 = new Point(-10, 26, -5);
        geometries.add(new Polygon(g1, g2, g3, g4).setEmission(lightColor).setMaterial(glowMat));

        geometries.add(new Polygon(new Point(-12, 0, 1), new Point(-10, 0, 1), new Point(-10, 28, 1), new Point(-12, 28, 1)).setEmission(frameColor).setMaterial(wallMat));
        geometries.add(new Polygon(new Point(10, 0, 1), new Point(12, 0, 1), new Point(12, 28, 1), new Point(10, 28, 1)).setEmission(frameColor).setMaterial(wallMat));
        geometries.add(new Polygon(new Point(-12, 26, 1), new Point(12, 26, 1), new Point(12, 28, 1), new Point(-12, 28, 1)).setEmission(frameColor).setMaterial(wallMat));
    }

    /**
     * Builds a layered shingled roof above a circular tower base.
     *
     * @param geometries the Geometries group to add roof triangles to
     * @param cx         center X coordinate of the roof
     * @param cy         base Y coordinate of the roof
     * @param cz         center Z coordinate of the roof
     * @param radius     base radius of the roof
     * @param height     total roof height
     * @param roofColor  color applied to roof shingles
     * @param roofMat    material applied to roof triangles
     */
    private void buildShingledRoof(Geometries geometries, double cx, double cy, double cz, double radius, double height, Color roofColor, Material roofMat) {
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

            geometries.add(new Triangle(p4, p1, pTop).setEmission(roofColor).setMaterial(roofMat));
            geometries.add(new Triangle(p1, p2, pTop).setEmission(roofColor).setMaterial(roofMat));
            geometries.add(new Triangle(p2, p3, pTop).setEmission(roofColor).setMaterial(roofMat));
            geometries.add(new Triangle(p3, p4, pTop).setEmission(roofColor).setMaterial(roofMat));
        }
    }

    /**
     * Constructs the full "Magic Detailed Castle" scene composed of multiple grouped geometries,
     * lights and materials. The scene is returned fully populated and ready for rendering.
     *
     * @return a Scene instance representing the magic castle environment
     */
    private Scene buildMagicCastleScene() {
        Scene scene = new Scene("Magic Detailed Castle");
        scene.setBackground(new Color(15, 10, 30));
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));

        Material waterMat = new Material().setKD(0.1).setKS(0.8).setShininess(100).setKR(0.5).setBlur(0.04);
        Material wallMat = new Material().setKD(0.7).setKS(0.1).setShininess(5);
        Material roofMat = new Material().setKD(0.8).setKS(0.0).setShininess(0);
        Material woodMat = new Material().setKD(0.6).setKS(0.2).setShininess(15);
        Material clearGlassMat = new Material().setKD(0.05).setKS(0.9).setShininess(120).setKT(0.85);
        Material frostyGlassMat = new Material().setKD(0.2).setKS(0.5).setShininess(50).setKT(0.5).setKR(0.2).setBlur(0.06);
        Material heavyGlassMat = new Material().setKD(0.3).setKS(0.6).setShininess(30).setKT(0.2).setKR(0.4);
        Material glowMat = new Material().setKD(0).setKS(0).setShininess(0);

        Color brick1 = new Color(155, 115, 95);
        Color brick2 = new Color(140, 100, 80);
        Color frameColor = new Color(100, 70, 50);
        Color roofColor = new Color(50, 35, 75);
        Color woodColor = new Color(120, 80, 55);
        Color ropeColor = new Color(90, 60, 40);
        Color waterColor = new Color(15, 20, 40);
        Color windowLight = new Color(255, 190, 60);

        // 1. Lake Group
        Geometries lakeGroup = new Geometries();
        Polygon lake = (Polygon) new Polygon(
                new Point(-1000, 0, 1000), new Point(1000, 0, 1000),
                new Point(1000, 0, -1000), new Point(-1000, 0, -1000)
        ).setEmission(waterColor).setMaterial(waterMat);
        lakeGroup.add(lake);

        // 2. Bridge Group
        Geometries bridgeGroup = new Geometries();
        buildWoodenBridge(bridgeGroup, woodColor, ropeColor, woodMat);

        // 3. Castle Towers & Walls Group
        Geometries castleGroup = new Geometries();
        buildBrickTower(castleGroup, 0, 0, -30, 30d, 105d, brick1, brick2, wallMat);
        buildBrickTower(castleGroup, -65, 0, 0, 20d, 70d, brick1, brick2, wallMat);
        buildBrickTower(castleGroup, 65, 0, 0, 20d, 70d, brick1, brick2, wallMat);
        buildBrickWall(castleGroup, -65, 0, -15, 55, brick1, brick2, wallMat);
        buildBrickWall(castleGroup, 0, 65, -15, 55, brick1, brick2, wallMat);
        addRecessedGate(castleGroup, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 0, -30, 30, Math.PI / 2 + 0.35, 40, 52, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 0, -30, 30, Math.PI / 2 - 0.35, 40, 52, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 0, -30, 30, Math.PI / 2 + 0.35, 75, 87, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 0, -30, 30, Math.PI / 2 - 0.35, 75, 87, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 0, -30, 30, 0, 55, 67, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 0, -30, 30, Math.PI, 55, 67, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, -65, 0, 20, Math.PI / 2, 35, 48, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, -65, 0, 20, Math.PI, 35, 48, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 65, 0, 20, Math.PI / 2, 35, 48, windowLight, frameColor, wallMat, glowMat);
        addRecessedWindow(castleGroup, 65, 0, 20, 0, 35, 48, windowLight, frameColor, wallMat, glowMat);
        buildShingledRoof(castleGroup, 0, 105, -30, 34, 55, roofColor, roofMat);
        castleGroup.add(new Cylinder(0.4, new Ray(new Point(0, 160, -30), new Vector(0, 1, 0)), 30d).setEmission(roofColor).setMaterial(wallMat));
        castleGroup.add(new Triangle(new Point(0, 185, -30), new Point(0, 172, -30), new Point(22, 178, -30)).setEmission(new Color(100, 200, 255)).setMaterial(glowMat));
        buildShingledRoof(castleGroup, -65, 70, 0, 23, 40, roofColor, roofMat);
        castleGroup.add(new Cylinder(0.3, new Ray(new Point(-65, 110, 0), new Vector(0, 1, 0)), 20d).setEmission(roofColor).setMaterial(wallMat));
        castleGroup.add(new Triangle(new Point(-65, 125, 0), new Point(-65, 115, 0), new Point(-50, 120, 0)).setEmission(new Color(100, 200, 255)).setMaterial(glowMat));
        buildShingledRoof(castleGroup, 65, 70, 0, 23, 40, roofColor, roofMat);
        castleGroup.add(new Cylinder(0.3, new Ray(new Point(65, 110, 0), new Vector(0, 1, 0)), 20d).setEmission(roofColor).setMaterial(wallMat));
        castleGroup.add(new Triangle(new Point(65, 125, 0), new Point(65, 115, 0), new Point(80, 120, 0)).setEmission(new Color(100, 200, 255)).setMaterial(glowMat));

        // 4. Magic Spheres & Stars Group
        Geometries magicGroup = new Geometries();
        magicGroup.add(new Sphere(new Point(-120, 160, 40), 20).setEmission(new Color(20, 40, 50)).setMaterial(clearGlassMat));
        magicGroup.add(new Sphere(new Point(-80, 130, 80), 12).setEmission(new Color(40, 20, 50)).setMaterial(frostyGlassMat));
        magicGroup.add(new Sphere(new Point(100, 140, 60), 16).setEmission(new Color(20, 40, 50)).setMaterial(clearGlassMat));
        magicGroup.add(new Sphere(new Point(140, 100, 30), 10).setEmission(new Color(50, 50, 50)).setMaterial(heavyGlassMat));
        magicGroup.add(new Sphere(new Point(-30, 190, -40), 14).setEmission(new Color(40, 50, 40)).setMaterial(frostyGlassMat));
        magicGroup.add(new Sphere(new Point(-160, 110, 10), 15).setEmission(new Color(30, 30, 60)).setMaterial(heavyGlassMat));

        Random rnd = new Random(42);
        for (int i = 0; i < 400; i++) {
            double t = i * 0.08;
            magicGroup.add(new Sphere(new Point(-800 + i * 4, 180 + 35 * Math.sin(t), -100 + 40 * Math.cos(t)), rnd.nextDouble() * 1.5 + 0.5).setEmission(new Color(0, 255, 255)).setMaterial(glowMat));
            magicGroup.add(new Sphere(new Point(800 - i * 4, 130 + 45 * Math.cos(t), -50 + 30 * Math.sin(t)), rnd.nextDouble() * 1.5 + 0.5).setEmission(new Color(255, 50, 200)).setMaterial(glowMat));
            magicGroup.add(new Sphere(new Point(-600 + i * 3, 200 + 20 * Math.sin(t * 1.5), -150 + 60 * Math.cos(t * 1.2)), rnd.nextDouble() * 1.2 + 0.4).setEmission(new Color(255, 200, 100)).setMaterial(glowMat));

            double starX = (rnd.nextDouble() - 0.5) * 4000;
            double starY = rnd.nextDouble() * 800;
            double starZ = -2500 + rnd.nextDouble() * 1500;
            double starRadius = rnd.nextDouble() * 1.5 + 0.6;
            magicGroup.add(new Sphere(new Point(starX, starY, starZ), starRadius).setEmission(new Color(255, 255, 255)).setMaterial(glowMat));
        }

        // Add the Manual Hierarchy structure to the main scene
        scene.geometries.add(lakeGroup, bridgeGroup, castleGroup, magicGroup);

        // Lights
        scene.lights.add(new DirectionalLight(new Color(60, 50, 110), new Vector(-0.5, -1, -0.5)));
        scene.lights.add(new PointLight(windowLight, new Point(0, 50, 15)).setKl(0.003).setKq(0.0001));
        scene.lights.add(new PointLight(windowLight, new Point(-65, 42, 25)).setKl(0.004).setKq(0.0002));
        scene.lights.add(new PointLight(windowLight, new Point(65, 42, 25)).setKl(0.004).setKq(0.0002));
        scene.lights.add(new SpotLight(windowLight, new Point(0, 13, 60), new Vector(0, 0, -1)).setKl(0.001).setKq(0.00005).setNarrowBeam(2));

        return scene;
    }

    // =========================================================================
    // VISUAL TESTS (SHOWCASE) - Optimized automatically
    // =========================================================================

    /**
     * Visual test: Front angle rendering using the baseline configuration (Before improvements).
     */
    @Test
    void test01_FrontAngle_Before() {
        Scene scene = buildMagicCastleScene();
        scene.geometries.buildBVH();
        SimpleRayTracer srtBase = new SimpleRayTracer(scene).setBeamRays(1);

        Camera.getBuilder()
                .setLocation(new Point(0, 80, 650))
                .setDirection(new Point(0, 35, 0))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(800, 800)
                .setMultithreading(-2)
                .setDebugPrint(0.1) // FIXED: Re-added debug print
                .setAntiAliasingRays(1)
                .setAdaptive(false)
                .setRayTracer(srtBase)
                .enableBVH()
                .build().renderImage().writeToImage("MagicCastle_01_Front_Before");
    }

    /**
     * Visual test: Front angle rendering using the improved configuration (After improvements).
     */
    @Test
    void test02_FrontAngle_After() {
        Scene scene = buildMagicCastleScene();
        scene.geometries.buildBVH();
        SimpleRayTracer srtImproved = new SimpleRayTracer(scene).setBeamRays(9).setBeamDistance(50);

        Camera.getBuilder()
                .setLocation(new Point(0, 80, 650))
                .setDirection(new Point(0, 35, 0))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(800, 800)
                .setMultithreading(-2)
                .setDebugPrint(0.1) // FIXED: Re-added debug print
                .setAntiAliasingRays(9)
                .setAdaptive(true)
                .setRayTracer(srtImproved)
                .enableBVH()
                .build().renderImage().writeToImage("MagicCastle_02_Front_After");
    }

    /**
     * Visual test: Right angle rendering using the baseline configuration (Before improvements).
     */
    @Test
    void test03_RightAngle_Before() {
        Scene scene = buildMagicCastleScene();
        scene.geometries.buildBVH();
        SimpleRayTracer srtBase = new SimpleRayTracer(scene).setBeamRays(1);

        Camera.getBuilder()
                .setLocation(new Point(350, 90, 450))
                .setDirection(new Point(-35, 35, -20))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(800, 800)
                .setMultithreading(-2)
                .setDebugPrint(0.1) // FIXED: Re-added debug print
                .setAntiAliasingRays(1)
                .setAdaptive(false)
                .setRayTracer(srtBase)
                .enableBVH()
                .build().renderImage().writeToImage("MagicCastle_03_Right_Before");
    }

    /**
     * Visual test: Right angle rendering using the improved configuration (After improvements).
     */
    @Test
    void test04_RightAngle_After() {
        Scene scene = buildMagicCastleScene();
        scene.geometries.buildBVH();
        SimpleRayTracer srtImproved = new SimpleRayTracer(scene).setBeamRays(9).setBeamDistance(50);

        Camera.getBuilder()
                .setLocation(new Point(350, 90, 450))
                .setDirection(new Point(-35, 35, -20))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(800, 800)
                .setMultithreading(-2)
                .setDebugPrint(0.1) // FIXED: Re-added debug print
                .setAntiAliasingRays(9)
                .setAdaptive(true)
                .setRayTracer(srtImproved)
                .enableBVH()
                .build().renderImage().writeToImage("MagicCastle_04_Right_After");
    }

    /**
     * Visual test: Low tilt front rendering using the baseline configuration (Before improvements).
     */
    @Test
    void test05_LowTilt_Before() {
        Scene scene = buildMagicCastleScene();
        scene.geometries.buildBVH();
        SimpleRayTracer srtBase = new SimpleRayTracer(scene).setBeamRays(1);

        Camera.getBuilder()
                .setLocation(new Point(-250, 30, 500))
                .setDirection(new Point(0, 45, 0))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(800, 800)
                .setMultithreading(-2)
                .setDebugPrint(0.1) // FIXED: Re-added debug print
                .setAntiAliasingRays(1)
                .setAdaptive(false)
                .setRayTracer(srtBase)
                .rotate(5)
                .enableBVH()
                .build().renderImage().writeToImage("MagicCastle_05_LowTilt_Before");
    }

    /**
     * Visual test: Low tilt front rendering using the improved configuration (After improvements).
     */
    @Test
    void test06_LowTilt_After() {
        Scene scene = buildMagicCastleScene();
        scene.geometries.buildBVH();
        SimpleRayTracer srtImproved = new SimpleRayTracer(scene).setBeamRays(9).setBeamDistance(50);

        Camera.getBuilder()
                .setLocation(new Point(-250, 30, 500))
                .setDirection(new Point(0, 45, 0))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(800, 800)
                .setMultithreading(-2)
                .setDebugPrint(0.1) // FIXED: Re-added debug print
                .setAntiAliasingRays(9)
                .setAdaptive(true)
                .setRayTracer(srtImproved)
                .rotate(5)
                .enableBVH()
                .build().renderImage().writeToImage("MagicCastle_06_LowTilt_After");
    }

    // =========================================================================
    // STAGE 10 REQUIREMENT: PERFORMANCE MEASUREMENT TABLE
    // =========================================================================

    /**
     * Executes the exact 12 performance measurements requested in the Stage 10 instructions.
     * Uses a slightly lowered resolution (400x400) to complete within ~15 minutes while
     * still demonstrating a massive, exponential speedup.
     */
    @Test
    void test10_PerformanceMeasurements() {
        System.out.println("===============================================================================");
        System.out.println("STAGE 10: ACCELERATION PERFORMANCE MEASUREMENTS (BVH & CBR)");
        System.out.println("===============================================================================");
        System.out.printf("%-40s | %-5s | %-5s | %-10s%n", "Configuration", "CBR", "MT", "Time (sec)");
        System.out.println("-----------------------------------------|-------|-------|-----------");

        // Prepare the 3 required states of the scene
        Scene sceneManual = buildMagicCastleScene();

        Scene sceneFlat = buildMagicCastleScene();
        sceneFlat.geometries.flatten();

        Scene sceneAuto = buildMagicCastleScene();
        sceneAuto.geometries.flatten();
        sceneAuto.geometries.buildBVH();

        // 1. No acceleration, flattened scene
        runSingleMeasurement("1. Flattened Scene", sceneFlat, false, false);
        runSingleMeasurement("2. Flattened Scene", sceneFlat, false, true);

        // 2. No acceleration, manual hierarchy
        runSingleMeasurement("3. Manual Hierarchy", sceneManual, false, false);
        runSingleMeasurement("4. Manual Hierarchy", sceneManual, false, true);

        // 3. No acceleration, automatic hierarchy
        runSingleMeasurement("5. Automatic BVH Hierarchy", sceneAuto, false, false);
        runSingleMeasurement("6. Automatic BVH Hierarchy", sceneAuto, false, true);

        // 4. CBR, flattened scene
        runSingleMeasurement("7. Flattened Scene", sceneFlat, true, false);
        runSingleMeasurement("8. Flattened Scene", sceneFlat, true, true);

        // 5. CBR, manual hierarchy
        runSingleMeasurement("9. Manual Hierarchy", sceneManual, true, false);
        runSingleMeasurement("10. Manual Hierarchy", sceneManual, true, true);

        // 6. CBR, automatic hierarchy
        runSingleMeasurement("11. Automatic BVH Hierarchy", sceneAuto, true, false);
        runSingleMeasurement("12. Automatic BVH Hierarchy", sceneAuto, true, true);

        System.out.println("===============================================================================");
    }

    /**
     * Helper method to execute and time a single measurement configuration.
     *
     * @param configName human readable name of the configuration being measured
     * @param scene      the Scene to render for the measurement
     * @param useCBR     whether to enable Conservative Bounding Region (AABB) acceleration
     * @param useMT      whether to enable multi-threading for rendering
     */
    private void runSingleMeasurement(String configName, Scene scene, boolean useCBR, boolean useMT) {
        geometries.api.Intersectable.setAabbEnabled(useCBR);
        SimpleRayTracer srt = new SimpleRayTracer(scene).setBeamRays(9).setBeamDistance(50);

        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 80, 650))
                .setDirection(new Point(0, 35, 0))
                .setVpDistance(400)
                .setVpSize(250, 250)
                .setResolution(50, 50)
                .setMultithreading(useMT ? -2 : 0)
                .setDebugPrint(0.1)
                .setAntiAliasingRays(9)
                .setAdaptive(true)
                .setRayTracer(srt)
                .build();

        long startTime = System.currentTimeMillis();
        camera.renderImage();
        long duration = (System.currentTimeMillis() - startTime) / 1000;

        System.out.printf("%-40s | %-5s | %-5s | %3d sec%n",
                configName, (useCBR ? "ON" : "OFF"), (useMT ? "ON" : "OFF"), duration);
    }
}