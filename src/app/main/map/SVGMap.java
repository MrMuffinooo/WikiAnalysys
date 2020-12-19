package app.main.map;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void colorMap(List<String> country, List<String> color){
        NodeList nodeList = svgDocument.getElementsByTagName("path");
        for (int i = 0; i < nodeList.getLength(); i++){
            Element element = (Element) nodeList.item(i);
            if (country.contains(element.getAttribute("id"))){
                int id = country.indexOf(element.getAttribute("id"));
                if (element.hasAttribute("fill")) element.getAttributeNode("fill").setValue(color.get(id));
                else element.setAttribute("fill", color.get(id));
            }//tworzenie atrybutu
        }
    }

    /**
     * @return object, which can be displayed using Swing.
     */
    public JSVGCanvas getSvgCanvas() {
        ArrayList<String> countries = new ArrayList<>();
        countries.add("PL");
        countries.add("GB");
        countries.add("DK");
        countries.add("US");
        countries.add("RU");
        ArrayList<Integer> values = new ArrayList<>();
        values.add(100);
        values.add(200);
        values.add(300);
        values.add(400);
        values.add(500);
        colorMap(countries, new ColorPallete(values).getRGBcodes());
        jsvgCanvas.setSVGDocument(svgDocument);
        jsvgCanvas.setDocumentState(JSVGCanvas.ALWAYS_INTERACTIVE);
        jsvgCanvas.setEnableRotateInteractor(false);
        return jsvgCanvas;
    }
}