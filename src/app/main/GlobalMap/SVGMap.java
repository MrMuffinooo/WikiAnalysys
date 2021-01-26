package app.main.GlobalMap;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGStylable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Class that generates a world map
 * with series of data on it.
 */
public class SVGMap {
    private static final String path = "resources" + File.separator + "world.svg";
    private static final String URI = "http://www.w3.org/2000/svg";
    private SVGDocument svgDocument;
    private final JSVGCanvas jsvgCanvas = new JSVGCanvas();
    private final UserAgent userAgent;
    private final BridgeContext ctx;
    private final GVTBuilder builder;
    private final GraphicsNode rootGN;
    private final DocumentLoader loader;
    private final MapData records;


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
        records = new MapData();
    }

    /**
     * Function to change data, which will generate map.
     * @param data map with language_domain as keys and number of views as values
     */
    public void setRecords(Map<String, Integer> data){
        records.prepareData(data);
    }

    private void colorCountry(MapRecord country){
        SVGStylable element = (SVGStylable) svgDocument.getElementById(country.getId());
        element.getStyle().setProperty("fill", country.getRGBColor(),"");
        element.getStyle().setProperty("stroke", "black", "");
    }

    private void addLabels(MapRecord country){
        Element element = svgDocument.getElementById(country.getId()+ "T");
        if (country.getNumberOfViews() != null)
            element.setTextContent(country.getNumberOfViews().toString());
        else
            element.setTextContent("");
    }

    private void addScale(Integer min, Integer max){
        Element root = svgDocument.getRootElement();
        if (svgDocument.getElementById("scale_colors") == null){
            Node defs = svgDocument.createElementNS(URI, "defs");
            Element linearGradient = createlinearGradient();

            defs.appendChild(linearGradient);
            root.appendChild(defs);

            Element scale = createRect();
            root.appendChild(scale);

            Element minText = createText("min","8", "9", min.toString());
            Element maxText = createText("max","190", "9", max.toString());
            root.appendChild(minText);
            root.appendChild(maxText);
        }
        else{
            Element minText = svgDocument.getElementById("min");
            minText.setTextContent(min.toString());
            Element maxText = svgDocument.getElementById("max");
            maxText.setTextContent(max.toString());
        }

    }

    private Element createRect() {
        Node node = svgDocument.createElementNS(URI, "rect");
        Element element = (Element) node;
        element.setAttribute("id", "scale");
        element.setAttribute("x", "10");
        element.setAttribute("y", "10");
        element.setAttribute("height", "10");
        element.setAttribute("width", "180");
        element.setAttribute("fill", "url(#scale_colors)");
        return element;
    }

    private Element createlinearGradient() {
        //stworzenie gradient
        Element linearGradient = svgDocument.createElementNS(URI, "linearGradient");
        linearGradient.setAttribute("id","scale_colors");
        linearGradient.setAttribute("x1", "0%");
        linearGradient.setAttribute("y1", "0%");
        linearGradient.setAttribute("x2", "100%");
        linearGradient.setAttribute("y2", "0%");

        //dodanie stopów
        linearGradient.appendChild(createStop("0%", "rgb(255,255,0)"));
        linearGradient.appendChild(createStop("100%", "rgb(255,0,0)"));
        return linearGradient;
    }

    private Element createStop(String offset, String color){
        Element stop = svgDocument.createElementNS(URI, "stop");
        stop.setAttribute("offset", offset);
        SVGStylable element = (SVGStylable) stop;
        element.getStyle().setProperty("stop-color", color, "");
        element.getStyle().setProperty("stop-opacity", "1", "");
        return stop;
    }

    private Element createText(String id, String x, String y, String value){
        Element text = svgDocument.createElementNS(URI, "text");
        text.setAttribute("id", id);
        text.setAttribute("x", x);
        text.setAttribute("y", y);
        text.setTextContent(value);
        return text;
    }

    private void addCountryName(MapRecord country) {
        Element text = svgDocument.getElementById(country.getId() + "T");
        EventTarget target = (EventTarget) text;
        if (country.getNumberOfViews() != null){
            target.addEventListener("mouseover",
                    evt -> {
                        Element element = (Element) evt.getCurrentTarget();
                        element.setTextContent(country.getCountryName());
                        jsvgCanvas.getCanvasGraphicsNode().fireGraphicsNodeChangeCompleted();
                    },
                    false);
            target.addEventListener("mouseout",
                    evt -> {
                        Element element = (Element) evt.getCurrentTarget();
                        element.setTextContent(country.getNumberOfViews().toString());
                        jsvgCanvas.getCanvasGraphicsNode().fireGraphicsNodeChangeCompleted();
                    },
                    false);
        }
    }



    /**
     * Function which colorize map and add labels depend on given data.
     */
    private void prepareMap(){
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (MapRecord record : records.getData()){
            addCountryName(record);
            addLabels(record);
            colorCountry(record);

            if (record.getNumberOfViews() != null){
                if (min > record.getNumberOfViews())
                min = record.getNumberOfViews();

                if (max < record.getNumberOfViews())
                max = record.getNumberOfViews();
            }
        }

        addScale(min, max);
    }

    /**
     * function used to save map to file
     * @param filename - name of file to save
     */
    private void saveMap(String filename){
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename+".svg"), StandardCharsets.UTF_8)){
            SVGGraphics2D graphics2D = new SVGGraphics2D(svgDocument);
            graphics2D.stream(svgDocument.getDocumentElement(), out, false, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SVGGraphics2DIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return JComponent
     */
    public JSVGCanvas getSvgCanvas() {
        jsvgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        prepareMap();
        jsvgCanvas.setSVGDocument(svgDocument);
        jsvgCanvas.setEnableRotateInteractor(false);
        return jsvgCanvas;
    }
}