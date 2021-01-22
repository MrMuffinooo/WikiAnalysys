package DataImport;

import org.codehaus.jackson.map.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class DataImporter {

    public DataImporter() {}

    public enum Domain {
        aa, ab, ace, ady, af, ak, als, am, ang, an, arc, ar, ary, arz, ast, as, atj, avk, av, awa, ay, azb, az, ban, bar,
        bat_smg, ba, bcl, be, bg, bh, bi, bjn, bm, bn, bo, bpy, br, bs, bug, bxr, ca, cbk_zam, cdo, ceb, ce, cho,
        chr, ch, chy, ckb, co, commons, crh, cr, csb, cs, cu, cv, cy, da, de, din, diq, dsb, dty, dv, dz, ee, el, eml, en,
        eo, es, et, eu, ext, fa, ff, fiu_vro, fi, fj, fo, frp, frr, fr, fur, fy, gag, gan, ga, gcr, gd, glk, gl, gn, gom,
        gor, got, gu, gv, hak, ha, haw, he, hif, hi, ho, hr, hsb, ht, hu, hy, hyw, hz, ia, id, ie, ig, ik, ilo, incubator,
        inh, io, is, it, iu, jam, ja, jbo, jv, kaa, kab, ka, kbd, kbp, kg, ki, kj, kk, kl, km, kn, koi, ko, krc, kr, ksh,
        ks, ku, kv, kw, ky, lad, la, lbe, lb, lez, lfn, lg, lij, li, lld, lmo, ln, lo, lrc, ltg, lt, lv, mad, mai, map_bms,
        mdf, meta, mg, mhr, min, mi, mk, ml, mn, mnw, mrj, mr, ms, mt, mus, mwl, myv, my, mzn, nah, nap, na,
        nds_nl, nds, ne, ng, nl, nn, nov, no, nqo, nrm, nso, nv, ny, oc, olo, om, or, os, pag, pam, pap, pa, pcd, pdc,
        pfl, pih, pi, pl, pms, pnb, pnt, ps, pt, qu, rm, rmy, rn, roa_rup, roa_tara, ro, rue, ru, rw, sah, sat, sa, scn,
        sco, sc, sd, se, sg, shn, sh, simple, si, skr, sk, sl, smn, sm, sn, sources, so, species, sq, srn, sr, ss, stq,
        st, su, sv, sw, szl, szy, ta, tcy, tet, te, tg, th, ti, tk, tl, tn, to, tpi, tr, ts, tt, tum, tw, tyv, ty, udm,
        ug, uk, ur, uz, vec, vep, ve, vi, vls, vo, war, wa, wo, wuu, xal, xh, xmf, yi, yo, za, zea, zh, zu
    }

    //Zwraca mapę najpopularniejszych artykulow.
    public Map importTop(Domain domain, LocalDate date, Boolean monthly) throws Exception { //domena, data dla topki i flaga czy topka miesieczna

        String dateStr = "";
        if (monthly)
            dateStr = date.format(DateTimeFormatter.ofPattern("yyyy/MM/")) + "all-days";
        else
            dateStr = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String jsonStr = importData("https://wikimedia.org/api/rest_v1/metrics/pageviews/top/" + domain +
                ".wikipedia/all-access/" + dateStr);
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
            e.printStackTrace(); }
        return articles; //Mapa topki, przyporzadkowuje artykulom wyswietlenia.
    }

    //overloading
    public Map importTop(Domain domain, LocalDate date) throws Exception {
        return this.importTop(domain, date, false);
    }

    //Zwraca nazwy artukulow we wszystkich domenach.
    public Map importNames(Domain domain, String article) { //domena i nazwa artykulu w tej domenie
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
                if (key.endsWith("wiki") && !key.contains("_") && !key.contains("commons") && !key.contains("new") && !key.contains("skr")) { // skr nie dziala (bo arabskie?)
                    key = key.replaceAll("wiki$", "");
                    Map value = (Map) element.getValue();
                    names.put(key, (String) value.get("title"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names; //Mapa przyporzadkowuje domenom nazwy artykulu.
    }

    //Zwraca wyswietlenia pojedynczego artykulu.
    public Integer importViews(Domain domain, String article, LocalDate date) { //domena, nazwa artykulu w domenie i data wyswietlen
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String jsonStr = importData("https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/" + domain +
                ".wikipedia.org/all-access/all-agents/" + article + "/daily/" + dateStr + "/" + dateStr);
        Integer views = null;
        try {
            Map<String,Object> result = new ObjectMapper().readValue(jsonStr, HashMap.class);
            ArrayList items = (ArrayList) result.get("items");
            Map deeper = (Map) items.get(0);
            views = (int) deeper.get("views");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return views; //wyswietlenia artykulu
    }

    //Zwraca wyswietlenia artykulu we wszystkich domenach.
    public Map importViewsByDomain(Domain domain, String article, LocalDate date) { //domena, nazwa artykulu w domenie i data wyswietlen
        Map names = importNames(domain, article);
        Map<String, Integer> viewsByDomain = new LinkedHashMap<>();
        for (Object x : names.entrySet()) {
            Map.Entry y = (Map.Entry) x;
            viewsByDomain.put((String) y.getKey(), importViews(Domain.valueOf((String) y.getKey()), (String) y.getValue(), date));
        }
        return viewsByDomain; //Mapa przyporzadkowuje domenom wyswietlenia artykulu.
    }

    public String importData(String url) {
        return readData(getConnection(url));
    }

    //Zwraca link do danego artykulu.
    public String getLink(Domain domain, String articleName) { //domena, nazwa artykulu w domenie
        return "https://" + domain + ".wikipedia.org/wiki/" + articleName;
    }

    private static HttpURLConnection getConnection ( String line ){
        URL url = null;
        try {
            url = new URL ( line );
            } catch ( MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection connection = null;
        try {
            connection = ( HttpURLConnection ) url.openConnection();
            } catch ( Exception e) {
            e.printStackTrace ();
            return null;
            }
        return connection ;
        }

    private static String readData ( HttpURLConnection connection ){
        InputStream inStream = null ;
        try {
            inStream = connection.getInputStream();
            } catch ( Exception e) {
            e.printStackTrace();
            return null;
            }
        Scanner in = new Scanner( inStream, "UTF-8");
        StringBuilder sb = new StringBuilder("");
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
            articles = test.importTop(Domain.en, LocalDate.of(2020, 11, 5), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(articles);
        for (Object x : articles.entrySet()) {
            Map.Entry y = (Map.Entry) x;
            System.out.println(test.getLink(Domain.en, (String) y.getKey()));
        }*/

        /*Map names = test.importNames(Domain.en, "Star_Wars");
        System.out.println(names);*/

        /*Map names = test.importViewsByDomain(Domain.en, "Star_Wars", LocalDate.of(2020,12,12));
        for (Object x : names.entrySet()) {
            Map.Entry y = (Map.Entry) x;
            System.out.println(y.getKey() + " : " + y.getValue());
        }*/

        /*int views = test.importViews(Domain.pl, "Gwiezdne_wojny", LocalDate.of(2020, 12, 12));
        System.out.println(views);*/

        Integer x = test.importViews(Domain.pl, "hrth", LocalDate.of(2020,12,12));
        System.out.print(x);
    }

}
