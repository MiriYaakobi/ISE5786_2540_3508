package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Tube represents an infinite tube (cylinder) in 3D space.
 *
 * @author Miri and Yael
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis ray of the tube.
     */
    protected final Ray _axis;

    /**
     * Constructor to initialize a tube with radius and axis ray.
     * Sets infinite bounding boxes.
     *
     * @param radius the radius of the tube
     * @param axis   the central axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;

        _minX = Double.NEGATIVE_INFINITY;
        _maxX = Double.POSITIVE_INFINITY;
        _minY = Double.NEGATIVE_INFINITY;
        _maxY = Double.POSITIVE_INFINITY;
        _minZ = Double.NEGATIVE_INFINITY;
        _maxZ = Double.POSITIVE_INFINITY;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        Vector p0ToPoint = point.subtract(p0);

        double t = alignZero(v.dotProduct(p0ToPoint));

        if (isZero(t)) return p0ToPoint.normalize();

        Point o = _axis.getPoint(t);

        return point.subtract(o).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Point pa = _axis.origin();
        Vector va = _axis.direction();

        Vector deltaP;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            deltaP = null;
        }

        double vva = v.dotProduct(va);
        Vector vecA = v;
        if (!isZero(vva)) {
            try {
                vecA = v.subtract(va.scale(vva));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        double a = vecA.lengthSquared();
        double b = 0;
        double c = -_radiusSquared;

        if (deltaP != null) {
            double dpva = deltaP.dotProduct(va);
            Vector vecB = deltaP;
            if (!isZero(dpva)) {
                try {
                    vecB = deltaP.subtract(va.scale(dpva));
                } catch (IllegalArgumentException e) {
                    vecB = null;
                }
            }

            if (vecB != null) {
                b = 2 * vecA.dotProduct(vecB);
                c += vecB.lengthSquared();
            }
        }

        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant <= 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = alignZero((-b - sqrtDisc) / (2 * a));
        double t2 = alignZero((-b + sqrtDisc) / (2 * a));

        if (t1 > 0 && t2 > 0)
            return List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2)));
        if (t1 > 0) return List.of(new Intersection(this, ray.getPoint(t1)));
        if (t2 > 0) return List.of(new Intersection(this, ray.getPoint(t2)));

        return null;
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", radius=" + _radius;
    }
}