package app.main.map;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import java.io.IOException;

/**
 * Class that generates a world map
 * with series of data on it.
 */
public class SVGMap {
    private static final String path = "world.svg";
    SVGDocument svgDocument;
    JSVGCanvas jsvgCanvas = new JSVGCanvas();


    public SVGMap() {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        try {
            svgDocument = (SVGDocument) f.createDocument(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void colorMap(String country, String color){
        NodeList nodeList = svgDocument.getElementsByTagName("path");
        for (int i = 0; i < nodeList.getLength(); i++){
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equalsIgnoreCase(country))
                if (element.hasAttribute("fill")) element.getAttributeNode("fill").setValue(color);
                else element.setAttribute("fill", color); //tworzenie atrybutu
        }
    }


    /**
     * @return object, which can be displayed using Swing.
     */
    public JSVGCanvas getSvgCanvas() {
        colorMap("PL", "blue");
        colorMap("GB", "yellow");
        colorMap("DK", "green");
        colorMap("PL", "red");
        colorMap("US", "purple");
        colorMap("CA", "#E6E6FA");
        colorMap("AE", "rgb(0, 0, 128)");
        jsvgCanvas.setSVGDocument(svgDocument);
        jsvgCanvas.setDocumentState(JSVGCanvas.ALWAYS_INTERACTIVE);
        jsvgCanvas.setEnableRotateInteractor(false);
        return jsvgCanvas;
    }
}

