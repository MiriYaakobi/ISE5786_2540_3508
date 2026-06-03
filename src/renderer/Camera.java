package renderer;

import java.util.MissingResourceException;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class represents a physical camera in 3D space.
 * <p>
 * This class defines the viewpoint, orientation, and view plane configurations.
 * It is responsible for constructing rays through specific pixels.
 * </p>
 *
 * @author Miri and Yael
 */
public class Camera implements Cloneable {
    /**
     * Camera's location point (p0).
     */
    private Point p0;
    /**
     * Camera's 'up' direction vector (vUp).
     */
    private Vector vUp;
    /**
     * Camera's 'to' direction vector (vTo).
     */
    private Vector vTo;
    /**
     * Camera's 'right' direction vector (vRight).
     */
    private Vector vRight;

    /**
     * View plane's physical width.
     */
    private double width;
    /**
     * View plane's physical height.
     */
    private double height;
    /**
     * Physical distance from the camera to the view plane.
     */
    private double distance;

    /**
     * Number of columns in the view plane (horizontal resolution).
     */
    private int nX = 1;
    /**
     * Number of rows in the view plane (vertical resolution).
     */
    private int nY = 1;

    /**
     * The center point of the view plane.
     */
    private Point viewPlaneCenter;
    /**
     * The width of a single pixel on the view plane.
     */
    private double pixelWidth;
    /**
     * The height of a single pixel on the view plane.
     */
    private double pixelHeight;

    /**
     * The image writer used to create the image file.
     */
    private ImageWriter imageWriter;
    /**
     * The ray tracer used to calculate the color of each pixel.
     */
    private RayTracerBase rayTracer;

    /**
     * Private default constructor to prevent direct instantiation.
     */
    private Camera() {
    }

    /**
     * Static method to create a new Camera Builder.
     *
     * @return a new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Returns the camera location point.
     *
     * @return the camera location point
     */
    public Point getP0() {
        return p0;
    }

    /**
     * Returns the camera's 'up' direction vector.
     *
     * @return the camera's 'up' direction vector
     */
    public Vector getVUp() {
        return vUp;
    }

    /**
     * Returns the camera's 'to' direction vector.
     *
     * @return the camera's 'to' direction vector
     */
    public Vector getVTo() {
        return vTo;
    }

    /**
     * Returns the camera's 'right' direction vector.
     *
     * @return the camera's 'right' direction vector
     */
    public Vector getVRight() {
        return vRight;
    }

    /**
     * Returns the view plane width.
     *
     * @return the view plane width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the view plane height.
     *
     * @return the view plane height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the distance to the view plane.
     *
     * @return the distance to the view plane
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the horizontal resolution (number of columns).
     *
     * @return the horizontal resolution (number of columns)
     */
    public int getNx() {
        return nX;
    }

    /**
     * Returns the vertical resolution (number of rows).
     *
     * @return the vertical resolution (number of rows)
     */
    public int getNy() {
        return nY;
    }

    /**
     * Constructs a ray through a specific pixel (xIndex, yIndex) on the view plane.
     *
     * @param xIndex pixel column index (0 to nX-1)
     * @param yIndex pixel row index (0 to nY-1)
     * @return the constructed ray starting from camera and passing through the pixel center
     */
    public Ray constructRay(int xIndex, int yIndex) {
        double xOffset = alignZero((xIndex - (nX - 1) / 2.0) * pixelWidth);
        double yOffset = alignZero(-(yIndex - (nY - 1) / 2.0) * pixelHeight);

        Point pIJ = viewPlaneCenter;

        if (!isZero(xOffset)) {
            pIJ = pIJ.add(vRight.scale(xOffset));
        }
        if (!isZero(yOffset)) {
            pIJ = pIJ.add(vUp.scale(yOffset));
        }

        Vector vIJ = pIJ.subtract(p0);
        return new Ray(p0, vIJ);
    }

