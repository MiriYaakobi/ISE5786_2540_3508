package geometries.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Ray;

/**
 * Composite class for all intersectable objects.
 * Extended for Stage 10 to support manual and automated BVH structure creation,
 * as well as flattening functionality for performance comparisons.
 *
 * @author Miri and Yael
 */
public class Geometries extends Intersectable {
    /**
     * List of intersectable geometries
     */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Default empty constructor
     */
    public Geometries() {
    }

    /**
     * Constructor that receives a list of geometries
     *
     * @param geometries geometries to add to the collection
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds a collection of geometries to the list using DRY principle
     *
     * @param geometries zero or more geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
        refreshBox();
    }

    /**
     * Recalculates the total bounding box enclosing all sub-geometries.
     */
    public void refreshBox() {
        if (_geometries.isEmpty()) {
            _minX = _minY = _minZ = Double.POSITIVE_INFINITY;
            _maxX = _maxY = _maxZ = Double.NEGATIVE_INFINITY;
            return;
        }

        BoxBounds bounds = new BoxBounds();
        for (Intersectable geo : _geometries) {
            bounds.include(geo);
        }
        applyBounds(bounds);
    }

    /**
     * Helper method to apply calculated bounds to this instance.
     *
     * @param bounds the BoxBounds object containing the calculated min/max coordinates
     */
    private void applyBounds(BoxBounds bounds) {
        _minX = bounds.minX;
        _maxX = bounds.maxX;
        _minY = bounds.minY;
        _maxY = bounds.maxY;
        _minZ = bounds.minZ;
        _maxZ = bounds.maxZ;
    }

    /**
     * Stage 10 Requirement: Flattens the geometries hierarchy into a single-level list.
     * Essential for the baseline measurements in the performance table.
     */
    public void flatten() {
        List<Intersectable> flatList = new ArrayList<>();
        flattenRecursive(this, flatList);
        _geometries.clear();
        _geometries.addAll(flatList);
        refreshBox();
    }

    /**
     * Recursive helper for flattening the hierarchy.
     *
     * @param current  the current intersectable node being inspected
     * @param flatList the list accumulating flattened (leaf) intersectables
     */
    private void flattenRecursive(Intersectable current, List<Intersectable> flatList) {
        if (current instanceof Geometries geos) {
            for (Intersectable item : geos._geometries) {
                flattenRecursive(item, flatList);
            }
        } else {
            flatList.add(current);
        }
    }

    /**
     * Automatically constructs a Bounding Volume Hierarchy tree using a Top-Down approach.
     * Splits geometries based on the longest axis of the current bounding volume.
     */
    public void buildBVH() {
        if (_geometries.size() <= 2) {
            return;
        }
        List<Intersectable> list = new ArrayList<>(_geometries);
        _geometries.clear();
        _geometries.add(buildBVHRecursive(list));
        refreshBox();
    }

    /**
     * Recursive helper method to partition the object list into a binary BVH tree structure.
     *
     * @param list the list of intersectable objects to partition into a BVH subtree
     * @return an Intersectable representing the root of the constructed subtree (single geometry or compound)
     */
    private Intersectable buildBVHRecursive(List<Intersectable> list) {
        if (list.size() == 1) {
            return list.getFirst();
        }
        if (list.size() == 2) {
            Geometries subGeo = new Geometries(list.get(0), list.get(1));
            subGeo.refreshBox();
            return subGeo;
        }

        BoxBounds bounds = new BoxBounds();
        for (Intersectable geo : list) {
            bounds.include(geo);
        }

        double lenX = bounds.maxX - bounds.minX;
        double lenY = bounds.maxY - bounds.minY;
        double lenZ = bounds.maxZ - bounds.minZ;

        final int axis = getLongestAxis(lenX, lenY, lenZ);

        list.sort((g1, g2) -> {
            double c1, c2;
            if (axis == 0) {
                c1 = (g1.getMinX() + g1.getMaxX()) / 2.0;
                c2 = (g2.getMinX() + g2.getMaxX()) / 2.0;
            } else if (axis == 1) {
                c1 = (g1.getMinY() + g1.getMaxY()) / 2.0;
                c2 = (g2.getMinY() + g2.getMaxY()) / 2.0;
            } else {
                c1 = (g1.getMinZ() + g1.getMaxZ()) / 2.0;
                c2 = (g2.getMinZ() + g2.getMaxZ()) / 2.0;
            }
            return Double.compare(c1, c2);
        });

        int mid = list.size() / 2;
        List<Intersectable> leftList = new ArrayList<>(list.subList(0, mid));
        List<Intersectable> rightList = new ArrayList<>(list.subList(mid, list.size()));

        Geometries leftSub = new Geometries();
        leftSub._geometries.add(buildBVHRecursive(leftList));
        leftSub.refreshBox();

        Geometries rightSub = new Geometries();
        rightSub._geometries.add(buildBVHRecursive(rightList));
        rightSub.refreshBox();

        Geometries parent = new Geometries(leftSub, rightSub);
        parent.refreshBox();
        return parent;
    }

    /**
     * Helper method to determine the longest axis (0 = X, 1 = Y, 2 = Z).
     *
     * @param lenX length of the bounding box along X axis
     * @param lenY length of the bounding box along Y axis
     * @param lenZ length of the bounding box along Z axis
     * @return index of the longest axis: 0 for X, 1 for Y, 2 for Z
     */
    private int getLongestAxis(double lenX, double lenY, double lenZ) {
        if (lenX >= lenY && lenX >= lenZ) return 0;
        if (lenY >= lenX && lenY >= lenZ) return 1;
        return 2;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return calcIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        for (Intersectable item : _geometries) {
            List<Intersection> itemIntersections = item.calcIntersections(ray, maxDistance);

            if (itemIntersections != null) {
                if (result == null) {
                    result = new ArrayList<>(itemIntersections);
                } else {
                    result.addAll(itemIntersections);
                }
            }
        }
        return result;
    }

    /**
     * Helper class to encapsulate bounding box limits and reduce code duplication.
     */
    private static class BoxBounds {
        /**
         * minimum X coordinate observed
         */
        double minX = Double.POSITIVE_INFINITY;
        /**
         * maximum X coordinate observed
         */
        double maxX = Double.NEGATIVE_INFINITY;
        /**
         * minimum Y coordinate observed
         */
        double minY = Double.POSITIVE_INFINITY;
        /**
         * maximum Y coordinate observed
         */
        double maxY = Double.NEGATIVE_INFINITY;
        /**
         * minimum Z coordinate observed
         */
        double minZ = Double.POSITIVE_INFINITY;
        /**
         * maximum Z coordinate observed
         */
        double maxZ = Double.NEGATIVE_INFINITY;

        /**
         * Default constructor for BoxBounds.
         */
        public BoxBounds() {
        }

        /**
         * Expands the current box to include the bounds of the provided geometry.
         *
         * @param geo the geometry whose bounds should be incorporated
         */
        void include(Intersectable geo) {
            minX = Math.min(minX, geo.getMinX());
            maxX = Math.max(maxX, geo.getMaxX());
            minY = Math.min(minY, geo.getMinY());
            maxY = Math.max(maxY, geo.getMaxY());
            minZ = Math.min(minZ, geo.getMinZ());
            maxZ = Math.max(maxZ, geo.getMaxZ());
        }
    }
}