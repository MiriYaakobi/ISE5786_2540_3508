package scene;

import java.io.File;

import geometries.api.Geometry;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Point;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Utility class to parse a Scene from an XML file.
 * Updated to support Material properties (kD, kS, shininess, kR, kT).
 *
 * @author Miri and Yael
 */
public class SceneXmlParser {
    /**
     * Default constructor for SceneXmlParser.
     */
    public SceneXmlParser() {
    }

    /**
     * Parses the XML file and returns a Scene object.
     *
     * @param sceneName the name of the scene
     * @param filePath  the path to the XML file
     * @return a new Scene object populated with data from the XML
     */
    public static Scene parse(String sceneName, String filePath) {
        Scene scene = new Scene(sceneName);
        try {
            File xmlFile = new File(filePath);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            scene.setBackground(parseColor(root.getAttribute("background-color")));

            NodeList ambientList = root.getElementsByTagName("ambient-light");
            if (ambientList.getLength() > 0) {
                Element ambientElement = (Element) ambientList.item(0);
                scene.setAmbientLight(new AmbientLight(parseColor(ambientElement.getAttribute("color"))));
            }

            NodeList geometriesList = root.getElementsByTagName("geometries");
            if (geometriesList.getLength() > 0) {
                Element geoElem = (Element) geometriesList.item(0);

                NodeList spheres = geoElem.getElementsByTagName("sphere");
                for (int i = 0; i < spheres.getLength(); i++) {
                    Element s = (Element) spheres.item(i);
                    Sphere sphere = new Sphere(parsePoint(s.getAttribute("center")), Double.parseDouble(s.getAttribute("radius")));
                    applyMaterial(sphere, s);
                    scene.geometries.add(sphere);
                }

                NodeList triangles = geoElem.getElementsByTagName("triangle");
                for (int i = 0; i < triangles.getLength(); i++) {
                    Element t = (Element) triangles.item(i);
                    Triangle triangle = new Triangle(parsePoint(t.getAttribute("p0")), parsePoint(t.getAttribute("p1")), parsePoint(t.getAttribute("p2")));
                    applyMaterial(triangle, t);
                    scene.geometries.add(triangle);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("XML parsing failed", e);
        }
        return scene;
    }

    /**
     * Helper to apply material properties from XML element to geometry.
     *
     * @param geo  the geometry to apply the material to
     * @param elem the XML element containing material data
     */
    private static void applyMaterial(Geometry geo, Element elem) {
        NodeList matList = elem.getElementsByTagName("material");
        if (matList.getLength() > 0) {
            Element matElem = (Element) matList.item(0);
            Material mat = new Material();
            if (matElem.hasAttribute("kD")) mat.setKD(Double.parseDouble(matElem.getAttribute("kD")));
            if (matElem.hasAttribute("kS")) mat.setKS(Double.parseDouble(matElem.getAttribute("kS")));
            if (matElem.hasAttribute("shininess"))
                mat.setShininess(Integer.parseInt(matElem.getAttribute("shininess")));
            if (matElem.hasAttribute("kR")) mat.setKR(new Double3(Double.parseDouble(matElem.getAttribute("kR"))));
            if (matElem.hasAttribute("kT")) mat.setKT(new Double3(Double.parseDouble(matElem.getAttribute("kT"))));
            geo.setMaterial(mat);
        }
    }

    /**
     * Parses a color from a string representation of three RGB values.
     *
     * @param s the string containing RGB values separated by spaces
     * @return a new Color object
     */
    private static Color parseColor(String s) {
        String[] p = s.trim().split("\\s+");
        return new Color(Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]));
    }

    /**
     * Parses a point from a string representation of three coordinate values.
     *
     * @param s the string containing x, y, z coordinates separated by spaces
     * @return a new Point object
     */
    private static Point parsePoint(String s) {
        String[] p = s.trim().split("\\s+");
        return new Point(Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]));
    }
}