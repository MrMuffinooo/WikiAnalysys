package app.main.map;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;

import java.io.IOException;
import java.util.List;

/**
 * Class that generates a world map
 * with series of data on it.
 */
public class SVGMap {
    private static final String path = "world.svg";
    private SVGDocument svgDocument;
    private JSVGCanvas jsvgCanvas = new JSVGCanvas();
    private UserAgent userAgent;
    private BridgeContext ctx;
    private GVTBuilder builder;
    private GraphicsNode rootGN;
    private DocumentLoader loader;
    private List<MapRecord> records;


    public SVGMap() {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        try {
            svgDocument = (SVGDocument) f.createDocument(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userAgent = new UserAgentAdapter();
        loader = new DocumentLoader(userAgent);
        ctx = new BridgeContext(userAgent, loader);
        ctx.setDynamicState(BridgeContext.DYNAMIC);
        builder = new GVTBuilder();
        rootGN = builder.build(ctx, svgDocument);
        records = new MapData(new MapRecord("PL", "Polska", 350),
                new MapRecord("GB", "Great Britain", 500),
                new MapRecord("US", "United States", 1000),
                new MapRecord("DK", "Denmark", 200),
                new MapRecord("RU", "Russia", 750),
                new MapRecord("JP", "Japan", 800)).getData();
    }

    private void colorCountry(MapRecord country){
        Element element = svgDocument.getElementById(country.getId());
        if (element.hasAttribute("fill"))
            element.getAttributeNode("fill").setValue(country.getRGBColor());
        else
            element.setAttribute("fill", country.getRGBColor());
    }

    private void addLabels(MapRecord country){
        Element root = svgDocument.getRootElement();
        SVGElement element = (SVGElement) svgDocument.getElementById(country.getId());
        SVGLocatable locatable = (SVGLocatable) element;
        SVGRect rect = locatable.getBBox(); //znalezienie boxa sciezki

        Element text = svgDocument.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI,"text");
        text.setAttribute("fill", "red");
        text.setAttribute("font-size", "5px");
        text.setAttribute("x", String.valueOf(rect.getX() +rect.getWidth()/2));
        text.setAttribute("y", String.valueOf(rect.getY() + rect.getHeight()/2));
        text.setTextContent(country.getNumber_of_exposures().toString());
        root.appendChild(text);
    }

    private void prepareMap(){
        for (MapRecord record : records){
            colorCountry(record);
            addLabels(record);
        }
    }

    /**
     * @return object, which can be displayed using Swing.
     */
    public JSVGCanvas getSvgCanvas() {
        jsvgCanvas.setDocumentState(JSVGCanvas.ALWAYS_INTERACTIVE);
        prepareMap();
        jsvgCanvas.setSVGDocument(svgDocument);
        jsvgCanvas.setEnableRotateInteractor(false);
        return jsvgCanvas;
    }
}