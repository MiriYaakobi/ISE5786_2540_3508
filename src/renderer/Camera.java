package renderer;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ThreadLocalRandom;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class represents a physical camera in 3D space.
 * Extended for Stage 9 and Stage 10 to include Super-sampling (Anti-Aliasing), Multi-threading,
 * heavily optimized Adaptive Super-Sampling, and Depth of Field (DoF).
 *
 * @author Miri and Yael
 */
@SuppressWarnings("unused")
public class Camera implements Cloneable {
    private Point p0;
    private Vector vUp;
    private Vector vTo;
    private Vector vRight;

    private double width;
    private double height;
    private double distance;

    private int nX = 1;
    private int nY = 1;

    private Point viewPlaneCenter;
    private double pixelWidth;
    private double pixelHeight;

    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;

    // --- Added for Stage 9 ---
    private int threadsCount = 0;
    private double printInterval = 0;
    private int antiAliasingRays = 1;
    private boolean useAdaptive = false; // Flag for Adaptive Super-Sampling
    // -------------------------

    // --- Added for Stage 10 (Depth of Field) ---
    private double focalDistance = 0;    // Distance from camera to the focal plane
    private double apertureSize = 0;     // Width/Height of the lens aperture area (0 means pinhole)
    private int dofRays = 1;             // Number of secondary primary rays sampled from the aperture
    // -------------------------------------------

    private Camera() {
    }

    /**
     * Factory method to obtain a new Camera Builder.
     *
     * @return a new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    public Point getP0() {
        return p0;
    }

    public Vector getVUp() {
        return vUp;
    }

    public Vector getVTo() {
        return vTo;
    }

    public Vector getVRight() {
        return vRight;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDistance() {
        return distance;
    }

    public int getNx() {
        return nX;
    }

    public int getNy() {
        return nY;
    }

    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Cannot happen since we implement Cloneable
        }
    }

    public Ray constructRay(int xIndex, int yIndex) {
        Point pIJ = getPixelCenter(nX, nY, xIndex, yIndex);
        Vector vIJ = pIJ.subtract(p0);
        return new Ray(p0, vIJ);
    }

    private Point getPixelCenter(int nX, int nY, int j, int i) {
        Point pIJ = viewPlaneCenter;
        double xOffset = alignZero((j - (nX - 1) / 2.0) * pixelWidth);
        double yOffset = alignZero(-(i - (nY - 1) / 2.0) * pixelHeight);
        if (!isZero(xOffset)) pIJ = pIJ.add(vRight.scale(xOffset));
        if (!isZero(yOffset)) pIJ = pIJ.add(vUp.scale(yOffset));
        return pIJ;
    }

    /**
     * Helper method to safely move a point by dx and dy on the view plane.
     */
    private Point movePoint(Point p, double dx, double dy) {
        Point res = p;
        if (!isZero(dx)) res = res.add(vRight.scale(dx));
        if (!isZero(dy)) res = res.add(vUp.scale(dy));
        return res;
    }

