package renderer;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.sampling.JitterSampler;
import renderer.sampling.TargetAreaSampler;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class represents a physical camera in 3D space.
 * Extended for Stage 9 to include Super-sampling (Anti-Aliasing), Multi-threading
 * (with parallel streams support), and optimized Adaptive Super-Sampling.
 *
 * @author Miri and Yael
 */
public class Camera implements Cloneable {
    /**
     * Camera location point.
     */
    private Point p0;
    /**
     * Camera vertical direction vector (up).
     */
    private Vector vUp;
    /**
     * Camera forward direction vector (to).
     */
    private Vector vTo;
    /**
     * Camera right direction vector (right).
     */
    private Vector vRight;

    /**
     * View plane width.
     */
    private double width;
    /**
     * View plane height.
     */
    private double height;
    /**
     * Distance between camera and view plane.
     */
    private double distance;

    /**
     * Number of pixels in the x-axis (horizontal resolution).
     */
    private int nX = 1;
    /**
     * Number of pixels in the y-axis (vertical resolution).
     */
    private int nY = 1;

    /**
     * Center point of the view plane.
     */
    private Point viewPlaneCenter;
    /**
     * Width of a single pixel on the view plane.
     */
    private double pixelWidth;
    /**
     * Height of a single pixel on the view plane.
     */
    private double pixelHeight;

    /**
     * Image writer used to create the output image file.
     */
    private ImageWriter imageWriter;
    /**
     * Ray tracer used to calculate the color of each ray.
     */
    private RayTracerBase rayTracer;

    // --- Added for Stage 9 ---
    /**
     * Number of threads to use for rendering (0 for no multithreading).
     */
    private int threadsCount = 0;
    /**
     * Interval for printing progress messages (0 for no printing).
     */
    private double printInterval = 0;
    /**
     * Number of rays to cast for each pixel (anti-aliasing).
     */
    private int antiAliasingRays = 1;
    /**
     * Flag to enable/disable adaptive super-sampling.
     */
    private boolean useAdaptive = false;
    /**
     * Maximum recursion depth for adaptive super-sampling.
     */
    private int adaptiveMaxDepth = 2;
    /**
     * Pixel manager for supporting multi-threading and debug print of progress.
     */
    private PixelManager pixelManager;
    // -------------------------

    /**
     * Private default constructor for Camera.
     */
    private Camera() {
    }

    /**
     * Gets a new instance of Camera.Builder.
     *
     * @return a new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Gets the camera location.
     *
     * @return the camera location point
     */
    public Point getP0() {
        return p0;
    }

    /**
     * Gets the up vector.
     *
     * @return the up direction vector
     */
    public Vector getVUp() {
        return vUp;
    }

    /**
     * Gets the to vector.
     *
     * @return the forward direction vector
     */
    public Vector getVTo() {
        return vTo;
    }

    /**
     * Gets the right vector.
     *
     * @return the right direction vector
     */
    public Vector getVRight() {
        return vRight;
    }

    /**
     * Gets the view plane width.
     *
     * @return the width of the view plane
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the view plane height.
     *
     * @return the height of the view plane
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets the distance to the view plane.
     *
     * @return the distance between camera and view plane
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Gets the horizontal resolution.
     *
     * @return the number of pixels in the x-axis
     */
    public int getNx() {
        return nX;
    }

    /**
     * Gets the vertical resolution.
     *
     * @return the number of pixels in the y-axis
     */
    public int getNy() {
        return nY;
    }

    /**
     * Constructs a ray through a specific pixel on the view plane.
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row index
     * @return the constructed ray passing through the pixel center
     */
    public Ray constructRay(int xIndex, int yIndex) {
        Point pIJ = getPixelCenter(nX, nY, xIndex, yIndex);
        Vector vIJ = pIJ.subtract(p0);
        return new Ray(p0, vIJ);
    }

    /**
     * Calculates the center point of a specific pixel (j, i).
     *
     * @param nX horizontal resolution
     * @param nY vertical resolution
     * @param j  pixel column index
     * @param i  pixel row index
     * @return the center point of the pixel
     */
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
     *
     * @param p  the starting point
     * @param dx the horizontal offset
     * @param dy the vertical offset
     * @return the moved point
     */
    private Point movePoint(Point p, double dx, double dy) {
        Point res = p;
        if (!isZero(dx)) res = res.add(vRight.scale(dx));
        if (!isZero(dy)) res = res.add(vUp.scale(dy));
        return res;
    }

