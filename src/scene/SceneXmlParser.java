package scene;

import java.io.File;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import primitives.Color;
import primitives.Point;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parses an XML file to create a Scene object.
 * Adheres to SRP and OOP by isolating parsing logic from rendering and test classes.
 *
 * @author Miri and Yael
 */
public class SceneXmlParser {

    /**
     * Default constructor for SceneXmlParser.
     * Established as a utility-like class for XML parsing.
     */
    public SceneXmlParser() {
    }

    /**
     * Parses the XML file and generates a complete Scene.
     *
     * @param sceneName the name of the scene
     * @param filePath  the path to the XML file
     * @return a constructed Scene object
     */
    public static Scene parse(String sceneName, String filePath) {
        Scene scene = new Scene(sceneName);
        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                throw new IllegalArgumentException("XML file not found: " + filePath);
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            String bgColorStr = root.getAttribute("background-color");
            if (bgColorStr != null && !bgColorStr.isEmpty()) {
                scene.setBackground(parseColor(bgColorStr));
            }

            NodeList ambientList = root.getElementsByTagName("ambient-light");
            if (ambientList.getLength() > 0) {
                Element ambientElement = (Element) ambientList.item(0);
                Color color = parseColor(ambientElement.getAttribute("color"));
                scene.setAmbientLight(new AmbientLight(color));
            }

            NodeList geometriesList = root.getElementsByTagName("geometries");
            if (geometriesList.getLength() > 0) {
                Element geometriesElement = (Element) geometriesList.item(0);

                NodeList spheres = geometriesElement.getElementsByTagName("sphere");
                for (int i = 0; i < spheres.getLength(); i++) {
                    Element sphere = (Element) spheres.item(i);
                    Point center = parsePoint(sphere.getAttribute("center"));
                    double radius = Double.parseDouble(sphere.getAttribute("radius"));
                    scene.geometries.add(new Sphere(center, radius));
                }

                NodeList triangles = geometriesElement.getElementsByTagName("triangle");
                for (int i = 0; i < triangles.getLength(); i++) {
                    Element triangle = (Element) triangles.item(i);
                    Point p0 = parsePoint(triangle.getAttribute("p0"));
                    Point p1 = parsePoint(triangle.getAttribute("p1"));
                    Point p2 = parsePoint(triangle.getAttribute("p2"));
                    scene.geometries.add(new Triangle(p0, p1, p2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse XML file: " + filePath, e);
        }
        return scene;
    }

    /**
     * Helper method to parse a string like "255 191 191" into a Color.
     *
     * @param colorStr the string representation of the color (RGB)
     * @return a new Color object based on the parsed values
     */
    private static Color parseColor(String colorStr) {
        String[] parts = colorStr.trim().split("\\s+");
        return new Color(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }

    /**
     * Helper method to parse a string like "0 0 -100" into a Point.
     *
     * @param pointStr the string representation of the point (x y z)
     * @return a new Point object based on the parsed values
     */
    private static Point parsePoint(String pointStr) {
        String[] parts = pointStr.trim().split("\\s+");
        return new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }
}