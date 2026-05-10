package geometries.impl;

import java.util.List;

import geometries.api.Intersectable.Intersection; // Added import
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
     * The central axis ray of the tube
     */
    protected final Ray _axis;

    /**
     * Constructor to initialize a tube with radius and axis ray.
     *
     * @param radius the radius of the tube
     * @param axis   the central axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    /**
     * Calculates the normal to the tube at a given point.
     *
     * @param point the point on the tube surface
     * @return normalized normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Vector from ray origin to the point: w = P - P0
        Vector p0ToPoint = point.subtract(p0);

        // Projection of p0ToPoint on the axis: t = v * (P - P0)
        double t = alignZero(v.dotProduct(p0ToPoint));

        // If t is zero, the projection point is exactly p0.
        if (isZero(t)) return p0ToPoint.normalize();

        // Use getPoint(t) from Ray class (Refactoring Step 5)
        Point o = _axis.getPoint(t);

        // The normal is (P - O) normalized
        return point.subtract(o).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) { // Renamed and changed return type/access
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Point pa = _axis.origin();
        Vector va = _axis.direction();

        // Vector deltaP = P0 - Pa
        Vector deltaP;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            deltaP = null; // Case where P0 == Pa
        }

        // Helper vector: a = v - (v * va) * va
        double vva = v.dotProduct(va);
        Vector vecA = v;
        if (!isZero(vva)) {
            try {
                vecA = v.subtract(va.scale(vva));
            } catch (IllegalArgumentException e) {
                return null; // Ray is parallel to the axis
            }
        }

        // Coefficients for quadratic equation at^2 + bt + c = 0
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
                    vecB = null; // dp is parallel to va
                }
            }

            if (vecB != null) {
                b = 2 * vecA.dotProduct(vecB);
                c += vecB.lengthSquared();
            }
        }

        // Solve quadratic equation
        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant <= 0) return null; // No intersection or tangent

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = alignZero((-b - sqrtDisc) / (2 * a));
        double t2 = alignZero((-b + sqrtDisc) / (2 * a));

        // Return points only if t > 0
        if (t1 > 0 && t2 > 0) return List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2))); // Changed to Intersection
        if (t1 > 0) return List.of(new Intersection(this, ray.getPoint(t1))); // Changed to Intersection
        if (t2 > 0) return List.of(new Intersection(this, ray.getPoint(t2))); // Changed to Intersection

        return null;
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", radius=" + _radius;
    }
}