    /**
     * Stage 9: Generic flexible infrastructure for Jittered Grid (Super-sampling).
     * Delegates the generation of sample points to the JitterSampler infrastructure.
     *
     * @param j        pixel column index
     * @param i        pixel row index
     * @param gridSize the size of the jittered grid (e.g., 3 for a 3x3 grid)
     * @return list of rays for the target area
     */
    public List<Ray> constructRaysTargetArea(int j, int i, int gridSize) {
        Point pIJ = getPixelCenter(nX, nY, j, i);

        if (gridSize <= 1) {
            List<Ray> singleRay = new LinkedList<>();
            singleRay.add(new Ray(p0, pIJ.subtract(p0)));
            return singleRay;
        }

        TargetAreaSampler sampler = new JitterSampler(gridSize);
        List<TargetAreaSampler.Point2D> offsets = sampler.generatePoints(pixelWidth, pixelHeight);
        List<Point> targetPoints = sampler.mapPointsTo3D(pIJ, vUp, vRight, offsets);

        return targetPoints.stream().map(p -> new Ray(p0, p.subtract(p0))).toList();
    }

    /**
     * Stage 9: Optimized Adaptive Super-Sampling Recursive algorithm.
     * Passes the pre-calculated corner colors to avoid 75% of redundant ray tracing!
     *
     * @param center   the center point of the current subdivision
     * @param w        width of the current subdivision
     * @param h        height of the current subdivision
     * @param depth    current recursion depth
     * @param maxDepth maximum recursion depth
     * @param cTl      color of the top-left corner
     * @param cTr      color of the top-right corner
     * @param cBl      color of the bottom-left corner
     * @param cBr      color of the bottom-right corner
     * @return the calculated color for the area
     */
    private Color calcAdaptiveColor(Point center, double w, double h, int depth, int maxDepth, Color cTl, Color cTr, Color cBl, Color cBr) {
        if (depth >= maxDepth || (cTl.equals(cTr) && cTl.equals(cBl) && cTl.equals(cBr))) {
            return cTl.add(cTr).add(cBl).add(cBr).reduce(4);
        }

        Color cTop = rayTracer.traceRay(new Ray(p0, movePoint(center, 0, h / 2).subtract(p0)));
        Color cBot = rayTracer.traceRay(new Ray(p0, movePoint(center, 0, -h / 2).subtract(p0)));
        Color cLeft = rayTracer.traceRay(new Ray(p0, movePoint(center, -w / 2, 0).subtract(p0)));
        Color cRight = rayTracer.traceRay(new Ray(p0, movePoint(center, w / 2, 0).subtract(p0)));
        Color cCenter = rayTracer.traceRay(new Ray(p0, center.subtract(p0)));

        Color topL = calcAdaptiveColor(movePoint(center, -w / 4, h / 4), w / 2, h / 2, depth + 1, maxDepth, cTl, cTop, cLeft, cCenter);
        Color topR = calcAdaptiveColor(movePoint(center, w / 4, h / 4), w / 2, h / 2, depth + 1, maxDepth, cTop, cTr, cCenter, cRight);
        Color botL = calcAdaptiveColor(movePoint(center, -w / 4, -h / 4), w / 2, h / 2, depth + 1, maxDepth, cLeft, cCenter, cBl, cBot);
        Color botR = calcAdaptiveColor(movePoint(center, w / 4, -h / 4), w / 2, h / 2, depth + 1, maxDepth, cCenter, cRight, cBot, cBr);

        return topL.add(topR).add(botL).add(botR).reduce(4);
    }

