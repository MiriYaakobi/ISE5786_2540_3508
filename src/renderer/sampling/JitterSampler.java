package renderer.sampling;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static primitives.Util.alignZero;

/**
 * Generates sample points using a Jittered Grid pattern.
 * Divides the target area into a grid and places one random point within each cell.
 *
 * @author Miri and Yael
 */
public class JitterSampler extends TargetAreaSampler {

    /**
     * Constructor for JitterSampler.
     *
     * @param gridSize the resolution of the grid
     */
    public JitterSampler(int gridSize) {
        super(gridSize);
    }

    @Override
    public List<Point2D> generatePoints(double width, double height) {
        List<Point2D> points = new LinkedList<>();
        if (gridSize <= 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        double subPixelWidth = width / gridSize;
        double subPixelHeight = height / gridSize;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                double randomX = (ThreadLocalRandom.current().nextDouble() - 0.5) * subPixelWidth;
                double randomY = (ThreadLocalRandom.current().nextDouble() - 0.5) * subPixelHeight;

                double dx = alignZero((c - (gridSize - 1) / 2.0) * subPixelWidth + randomX);
                double dy = alignZero(-(r - (gridSize - 1) / 2.0) * subPixelHeight + randomY);

                points.add(new Point2D(dx, dy));
            }
        }
        return points;
    }
}