package renderer;

import java.util.MissingResourceException;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class represents a physical camera in 3D space.
 * Implements Cloneable as per Stage 4 requirements.
 *
 * @author Miri and Yael
 */
public class Camera implements Cloneable {
    // Camera location and orientation
    private Point p0;
    private Vector vUp;
    private Vector vTo;
    private Vector vRight;

    // View Plane geometry
    private double width;
    private double height;
    private double distance;

    // Resolution (columns and rows)
    private int nX = 1;
    private int nY = 1;

    // Pre-computed helper fields for performance optimization
    private Point viewPlaneCenter;
    private double pixelWidth;
    private double pixelHeight;

    /**
     * Private default constructor to prevent direct instantiation
     */
    private Camera() {
    }

    /**
     * Static method to create a new Camera Builder
     *
     * @return a new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    // Getters for Camera fields
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


    /**
     * Constructs a ray through a specific pixel (xIndex, yIndex).
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row index
     * @return the constructed ray
     */
    public Ray constructRay(int xIndex, int yIndex) {
        // Calculate the offset from View Plane center to pixel (xIndex, yIndex) center
        // x_offset = (xIndex - (nX - 1) / 2.0) * pixelWidth
        // y_offset = -(yIndex - (nY - 1) / 2.0) * pixelHeight (negative because Y-axis usually points down in image coordinates)
        double xOffset = (xIndex - (nX - 1) / 2.0) * pixelWidth;
        double yOffset = -(yIndex - (nY - 1) / 2.0) * pixelHeight;

        Point pIJ = viewPlaneCenter;

        // Apply horizontal and vertical offsets
        if (!isZero(xOffset)) {
            pIJ = pIJ.add(vRight.scale(xOffset));
        }
        if (!isZero(yOffset)) {
            pIJ = pIJ.add(vUp.scale(yOffset));
        }

        // Ray direction: Vector from camera location to pixel center
        Vector vIJ = pIJ.subtract(p0);

        return new Ray(p0, vIJ);
    }

    /**
     * Marker interface implementation for cloning.
     *
     * @return a clone of this Camera instance.
     * @throws CloneNotSupportedException if the object's class does not support the Cloneable interface.
     */
    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

    /**
     * Nested static Builder class for Camera construction
     */
    public static class Builder {
        private final Camera _camera = new Camera();

        // Temporary builder fields for intermediate data
        private Point _target = null;
        private Vector _vUpGen = Vector.AXIS_Y; // Default General Up (AXIS_Y)

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
         * Sets the camera's direction using 'to' and 'up' vectors.
         *
         * @param to the 'to' vector
         * @param up the 'up' vector
         * @return the Builder instance
         */
        public Builder setDirection(Vector to, Vector up) {
            _camera.vTo = to;
            _vUpGen = up;
            _target = null; // Reset target if direct vectors are provided
            return this;
        }

        /**
         * Sets the camera's direction using a target point and an 'up' vector.
         *
         * @param target the point the camera is looking at
         * @param up     the 'up' vector
         * @return the Builder instance
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vUpGen = up;
            _camera.vTo = null; // Will be calculated in build()
            return this;
        }

        /**
         * Sets the camera's direction using only a target point.
         * The 'up' vector will default to AXIS_Y.
         *
         * @param target the point the camera is looking at
         * @return the Builder instance
         */
        public Builder setDirection(Point target) {
            _target = target;
            _camera.vTo = null; // Will be calculated in build()
            // _vUpGen remains its default (AXIS_Y)
            return this;
        }

        /**
         * Sets the view plane's size.
         *
         * @param width  the width of the view plane
         * @param height the height of the view plane
         * @return the Builder instance
         */
        public Builder setVpSize(double width, double height) {
            _camera.width = width;
            _camera.height = height;
            return this;
        }

        /**
         * Sets the view plane's distance from the camera.
         *
         * @param distance the distance to the view plane
         * @return the Builder instance
         */
        public Builder setVpDistance(double distance) {
            _camera.distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane.
         *
         * @param nX the number of pixels in the X direction
         * @param nY the number of pixels in the Y direction
         * @return the Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            _camera.nX = nX;
            _camera.nY = nY;
            return this;
        }

        /**
         * Finalizes the construction of the camera.
         *
         * @return the constructed Camera object
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane(); // This method now also calculates helper fields
            return _camera.clone(); // Use the covariant clone method
        }

        /**
         * Checks if resolution values are positive.
         * Throws IllegalArgumentException if not.
         */
        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0) {
                throw new IllegalArgumentException("Resolution values must be positive");
            }
        }

        /**
         * Checks view plane dimensions and distance, and calculates helper fields.
         * Throws IllegalArgumentException if dimensions or distance are not positive.
         */
        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0) {
                throw new IllegalArgumentException("View plane size must be positive");
            }
            if (alignZero(_camera.distance) <= 0) {
                throw new IllegalArgumentException("View plane distance must be positive");
            }

            // Calculate and update helper fields in the camera object as per instructions
            _camera.viewPlaneCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }

        /**
         * Checks camera location and direction vectors.
         * Calculates missing direction vectors and normalizes them.
         * Throws MissingResourceException if location/direction is missing.
         * Throws IllegalArgumentException if vTo and vUpGen are parallel.
         */
        private void checkLocationAndDirection() {
            // Check for camera location
            if (_camera.p0 == null) {
                throw new MissingResourceException("Missing camera location", "Camera", "p0");
            }

            // Calculate vTo if missing (based on target)
            if (_camera.vTo == null) {
                if (_target == null) {
                    throw new MissingResourceException("Missing camera direction or target", "Camera", "vTo");
                }
                _camera.vTo = _target.subtract(_camera.p0);
            }
            _camera.vTo = _camera.vTo.normalize();

            // vRight - cross product of vTo and _vUpGen
            try {
                _camera.vRight = _camera.vTo.crossProduct(_vUpGen).normalize();
            } catch (IllegalArgumentException e) {
                // This happens if the vectors are parallel
                throw new IllegalArgumentException("Camera direction and Up vector cannot be parallel");
            }

            // vUp - cross product of vRight and vTo (ensures orthogonality)
            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
        }
    }
}