    /**
     * Casts rays for a single pixel and sets the pixel's color in the image.
     * Handles both standard jittered grid and adaptive super-sampling.
     *
     * @param nX horizontal resolution
     * @param nY vertical resolution
     * @param j  pixel column index
     * @param i  pixel row index
     */
    private void castRays(int nX, int nY, int j, int i) {
        Color pixelColor = Color.BLACK;

        if (antiAliasingRays <= 1) {
            pixelColor = rayTracer.traceRay(constructRay(j, i));
        } else if (useAdaptive) {
            Point pIJ = getPixelCenter(nX, nY, j, i);

            Point tl = movePoint(pIJ, -pixelWidth / 2, pixelHeight / 2);
            Point tr = movePoint(pIJ, pixelWidth / 2, pixelHeight / 2);
            Point bl = movePoint(pIJ, -pixelWidth / 2, -pixelHeight / 2);
            Point br = movePoint(pIJ, pixelWidth / 2, -pixelHeight / 2);

            Color cTl = rayTracer.traceRay(new Ray(p0, tl.subtract(p0)));
            Color cTr = rayTracer.traceRay(new Ray(p0, tr.subtract(p0)));
            Color cBl = rayTracer.traceRay(new Ray(p0, bl.subtract(p0)));
            Color cBr = rayTracer.traceRay(new Ray(p0, br.subtract(p0)));

            pixelColor = calcAdaptiveColor(pIJ, pixelWidth, pixelHeight, 1, adaptiveMaxDepth, cTl, cTr, cBl, cBr);
        } else {
            int gridSize = (int) Math.ceil(Math.sqrt(antiAliasingRays));
            List<Ray> rays = constructRaysTargetArea(j, i, gridSize);
            for (Ray r : rays) {
                pixelColor = pixelColor.add(rayTracer.traceRay(r));
            }
            pixelColor = pixelColor.reduce(rays.size());
        }

        imageWriter.writePixel(j, i, pixelColor);
    }

