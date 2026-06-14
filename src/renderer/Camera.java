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
 * Extended for Stage 9 to include Super-sampling (Anti-Aliasing), Multi-threading,
 * and heavily optimized Adaptive Super-Sampling.
 *
 * @author Miri and Yael
 */
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

    private Camera() {
    }

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
     * Stage 9: Optimized Adaptive Super-Sampling Recursive algorithm.
     * Passes the pre-calculated corner colors to avoid 75% of redundant ray tracing!
     */
    private Color calcAdaptiveColor(Point center, double w, double h, int depth, int maxDepth, Color cTl, Color cTr, Color cBl, Color cBr) {
        // If we reached max depth, or all 4 corners are exactly the same color, stop and return the average
        if (depth >= maxDepth || (cTl.equals(cTr) && cTl.equals(cBl) && cTl.equals(cBr))) {
            return cTl.add(cTr).add(cBl).add(cBr).reduce(4);
        }

        // Calculate ONLY the missing 5 points (center and 4 edge midpoints)
        Color cTop = rayTracer.traceRay(new Ray(p0, movePoint(center, 0, h / 2).subtract(p0)));
        Color cBot = rayTracer.traceRay(new Ray(p0, movePoint(center, 0, -h / 2).subtract(p0)));
        Color cLeft = rayTracer.traceRay(new Ray(p0, movePoint(center, -w / 2, 0).subtract(p0)));
        Color cRight = rayTracer.traceRay(new Ray(p0, movePoint(center, w / 2, 0).subtract(p0)));
        Color cCenter = rayTracer.traceRay(new Ray(p0, center.subtract(p0)));

        // Subdivide into 4 quadrants passing the known colors downward
        Color topL = calcAdaptiveColor(movePoint(center, -w / 4, h / 4), w / 2, h / 2, depth + 1, maxDepth, cTl, cTop, cLeft, cCenter);
        Color topR = calcAdaptiveColor(movePoint(center, w / 4, h / 4), w / 2, h / 2, depth + 1, maxDepth, cTop, cTr, cCenter, cRight);
        Color botL = calcAdaptiveColor(movePoint(center, -w / 4, -h / 4), w / 2, h / 2, depth + 1, maxDepth, cLeft, cCenter, cBl, cBot);
        Color botR = calcAdaptiveColor(movePoint(center, w / 4, -h / 4), w / 2, h / 2, depth + 1, maxDepth, cCenter, cRight, cBot, cBr);

        return topL.add(topR).add(botL).add(botR).reduce(4);
    }

    private void castRays(int nX, int nY, int j, int i) {
        Color pixelColor = Color.BLACK;

        if (antiAliasingRays <= 1) {
            pixelColor = rayTracer.traceRay(constructRay(j, i));
        } else if (useAdaptive) {
            // Adaptive mode
            int maxDepth = 2; // Depth 2 provides excellent quality at high speeds
            Point pIJ = getPixelCenter(nX, nY, j, i);

            // Calculate initial 4 corners of the pixel
            Point tl = movePoint(pIJ, -pixelWidth / 2, pixelHeight / 2);
            Point tr = movePoint(pIJ, pixelWidth / 2, pixelHeight / 2);
            Point bl = movePoint(pIJ, -pixelWidth / 2, -pixelHeight / 2);
            Point br = movePoint(pIJ, pixelWidth / 2, -pixelHeight / 2);

            Color cTl = rayTracer.traceRay(new Ray(p0, tl.subtract(p0)));
            Color cTr = rayTracer.traceRay(new Ray(p0, tr.subtract(p0)));
            Color cBl = rayTracer.traceRay(new Ray(p0, bl.subtract(p0)));
            Color cBr = rayTracer.traceRay(new Ray(p0, br.subtract(p0)));

            pixelColor = calcAdaptiveColor(pIJ, pixelWidth, pixelHeight, 1, maxDepth, cTl, cTr, cBl, cBr);
        } else {
            // Standard Jittered Grid mode
            int gridSize = (int) Math.ceil(Math.sqrt(antiAliasingRays));
            List<Ray> rays = constructRaysTargetArea(j, i, gridSize);
            for (Ray r : rays) {
                pixelColor = pixelColor.add(rayTracer.traceRay(r));
            }
            pixelColor = pixelColor.reduce(rays.size());
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
                    e.printStackTrace();
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

    public static class Builder {
        private final Camera _camera = new Camera();
        private Point _target = null;
        private Vector _vUpGen = Vector.AXIS_Y;

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

        public Builder setImageWriter(ImageWriter imageWriter) {
            _camera.imageWriter = imageWriter;
            return this;
        }

        public Builder setRayTracer(RayTracerBase rayTracer) {
            _camera.rayTracer = rayTracer;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
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
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
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
    }
}