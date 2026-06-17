package renderer.sampling;

import java.util.List;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for target area sampling (Blackboard pattern).
 * Responsible for generating a list of 2D points within a specified target area.
 *
 * @author Miri and Yael
 */
public abstract class TargetAreaSampler {

    /**
     * Helper class to represent 2D offset points, avoiding dependency on 3D Point getters.
     */
    public static class Point2D {
        public final double x;
        public final double y;

        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Number of rows/columns in the sampling grid (or equivalent resolution).
     */
    protected int gridSize;

    /**
     * Constructor for TargetAreaSampler.
     *
     * @param gridSize the resolution of the sampling area
     */
    public TargetAreaSampler(int gridSize) {
        if (gridSize < 1) {
            throw new IllegalArgumentException("Grid size must be at least 1");
        }
        this.gridSize = gridSize;
    }

    /**
     * Sets a new grid size for the sampler.
     *
     * @param newGridSize the new grid size
     * @return the sampler object itself
     */
    public TargetAreaSampler setGridSize(int newGridSize) {
        if (newGridSize < 1) {
            throw new IllegalArgumentException("Grid size must be at least 1");
        }
        this.gridSize = newGridSize;
        return this;
    }

    /**
     * Gets the current grid size.
     *
     * @return the grid size
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Generates a list of 2D offset points within the target area.
     *
     * @param width  the width of the target area
     * @param height the height of the target area
     * @return a list of 2D points (x, y) representing offsets from the center
     */
    public abstract List<Point2D> generatePoints(double width, double height);

    /**
     * Maps a list of 2D offset points to 3D points in the world coordinate system.
     *
     * @param center  the center point of the target area in 3D space
     * @param vUp     the up vector of the target area
     * @param vRight  the right vector of the target area
     * @param offsets the list of 2D offset points
     * @return a list of 3D points
     */
    public List<Point> mapPointsTo3D(Point center, Vector vUp, Vector vRight, List<Point2D> offsets) {
        return offsets.stream()
                .map(offset -> {
                    Point p = center;
                    if (offset.x != 0) p = p.add(vRight.scale(offset.x));
                    if (offset.y != 0) p = p.add(vUp.scale(offset.y));
                    return p;
                })
                .toList();
    }
}