    /**
     * Renders the image by casting rays for every pixel.
     * Routes to the correct threading method based on Stage 9 instructions.
     *
     * @return the camera instance itself (fluent API)
     */
    public Camera renderImage() {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        if (rayTracer == null) throw new MissingResourceException("Missing ray tracer", "Camera", "rayTracer");

        pixelManager = new PixelManager(nY, nX, printInterval);

        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Render image using multi-threading by parallel streaming (The fastest method).
     *
     * @return the camera object itself
     */
    private Camera renderImageStream() {
        java.util.stream.IntStream.range(0, nY).parallel()
                .forEach(i -> java.util.stream.IntStream.range(0, nX).parallel()
                        .forEach(j -> {
                            castRays(nX, nY, j, i);
                            pixelManager.pixelDone();
                        }));
        return this;
    }

    /**
     * Render image without multi-threading (Standard linear processing).
     *
     * @return the camera object itself
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i) {
            for (int j = 0; j < nX; ++j) {
                castRays(nX, nY, j, i);
                pixelManager.pixelDone();
            }
        }
        return this;
    }

    /**
     * Render image using multi-threading by creating and running raw threads.
     *
     * @return the camera object itself
     */
    private Camera renderImageRawThreads() {
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
            } catch (InterruptedException ignored) {
            }
        }
        return this;
    }

    /**
     * Prints a grid on top of the image for debugging purposes.
     *
     * @param interval the distance between grid lines
     * @param color    the color of the grid lines
     * @return the camera instance itself (fluent API)
     */
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

    /**
     * Delegates to ImageWriter to produce the final image file.
     *
     * @param imageName the name of the output image file
     */
    public void writeToImage(String imageName) {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        imageWriter.writeToImage(imageName);
    }

    /**
     * Builder class for Camera.
     * Provides a fluent API for constructing Camera instances.
     */
    public static class Builder {
        /**
         * The camera object being built.
         */
        private final Camera _camera = new Camera();
        /**
         * The target point the camera is looking at.
         */
        private Point _target = null;
        /**
         * The initial up vector for the camera.
         */
        private Vector _vUpGen = Vector.AXIS_Y;

        /**
         * Default constructor for the Builder.
         */
        public Builder() {
        }

        /**
         * Sets the camera location.
         *
         * @param location the camera location point
         * @return the builder instance
         */
        public Builder setLocation(Point location) {
            _camera.p0 = location;
            return this;
        }

        /**
         * Sets the camera direction using vectors.
         *
         * @param to the forward direction vector
         * @param up the up direction vector
         * @return the builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            _camera.vTo = to;
            _vUpGen = up;
            _target = null;
            return this;
        }

        /**
         * Sets the camera direction looking at a target point.
         *
         * @param target the point to look at
         * @param up     the up direction vector
         * @return the builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vUpGen = up;
            _camera.vTo = null;
            return this;
        }

        /**
         * Sets the camera direction looking at a target point with default up.
         *
         * @param target the point to look at
         * @return the builder instance
         */
        public Builder setDirection(Point target) {
            _target = target;
            _camera.vTo = null;
            return this;
        }

        /**
         * Sets the view plane size.
         *
         * @param width  the width of the view plane
         * @param height the height of the view plane
         * @return the builder instance
         */
        public Builder setVpSize(double width, double height) {
            _camera.width = width;
            _camera.height = height;
            return this;
        }

        /**
         * Sets the distance to the view plane.
         *
         * @param distance the distance between camera and view plane
         * @return the builder instance
         */
        public Builder setVpDistance(double distance) {
            _camera.distance = distance;
            return this;
        }

        /**
         * Sets the horizontal and vertical resolution.
         *
         * @param nX number of pixels in the x-axis
         * @param nY number of pixels in the y-axis
         * @return the builder instance
         */
        public Builder setResolution(int nX, int nY) {
            _camera.nX = nX;
            _camera.nY = nY;
            return this;
        }

        /**
         * Sets the image writer.
         *
         * @param imageWriter the image writer instance
         * @return the builder instance
         */
        public Builder setImageWriter(ImageWriter imageWriter) {
            _camera.imageWriter = imageWriter;
            return this;
        }

        /**
         * Sets the ray tracer.
         *
         * @param rayTracer the ray tracer instance
         * @return the builder instance
         */
        public Builder setRayTracer(RayTracerBase rayTracer) {
            _camera.rayTracer = rayTracer;
            return this;
        }

        /**
         * Sets the ray tracer by type and scene.
         *
         * @param scene the scene to trace
         * @param type  the type of ray tracer to use
         * @return the builder instance
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) _camera.rayTracer = new SimpleRayTracer(scene);
            return this;
        }

        /**
         * Sets the number of threads for multi-threaded rendering.
         *
         * @param threads number of threads (0 for none, -1 for auto/stream)
         * @return the builder instance
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2) throw new IllegalArgumentException("Multithreading must be -2 or higher");
            if (threads >= -1) _camera.threadsCount = threads;
            else _camera.threadsCount = Runtime.getRuntime().availableProcessors();
            return this;
        }

        /**
         * Sets the debug print interval.
         *
         * @param interval interval in seconds between progress prints
         * @return the builder instance
         */
        public Builder setDebugPrint(double interval) {
            _camera.printInterval = interval;
            return this;
        }

        /**
         * Sets the number of rays for anti-aliasing.
         *
         * @param rays number of rays per pixel
         * @return the builder instance
         */
        public Builder setAntiAliasingRays(int rays) {
            if (rays < 1) throw new IllegalArgumentException("Rays must be at least 1");
            _camera.antiAliasingRays = rays;
            return this;
        }

        /**
         * Enables or disables adaptive super-sampling.
         *
         * @param useAdaptive true to enable, false to disable
         * @return the builder instance
         */
        public Builder setAdaptive(boolean useAdaptive) {
            _camera.useAdaptive = useAdaptive;
            return this;
        }

        /**
         * Sets the maximum recursion depth for adaptive super-sampling.
         * Removes hard-coded limits to satisfy Stage 9 configuration requirements.
         *
         * @param depth the maximum recursion depth
         * @return the builder instance
         */
        public Builder setAdaptiveMaxDepth(int depth) {
            if (depth < 1) throw new IllegalArgumentException("Depth must be at least 1");
            _camera.adaptiveMaxDepth = depth;
            return this;
        }

        /**
         * Rotates the camera around its forward axis.
         *
         * @param angle the rotation angle in degrees
         * @return the builder instance
         */
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

        /**
         * Builds and returns a new Camera instance.
         * Performs validations before construction.
         *
         * @return the constructed Camera instance
         */
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

        /**
         * Checks if the resolution is valid and initializes the image writer.
         */
        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0) throw new IllegalArgumentException("Resolution must be positive");
            _camera.imageWriter = new ImageWriter(_camera.nX, _camera.nY);
        }

        /**
         * Checks if the view plane size and distance are valid.
         * Initializes the view plane center and pixel dimensions.
         */
        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            if (alignZero(_camera.distance) <= 0) throw new IllegalArgumentException("Distance must be positive");

            _camera.viewPlaneCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        /**
         * Checks if the location and direction are valid.
         * Normalizes the direction vector and calculates the right and up vectors.
         */
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