    /**
     * Renders the image by tracing rays for every pixel.
     *
     * @return the camera itself for method chaining
     */
    public Camera renderImage() {
        if (imageWriter == null) {
            throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        }
        if (rayTracer == null) {
            throw new MissingResourceException("Missing ray tracer", "Camera", "rayTracer");
        }

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                Ray ray = constructRay(j, i);
                Color pixelColor = rayTracer.traceRay(ray);
                imageWriter.writePixel(j, i, pixelColor);
            }
        }
        return this;
    }

    /**
     * Prints a grid on the image with a specified interval and color.
     *
     * @param interval the size of the grid squares (in pixels)
     * @param color    the color of the grid lines
     * @return the camera itself for method chaining
     */
    public Camera printGrid(int interval, Color color) {
        if (imageWriter == null) {
            throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        }

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
     * Produces the final image file.
     *
     * @param imageName the name of the image file to save
     */
    public void writeToImage(String imageName) {
        if (imageWriter == null) {
            throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        }
        imageWriter.writeToImage(imageName);
    }

    /**
     * Builder class for Camera construction using the Builder pattern.
     */
    public static class Builder {
        /**
         * Internal camera instance being populated by the builder.
         */
        private final Camera _camera = new Camera();
        /**
         * The target point the camera is oriented towards.
         */
        private Point _target = null;
        /**
         * The general 'up' vector for the initial camera orientation.
         */
        private Vector _vUpGen = Vector.AXIS_Y;

        /**
         * Default constructor for the Builder.
         */
        public Builder() {
        }

        /**
         * Sets the camera's location.
         *
         * @param location the camera's position
         * @return the Builder instance
         */
        public Builder setLocation(Point location) {
            _camera.p0 = location;
            return this;
        }

        /**
         * Sets direction using explicit 'to' and 'up' vectors.
         *
         * @param to the forward vector
         * @param up the general up vector
         * @return the Builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            _camera.vTo = to;
            _vUpGen = up;
            _target = null;
            return this;
        }

        /**
         * Sets direction using a target point and a general 'up' vector.
         *
         * @param target the point the camera looks at
         * @param up     the general up vector
         * @return the Builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vUpGen = up;
            _camera.vTo = null;
            return this;
        }

        /**
         * Sets direction using only a target point. Up defaults to AXIS_Y.
         *
         * @param target the point the camera looks at
         * @return the Builder instance
         */
        public Builder setDirection(Point target) {
            _target = target;
            _camera.vTo = null;
            return this;
        }

        /**
         * Sets the physical size of the view plane.
         *
         * @param width  the width dimension
         * @param height the height dimension
         * @return the Builder instance
         */
        public Builder setVpSize(double width, double height) {
            _camera.width = width;
            _camera.height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance the distance value
         * @return the Builder instance
         */
        public Builder setVpDistance(double distance) {
            _camera.distance = distance;
            return this;
        }

        /**
         * Sets the pixel resolution of the view plane.
         *
         * @param nX number of columns
         * @param nY number of rows
         * @return the Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            _camera.nX = nX;
            _camera.nY = nY;
            return this;
        }

        /**
         * Sets the image writer for the camera.
         *
         * @param imageWriter the image writer responsible for creating the image
         * @return the Builder instance
         */
        public Builder setImageWriter(ImageWriter imageWriter) {
            _camera.imageWriter = imageWriter;
            return this;
        }

        /**
         * Sets the ray tracer for the camera.
         *
         * @param rayTracer the ray tracer responsible for calculating pixel colors
         * @return the Builder instance
         */
        public Builder setRayTracer(RayTracerBase rayTracer) {
            _camera.rayTracer = rayTracer;
            return this;
        }

        /**
         * Sets the ray tracer for the camera using a scene and type (Enum).
         *
         * @param scene the scene
         * @param type  the type of the ray tracer
         * @return the Builder instance
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                _camera.rayTracer = new SimpleRayTracer(scene);
            }
            return this;
        }

        /**
         * Rotates the camera around its viewing direction vector (vTo).
         * Clockwise rotation in degrees.
         *
         * @param angle rotation angle in degrees
         * @return the Builder instance
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
         * Validates all data and constructs the final Camera object.
         *
         * @return a new validated Camera instance
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
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
            _camera.imageWriter = new ImageWriter(_camera.nX, _camera.nY);
        }

        /**
         * Checks if the view plane parameters are valid and computes pixel dimensions.
         */
        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            if (alignZero(_camera.distance) <= 0)
                throw new IllegalArgumentException("Distance must be positive");

            _camera.viewPlaneCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        /**
         * Checks if location and direction are valid and computes the orthogonal basis.
         */
        private void checkLocationAndDirection() {
            if (_camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            if (_camera.vTo == null) {
                if (_target == null)
                    throw new MissingResourceException("Missing direction", "Camera", "vTo");
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
