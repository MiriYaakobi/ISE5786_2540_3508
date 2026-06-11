package renderer;

/**
 * PixelManager is a helper class. It is used for multi-threading in the
 * renderer and for follow up its progress.<br/>
 * A Camera uses one pixel manager object and several Pixel objects - one in
 * each thread.
 */
class PixelManager {
    /**
     * Immutable class for object containing allocated pixel (with its row and
     * column numbers)
     *
     * @param col pixel column number
     * @param row pixel row number
     */
    record Pixel(int col, int row) {
    }

    private int maxRows = 0;
    private int maxCols = 0;
    private long totalPixels = 0l;

    private volatile int cRow = 0;
    private volatile int cCol = -1;
    private volatile long pixels = 0l;
    private volatile int lastPrinted = 0;

    private boolean print = false;
    private long printInterval = 100l;
    private static final String PRINT_FORMAT = "%5.1f%%\n";
    private Object mutexNext = new Object();
    private Object mutexPixels = new Object();

    /**
     * Initialize pixel manager data for multi-threading
     *
     * @param maxRows  the amount of pixel rows
     * @param maxCols  the amount of pixel columns
     * @param interval print time interval in percentage, 0 if printing is not required
     */
    PixelManager(int maxRows, int maxCols, double... interval) {
        if (interval.length > 1) throw new IllegalArgumentException("only up to one interval argument is allowed");
        this.maxRows = maxRows;
        this.maxCols = maxCols;
        totalPixels = (long) maxRows * maxCols;
        printInterval = interval.length == 0 ? printInterval : (long) (interval[0] * 10);
        print = printInterval != 0;
        if (print) {
            System.out.printf(PRINT_FORMAT, 0d);
            System.out.flush(); // Crucial for IntelliJ console updating!
        }
    }

    /**
     * Function for thread-safe manipulating of main follow up Pixel object.
     *
     * @return next pixel object, or null if there are no more pixels
     */
    Pixel nextPixel() {
        synchronized (mutexNext) {
            if (cRow == maxRows) return null;

            ++cCol;
            if (cCol < maxCols)
                return new Pixel(cCol, cRow);

            cCol = 0;
            ++cRow;
            if (cRow < maxRows)
                return new Pixel(cCol, cRow);
        }
        return null;
    }

    /**
     * Finish pixel processing by updating and printing of progress percentage
     */
    void pixelDone() {
        boolean flag = false;
        int percentage = 0;
        synchronized (mutexPixels) {
            ++pixels;
            if (print) {
                percentage = (int) (1000l * pixels / totalPixels);
                if (percentage - lastPrinted >= printInterval) {
                    lastPrinted = percentage;
                    flag = true;
                }
            }
            if (flag) {
                System.out.printf(PRINT_FORMAT, percentage / 10d);
                System.out.flush(); // Crucial for IntelliJ console updating!
            }
        }
    }
}