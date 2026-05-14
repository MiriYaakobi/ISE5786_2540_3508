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
 * Utility class to parse a Scene from an XML file.
 *
 * @author Miri and Yael
 */
public class SceneXmlParser {
    /**
     * Default constructor
     */
    public SceneXmlParser() {
    }

    /**
     * Parses an XML file and creates a Scene object.
     *
     * @param sceneName the name of the scene
     * @param filePath  path to the XML file
     * @return a constructed Scene
     */
    public static Scene parse(String sceneName, String filePath) {
        Scene scene = new Scene(sceneName);
        try {
            File xmlFile = new File(filePath);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            // Set background
            scene.setBackground(parseColor(root.getAttribute("background-color")));

            // Set ambient light
            NodeList ambientList = root.getElementsByTagName("ambient-light");
            if (ambientList.getLength() > 0) {
                Element ambientElement = (Element) ambientList.item(0);
                scene.setAmbientLight(new AmbientLight(parseColor(ambientElement.getAttribute("color"))));
            }

            // Add geometries (Spheres and Triangles)
            NodeList geometriesList = root.getElementsByTagName("geometries");
            if (geometriesList.getLength() > 0) {
                Element geoElem = (Element) geometriesList.item(0);

                NodeList spheres = geoElem.getElementsByTagName("sphere");
                for (int i = 0; i < spheres.getLength(); i++) {
                    Element s = (Element) spheres.item(i);
                    scene.geometries.add(new Sphere(parsePoint(s.getAttribute("center")), Double.parseDouble(s.getAttribute("radius"))));
                }

                NodeList triangles = geoElem.getElementsByTagName("triangle");
                for (int i = 0; i < triangles.getLength(); i++) {
                    Element t = (Element) triangles.item(i);
                    scene.geometries.add(new Triangle(parsePoint(t.getAttribute("p0")), parsePoint(t.getAttribute("p1")), parsePoint(t.getAttribute("p2"))));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("XML parsing failed", e);
        }
        return scene;
    }

    /**
     * Parses a color string into a Color object.
     *
     * @param s the color string (three space-separated double values)
     * @return the parsed Color
     */
    private static Color parseColor(String s) {
        String[] p = s.trim().split("\\s+");
        return new Color(Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]));
    }

    /**
     * Parses a point string into a Point object.
     *
     * @param s the point string (three space-separated double values)
     * @return the parsed Point
     */
    private static Point parsePoint(String s) {
        String[] p = s.trim().split("\\s+");
        return new Point(Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]));
    }
}