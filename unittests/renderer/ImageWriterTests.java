package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Testing the ImageWriter class.
 *
 * @author Miri and Yael
 */
class ImageWriterTests {
    /**
     * Default constructor for ImageWriterTests.
     */
    public ImageWriterTests() {
    }

    /**
     * Test method for writing a basic image with a grid.
     */
    @Test
    void testImageWriter() {
        // Constants for resolution and grid size to avoid Hard Code
        final int nX = 800;
        final int nY = 500;
        final int step = 50;

        // High contrast colors for background and grid
        final Color yellow = new Color(255, 255, 0);
        final Color red = new Color(255, 0, 0);

        // Create the ImageWriter object
        ImageWriter imageWriter = new ImageWriter(nX, nY);

        // Nested loop to color all pixels
        for (int i = 0; i < nX; i++) {
            for (int j = 0; j < nY; j++) {
                // Ternary operator to avoid code duplication
                imageWriter.writePixel(i, j, i % step == 0 || j % step == 0 ? red : yellow);
            }
        }

        // Generate the final image file
        imageWriter.writeToImage("base_render_test");
    }
}