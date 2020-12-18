package DataImport;

import org.codehaus.jackson.map.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class DataImporter {

    public DataImporter() {}

    public enum Domain {
        en,
        pl
    }

    public Map importTop(Domain domain, String date) throws Exception {

        Pattern dateFormat = Pattern.compile("[0-9]{4}\\/[0-9]{2}\\/[0-9]{2}");
        if (!dateFormat.matcher(date).matches())
            throw new Exception("Wrong date format. Follow: \"RRRR/MM/DD\". DD=00 - all days");

        date = date.replaceAll("00$", "all-days");

        String jsonStr = importData("https://wikimedia.org/api/rest_v1/metrics/pageviews/top/" + domain +
                ".wikipedia/all-access/" + date);
        Map<String, Integer> articles = new LinkedHashMap<>();
        try {
            Map<String,Object> result = new ObjectMapper().readValue(jsonStr, HashMap.class);
            ArrayList items = (ArrayList) result.get("items");
            Map deeper = (Map) items.get(0);
            ArrayList articles_raw = (ArrayList) deeper.get("articles");
            for (Object x : articles_raw) {
                LinkedHashMap element = (LinkedHashMap) x;
                articles.put((String) element.get("article"), (Integer) element.get("views"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    public Map importNames(Domain domain, String article) {
        String jsonStr = importData("https://www.wikidata.org/w/api.php?action=wbgetentities&sites=" + domain +
                "wiki&titles=" + article + "&languages=en&format=json");
        Map<String, String> names = new LinkedHashMap<>();
        try {
            Map<String,Object> result = new ObjectMapper().readValue(jsonStr, HashMap.class);
            Map deeper = (Map) result.get("entities");
            Map.Entry deep = (Map.Entry) deeper.entrySet().iterator().next();
            deeper = (Map) deep.getValue();
            deeper = (Map) deeper.get("sitelinks");
            for (Object x : deeper.entrySet()) {
                Map.Entry element = (Map.Entry) x;
                String key = (String)element.getKey();
                if (key.endsWith("wiki")) {
                    key = key.replaceAll("wiki$", "");
                    Map value = (Map) element.getValue();
                    names.put(key, (String) value.get("title"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    public Integer importViews(Domain domain, String article, String date) {
        String jsonStr = importData("https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/" + domain +
                ".wikipedia.org/all-access/all-agents/" + article + "/daily/" + date + "/" + date);
        Integer views = null;
        try {
            Map<String,Object> result = new ObjectMapper().readValue(jsonStr, HashMap.class);
            ArrayList items = (ArrayList) result.get("items");
            Map deeper = (Map) items.get(0);
            views = (int) deeper.get("views");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return views;
    }

    public String importData(String url) {
        return readData(getConnection(url));
    }

    public String getLink(Domain domain, String articleName) {
        return "https://" + domain + ".wikipedia.org/wiki/" + articleName;
    }

    private static HttpURLConnection getConnection ( String line ){
        URL url = null ;
        try {
            url = new URL ( line );
            } catch ( MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null ;
        try {
            connection = ( HttpURLConnection ) url . openConnection();
            } catch ( Exception e) {
            e. printStackTrace ();
            }
        return connection ;
        }

    private static String readData ( HttpURLConnection connection ){
        InputStream inStream = null ;
        try {
            inStream = connection . getInputStream();
            } catch ( Exception e) {
            e. printStackTrace ();
            }
        Scanner in = new Scanner( inStream , "UTF-8");
        StringBuilder sb = new StringBuilder ("");
        while (in.hasNext ()) {
            String line = in.next();
            sb.append( line );
            sb.append("_");
            }
        return sb.toString();
        }

    public static void main(String[] args) {

        //TESTY:

        DataImporter test = new DataImporter();
        /*Map articles = null;
        try {
            articles = test.importTop(Domain.en, "2020/11/05");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(articles);
        for (Object x : articles.entrySet()) {
            Map.Entry y = (Map.Entry) x;
            System.out.println(test.getLink(Domain.en, (String) y.getKey()));
        }*/

        /*Map names = test.importNames(Domain.en, "Main_Page");
        System.out.println(names);*/

        /*int views = test.importViews(Domain.pl, "Gwiezdne_wojny", "20201212");
        System.out.println(views);*/
    }

}
