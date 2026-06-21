package geometries.impl;

import java.util.List;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * This class represents a plane in 3D space.
 * It inherits from the Geometry abstract class.
 *
 * @author Miri and Yael
 */
public class Plane extends Geometry {
    /**
     * A point on the plane used as a reference point.
     */
    private final Point _point;

    /**
     * The normal vector to the plane.
     */
    private final Vector _normal;

    /**
     * Constructor to initialize a plane from three points.
     * Sets infinite bounding boxes.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;

        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        _normal = v1.crossProduct(v2).normalize();

        initInfiniteBounds();
    }

    /**
     * Constructor to initialize a plane from a point and a normal vector.
     * Sets infinite bounding boxes.
     *
     * @param point  a point on the plane
     * @param normal the normal vector to the plane
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();

        initInfiniteBounds();
    }

    /**
     * Helper to initialize infinite bounds for the geometry.
     */
    private void initInfiniteBounds() {
        _minX = Double.NEGATIVE_INFINITY;
        _maxX = Double.POSITIVE_INFINITY;
        _minY = Double.NEGATIVE_INFINITY;
        _maxY = Double.POSITIVE_INFINITY;
        _minZ = Double.NEGATIVE_INFINITY;
        _maxZ = Double.POSITIVE_INFINITY;
    }

    /**
     * Getter for the point on the plane.
     *
     * @return the reference point
     */
    public Point getPoint() {
        return _point;
    }

    @Override
    public Vector getNormal(Point unused) {
        return _normal;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector n = _normal;

        double nv = alignZero(n.dotProduct(v));

        if (isZero(nv)) {
            return null;
        }

        Vector p0_q0;
        try {
            p0_q0 = _point.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            return null;
        }

        double nQminusP = alignZero(n.dotProduct(p0_q0));
        double t = alignZero(nQminusP / nv);

        return t <= 0 ? null : List.of(new Intersection(this, ray.getPoint(t)));
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }
}