    /**
     * Stage 9: Generic flexible infrastructure for Jittered Grid (Super-sampling).
     * Uses ThreadLocalRandom for massive performance gains in Multi-threading.
     */
    public List<Ray> constructRaysTargetArea(int j, int i, int gridSize) {
        List<Ray> rays = new LinkedList<>();
        Point pIJ = getPixelCenter(nX, nY, j, i);

        if (gridSize <= 1) {
            rays.add(new Ray(p0, pIJ.subtract(p0)));
            return rays;
        }

        double subPixelWidth = pixelWidth / gridSize;
        double subPixelHeight = pixelHeight / gridSize;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                // FIXED: Using ThreadLocalRandom prevents thread lock contention!
                double randomX = (ThreadLocalRandom.current().nextDouble() - 0.5) * subPixelWidth;
                double randomY = (ThreadLocalRandom.current().nextDouble() - 0.5) * subPixelHeight;

                double dx = alignZero((c - (gridSize - 1) / 2.0) * subPixelWidth + randomX);
                double dy = alignZero(-(r - (gridSize - 1) / 2.0) * subPixelHeight + randomY);

                Point pTarget = movePoint(pIJ, dx, dy);
                rays.add(new Ray(p0, pTarget.subtract(p0)));
            }
        }
        return rays;
    }

    /**
     * Stage 10: Unified sampling method that handles Depth of Field aperture distribution.
     * Integrates transparently with all Super-Sampling configurations.
     */
    private Color traceSample(Point pTarget) {
        // If Depth of Field is disabled, perform a single classic ray trace
        if (apertureSize <= 0 || dofRays <= 1 || isZero(focalDistance)) {
            return rayTracer.traceRay(new Ray(p0, pTarget.subtract(p0)));
        }

        // Calculate the corresponding point on the focal plane using similar triangles rules
        Vector v = pTarget.subtract(p0);
        Point pFocal = p0.add(v.scale(focalDistance / distance));
        Color colorSum = Color.BLACK;

        int apertureGrid = (int) Math.ceil(Math.sqrt(dofRays));
        double subAperture = apertureSize / apertureGrid;

        // Sample the aperture area using a Jittered distribution
        for (int r = 0; r < apertureGrid; r++) {
            for (int c = 0; c < apertureGrid; c++) {
                double randomX = (ThreadLocalRandom.current().nextDouble() - 0.5) * subAperture;
                double randomY = (ThreadLocalRandom.current().nextDouble() - 0.5) * subAperture;

                double dx = alignZero((c - (apertureGrid - 1) / 2.0) * subAperture + randomX);
                double dy = alignZero(-(r - (apertureGrid - 1) / 2.0) * subAperture + randomY);

                Point pAperture = p0;
                if (!isZero(dx)) pAperture = pAperture.add(vRight.scale(dx));
                if (!isZero(dy)) pAperture = pAperture.add(vUp.scale(dy));

                colorSum = colorSum.add(rayTracer.traceRay(new Ray(pAperture, pFocal.subtract(pAperture))));
            }
        }
        return colorSum.reduce(apertureGrid * apertureGrid);
    }

    /**
     * Stage 9: Optimized Adaptive Super-Sampling Recursive algorithm.
     * Passes the pre-calculated corner colors to avoid 75% of redundant ray tracing!
     * Updated for Stage 10 to flow samples through traceSample helper.
     */
    private Color calcAdaptiveColor(Point center, double w, double h, int depth, int maxDepth, Color cTl, Color cTr, Color cBl, Color cBr) {
        // If we reached max depth, or all 4 corners are exactly the same color, stop and return the average
        if (depth >= maxDepth || (cTl.equals(cTr) && cTl.equals(cBl) && cTl.equals(cBr))) {
            return cTl.add(cTr).add(cBl).add(cBr).reduce(4);
        }

        // Calculate ONLY the missing 5 points (center and 4 edge midpoints) via traceSample
        Color cTop = traceSample(movePoint(center, 0, h / 2));
        Color cBot = traceSample(movePoint(center, 0, -h / 2));
        Color cLeft = traceSample(movePoint(center, -w / 2, 0));
        Color cRight = traceSample(movePoint(center, w / 2, 0));
        Color cCenter = traceSample(center);

        // Subdivide into 4 quadrants passing the known colors downward
        Color topL = calcAdaptiveColor(movePoint(center, -w / 4, h / 4), w / 2, h / 2, depth + 1, maxDepth, cTl, cTop, cLeft, cCenter);
        Color topR = calcAdaptiveColor(movePoint(center, w / 4, h / 4), w / 2, h / 2, depth + 1, maxDepth, cTop, cTr, cCenter, cRight);
        Color botL = calcAdaptiveColor(movePoint(center, -w / 4, -h / 4), w / 2, h / 2, depth + 1, maxDepth, cLeft, cCenter, cBl, cBot);
        Color botR = calcAdaptiveColor(movePoint(center, w / 4, -h / 4), w / 2, h / 2, depth + 1, maxDepth, cCenter, cRight, cBot, cBr);

        return topL.add(topR).add(botL).add(botR).reduce(4);
    }

    private void castRays(int nX, int nY, int j, int i) {
        Color pixelColor = Color.BLACK;
        Point pIJ = getPixelCenter(nX, nY, j, i);

        if (antiAliasingRays <= 1) {
            pixelColor = traceSample(pIJ);
        } else if (useAdaptive) {
            // Adaptive mode
            int maxDepth = 2; // Depth 2 provides excellent quality at high speeds

            // Calculate initial 4 corners of the pixel
            Point tl = movePoint(pIJ, -pixelWidth / 2, pixelHeight / 2);
            Point tr = movePoint(pIJ, pixelWidth / 2, pixelHeight / 2);
            Point bl = movePoint(pIJ, -pixelWidth / 2, -pixelHeight / 2);
            Point br = movePoint(pIJ, pixelWidth / 2, -pixelHeight / 2);

            Color cTl = traceSample(tl);
            Color cTr = traceSample(tr);
            Color cBl = traceSample(bl);
            Color cBr = traceSample(br);

            pixelColor = calcAdaptiveColor(pIJ, pixelWidth, pixelHeight, 1, maxDepth, cTl, cTr, cBl, cBr);
        } else {
            // Standard Jittered Grid mode - calculated directly to avoid Ray.getDir() errors and code duplication
            int gridSize = (int) Math.ceil(Math.sqrt(antiAliasingRays));
            double sWidth = pixelWidth / gridSize;
            double sHeight = pixelHeight / gridSize;

            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    double rx = (ThreadLocalRandom.current().nextDouble() - 0.5) * sWidth;
                    double ry = (ThreadLocalRandom.current().nextDouble() - 0.5) * sHeight;

                    double xOff = alignZero((col - (gridSize - 1) / 2.0) * sWidth + rx);
                    double yOff = alignZero(-(row - (gridSize - 1) / 2.0) * sHeight + ry);

                    pixelColor = pixelColor.add(traceSample(movePoint(pIJ, xOff, yOff)));
                }
            }
            pixelColor = pixelColor.reduce(gridSize * gridSize);
        }

        imageWriter.writePixel(j, i, pixelColor);
    }

    public Camera renderImage() {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        if (rayTracer == null) throw new MissingResourceException("Missing ray tracer", "Camera", "rayTracer");

        PixelManager pixelManager = new PixelManager(nY, nX, printInterval);

        if (threadsCount == 0) {
            for (int i = 0; i < nY; ++i) {
                for (int j = 0; j < nX; ++j) {
                    castRays(nX, nY, j, i);
                    pixelManager.pixelDone();
                }
            }
        } else {
            int threads = threadsCount > 0 ? threadsCount : Runtime.getRuntime().availableProcessors();
            Thread[] activeThreads = new Thread[threads];

            for (int i = 0; i < threads; i++) {
                activeThreads[i] = new Thread(() -> {
                    PixelManager.Pixel pixel;
                    while ((pixel = pixelManager.nextPixel()) != null) {
                        castRays(nX, nY, pixel.col(), pixel.row());
                        pixelManager.pixelDone();
                    }
                });
            }

            for (Thread thread : activeThreads) thread.start();
            for (Thread thread : activeThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return this;
    }

    public Camera printGrid(int interval, Color color) {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (j % interval == 0 || i % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    public void writeToImage(String imageName) {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        imageWriter.writeToImage(imageName);
    }

    /**
     * Builder class for Camera using the Builder pattern.
     */
    @SuppressWarnings({"unused", "exports"})
    public static class Builder {
        private final Camera _camera = new Camera();
        private Point _target = null;
        private Vector _vUpGen = Vector.AXIS_Y;
        private Scene _scene = null; // Added to hold the reference for Stage 10 BVH creation

        public Builder() {
        }

        public Builder setLocation(Point location) {
            _camera.p0 = location;
            return this;
        }

        public Builder setDirection(Vector to, Vector up) {
            _camera.vTo = to;
            _vUpGen = up;
            _target = null;
            return this;
        }

        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vUpGen = up;
            _camera.vTo = null;
            return this;
        }

        public Builder setDirection(Point target) {
            _target = target;
            _camera.vTo = null;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            _camera.width = width;
            _camera.height = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            _camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            _camera.nX = nX;
            _camera.nY = nY;
            return this;
        }

        // FIXED: Package-private visibility avoids visibility scope exposure of non-public ImageWriter class
        Builder setImageWriter(ImageWriter imageWriter) {
            _camera.imageWriter = imageWriter;
            return this;
        }

        public Builder setRayTracer(RayTracerBase rayTracer) {
            _camera.rayTracer = rayTracer;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            this._scene = scene; // Store the scene reference internally
            if (type == RayTracerType.SIMPLE) _camera.rayTracer = new SimpleRayTracer(scene);
            return this;
        }

        // --- Config for Stage 9 ---
        public Builder setMultithreading(int threads) {
            if (threads < -2) throw new IllegalArgumentException("Multithreading must be -2 or higher");
            if (threads >= -1) _camera.threadsCount = threads;
            else _camera.threadsCount = Runtime.getRuntime().availableProcessors();
            return this;
        }

        public Builder setDebugPrint(double interval) {
            _camera.printInterval = interval;
            return this;
        }

        public Builder setAntiAliasingRays(int rays) {
            if (rays < 1) throw new IllegalArgumentException("Rays must be at least 1");
            _camera.antiAliasingRays = rays;
            return this;
        }

        public Builder setAdaptive(boolean useAdaptive) {
            _camera.useAdaptive = useAdaptive;
            return this;
        }
        // --------------------------------

        // --- Config for Stage 10 (Depth of Field Builder Methods) ---
        public Builder setFocalDistance(double focalDistance) {
            if (focalDistance < 0) throw new IllegalArgumentException("Focal distance cannot be negative");
            _camera.focalDistance = focalDistance;
            return this;
        }

        public Builder setApertureSize(double apertureSize) {
            if (apertureSize < 0) throw new IllegalArgumentException("Aperture size cannot be negative");
            _camera.apertureSize = apertureSize;
            return this;
        }

        public Builder setDofRays(int rays) {
            if (rays < 1) throw new IllegalArgumentException("DOF rays count must be at least 1");
            _camera.dofRays = rays;
            return this;
        }
        // -------------------------------------------------------------

        public Builder rotate(double angle) {
            if (isZero(angle) || isZero(angle % 360)) return this;

            if (_camera.vTo == null) {
                if (_target == null || _camera.p0 == null)
                    throw new IllegalStateException("Direction must be set before rotation");
                _camera.vTo = _target.subtract(_camera.p0).normalize();
            }

            Vector to = _camera.vTo;
            Vector upGen = _vUpGen;
            double dotProd = alignZero(upGen.dotProduct(to));
            Vector upPerp = isZero(dotProd) ? upGen : upGen.subtract(to.scale(dotProd));
            Vector rightPerp = to.crossProduct(upPerp);

            double rad = Math.toRadians(angle);
            double cosT = alignZero(Math.cos(rad));
            double sinT = alignZero(Math.sin(rad));

            Vector rotatedUp = null;
            if (!isZero(cosT)) rotatedUp = upPerp.scale(cosT);
            if (!isZero(sinT)) {
                Vector sinComp = rightPerp.scale(sinT);
                rotatedUp = (rotatedUp == null) ? sinComp : rotatedUp.add(sinComp);
            }
            if (!isZero(dotProd)) {
                Vector parComp = to.scale(dotProd);
                rotatedUp = (rotatedUp == null) ? parComp : rotatedUp.add(parComp);
            }

            _vUpGen = rotatedUp;
            return this;
        }

        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            return _camera.clone();
        }

        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0) throw new IllegalArgumentException("Resolution must be positive");
            _camera.imageWriter = new ImageWriter(_camera.nX, _camera.nY);
        }

        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            if (alignZero(_camera.distance) <= 0) throw new IllegalArgumentException("Distance must be positive");

            _camera.viewPlaneCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        private void checkLocationAndDirection() {
            if (_camera.p0 == null) throw new MissingResourceException("Missing location", "Camera", "p0");
            if (_camera.vTo == null) {
                if (_target == null) throw new MissingResourceException("Missing direction", "Camera", "vTo");
                _camera.vTo = _target.subtract(_camera.p0);
            }
            _camera.vTo = _camera.vTo.normalize();

            try {
                _camera.vRight = _camera.vTo.crossProduct(_vUpGen).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Direction and Up vector cannot be parallel");
            }
            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
        }

        /**
         * Enables Conservative Bounding Region (CBR / AABB) acceleration.
         *
         * @return the builder instance for method chaining
         */
        public Builder enableCBR() {
            geometries.api.Intersectable.setAabbEnabled(true);
            return this;
        }

        /**
         * Enables Bounding Volume Hierarchy (BVH) acceleration and constructs the tree structure.
         *
         * @return the builder instance for method chaining
         */
        public Builder enableBVH() {
            geometries.api.Intersectable.setAabbEnabled(true);
            if (this._scene != null) {
                this._scene.geometries.buildBVH();
            }
            return this;
        }
    }
}