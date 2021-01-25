package app.main.GlobalMap;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGStylable;

import java.io.*;
import java.util.Map;

/**
 * Class that generates a world map
 * with series of data on it.
 */
public class SVGMap {
    private static final String path = "resources" + File.separator + "world.svg";
    private SVGDocument svgDocument;
    private JSVGCanvas jsvgCanvas = new JSVGCanvas();
    private UserAgent userAgent;
    private BridgeContext ctx;
    private GVTBuilder builder;
    private GraphicsNode rootGN;
    private DocumentLoader loader;
    private MapData records;


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
    }

    private void addLabels(MapRecord country){
        Element element = svgDocument.getElementById(country.getId()+ "T");
        if (country.getNumberOfViews() != null)
            element.setTextContent(country.getNumberOfViews().toString());
    }

    /**
     * Function which colorize map and add labels depend on given data.
     */
    private void prepareMap(){
        for (MapRecord record : records.getData()){
            addLabels(record);
            colorCountry(record);
        }
    }

    /**
     * function used to save map to file
     * @param filename - name of file to save
     */
    private void saveMap(String filename){
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename+".svg"), "UTF-8")){
            SVGGraphics2D graphics2D = new SVGGraphics2D(svgDocument);
            graphics2D.stream(svgDocument.getDocumentElement(),out, false, true);
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
        prepareMap();
        jsvgCanvas.setDocumentState(JSVGCanvas.ALWAYS_INTERACTIVE);
        jsvgCanvas.setSVGDocument(svgDocument);
        jsvgCanvas.setEnableRotateInteractor(false);
        return jsvgCanvas;
    }
}