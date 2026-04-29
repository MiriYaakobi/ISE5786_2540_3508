package geometries.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Point;
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
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null; // Start with null for performance

        for (Intersectable item : _geometries) {
            List<Point> itemIntersections = item.findIntersections(ray);

            if (itemIntersections != null) {
                // Lazy initialization: only create the list if there's an intersection
                if (result == null) {
                    result = new ArrayList<>(itemIntersections);
                } else {
                    result.addAll(itemIntersections);
                }
            }
        }
        return result; // Returns null if no intersections were found
    }
}