package renderer;

import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the LightSource implementations:
 * DirectionalLight, PointLight, and SpotLight.
 * Focuses on testing getL() and getIntensity() methods.
 * * @author Miri and Yael
 */
class LightSourceTests {

    /**
     * Default constructor for LightSourceTests.
     * Provided explicitly to satisfy strict Javadoc documentation rules.
     */
    public LightSourceTests() {
        // Explicit default constructor for Javadoc compliance
    }

    /**
     * Test method for DirectionalLight
     */
    @Test
    void testDirectionalLight() {
        DirectionalLight dirLight = new DirectionalLight(new Color(100, 200, 300), new Vector(0, 0, -1));
        Point p = new Point(0, 0, 10);

        // TC01: getIntensity should return the initial color regardless of distance
        assertEquals(new Color(100, 200, 300), dirLight.getIntensity(p), "DirectionalLight getIntensity is wrong");

        // TC02: getL should return the normalized direction vector regardless of the point
        assertEquals(new Vector(0, 0, -1), dirLight.getL(p), "DirectionalLight getL is wrong");
    }

    /**
     * Test method for PointLight
     */
    @Test
    void testPointLight() {
        // Initial color is (300, 600, 900)
        PointLight pointLight = new PointLight(new Color(300, 600, 900), new Point(0, 0, 0))
                .setKc(1).setKl(0.1).setKq(0.01);

        Point p = new Point(0, 0, 10);

        // Distance is 10.
        // Attenuation = kC + kL*d + kQ*d^2 = 1 + 0.1*10 + 0.01*100 = 1 + 1 + 1 = 3.
        // Expected intensity = (300, 600, 900) / 3 = (100, 200, 300).
        // TC01: getIntensity with attenuation
        assertEquals(new Color(100, 200, 300), pointLight.getIntensity(p), "PointLight getIntensity with attenuation is wrong");

        // TC02: getL should return normalized vector from light (0,0,0) to point (0,0,10) -> (0,0,1)
        assertEquals(new Vector(0, 0, 1), pointLight.getL(p), "PointLight getL is wrong");
    }

    /**
     * Test method for SpotLight
     */
    @Test
    void testSpotLight() {
        Point p = new Point(0, 0, 10);

        // SpotLight pointing exactly towards the point p (0,0,1)
        SpotLight spotLight = new SpotLight(new Color(300, 600, 900), new Point(0, 0, 0), new Vector(0, 0, 1))
                .setKc(1).setKl(0.1).setKq(0.01);

        // The attenuation is 3 (same as PointLight).
        // The angle between direction (0,0,1) and L (0,0,1) is 0 degrees, cosine is 1.
        // Expected intensity = (100, 200, 300) * 1 = (100, 200, 300).
        // TC01: getIntensity exactly on the beam center
        assertEquals(new Color(100, 200, 300), spotLight.getIntensity(p), "SpotLight getIntensity is wrong");

        // TC02: getL should be the same as PointLight
        assertEquals(new Vector(0, 0, 1), spotLight.getL(p), "SpotLight getL is wrong");

        // TC03: Point is behind the SpotLight (angle > 90 degrees)
        SpotLight spotLightBehind = new SpotLight(new Color(300, 600, 900), new Point(0, 0, 0), new Vector(0, 0, -1))
                .setKc(1).setKl(0.1).setKq(0.01);

        // Intensity must be BLACK (0,0,0)
        assertEquals(Color.BLACK, spotLightBehind.getIntensity(p), "SpotLight getIntensity for point behind the light is wrong");
    }
}