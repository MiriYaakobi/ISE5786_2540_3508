package geometries.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Ray;

/**
 * Composite class for all intersectable objects.
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
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return calcIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        for (Intersectable item : _geometries) {
            // Propagate the maxDistance optimization to all child geometries
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
}