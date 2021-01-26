package app.main.DataImport;

import org.apache.commons.lang3.EnumUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class DataImporter {

    public DataImporter() {}

    /*public enum Domain { //old
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
    }*/
    public enum Domain {
        ar,	ary,	az,	be,	bg,	bn,	bs,	ca,	cs,	da,	de,	dv,	dz,	el,	en,	es,	et,	fa,	fi,	fj,	fr,	gn,	he,	hi,	hr,	ht,	hu,
        hy,	id,	is,	it,	ja,	ka,	kk,	km,	ko,	ky,	lb,	lo,	lt,	lv,	mg,	mk,	mn,	ms,	mt,	my,	na,	ne,	nl,	no,	ny,	om,	pl,	ps,
        pt,	rn,	ro,	ru,	rw,	si,	sk,	sl,	sn,	so,	sq,	sr,	ss,	st,	sv,	sw,	tg,	th,	ti,	tk,	tl,	tn,	tpi,	tr,	uk,	ur,	uz,
        vi,	zh,	zu
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
        if (jsonStr == null) {
            throw new Exception("Niepoprawny link dla danych wejsciowych.");
        }
        Map<String, Integer> articles = new LinkedHashMap<>();
        Map<String,Object> result = new ObjectMapper().readValue(jsonStr, HashMap.class);
        ArrayList items = (ArrayList) result.get("items");
        Map deeper = (Map) items.get(0);
        ArrayList articles_raw = (ArrayList) deeper.get("articles");
        for (Object x : articles_raw) {
            LinkedHashMap element = (LinkedHashMap) x;
            articles.put((String) element.get("article"), (Integer) element.get("views"));
        }
        return articles; //Mapa topki, przyporzadkowuje artykulom wyswietlenia.
    }

    //overloading
    public Map importTop(Domain domain, LocalDate date) throws Exception {
        return this.importTop(domain, date, false);
    }

    //Zwraca nazwy artukulow we wszystkich domenach.
    public Map importNames(Domain domain, String article) throws Exception { //domena i nazwa artykulu w tej domenie
        String jsonStr = importData("https://www.wikidata.org/w/api.php?action=wbgetentities&sites=" + domain +
                "wiki&titles=" + URLEncoder.encode(article, StandardCharsets.UTF_8) + "&languages=en&format=json");
        if (jsonStr == null) {
            throw new Exception("Niepoprawny link dla danych wejsciowych.");
        }
        Map<String, String> names = new LinkedHashMap<>();
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
                if (EnumUtils.isValidEnum(Domain.class, key)) {
                    Map value = (Map) element.getValue();
                    names.put(key, (String) value.get("title"));
                }
            }
        }
        return names; //Mapa przyporzadkowuje domenom nazwy artykulu.
    }

    //Zwraca wyswietlenia pojedynczego artykulu dla zakresu dat.
    public List<Integer> importViews(Domain domain, String article, LocalDate date, LocalDate date2) throws Exception { //domena, nazwa artykulu w domenie i zakres dat wyswietlen
        if (date.isAfter(date2))
            throw new Exception("Niepoprawny zakres dat.");
        long days = DAYS.between(date, date2);
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String dateStr2 = date2.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String jsonStr = importData("https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/" + domain +
                ".wikipedia.org/all-access/all-agents/" + URLEncoder.encode(article, StandardCharsets.UTF_8) + "/daily/" + dateStr + "/" + dateStr2);
        if (jsonStr == null) {
            return new ArrayList(Collections.nCopies((int)days, 0));
        }
        List<Integer> views = new ArrayList<>();
        Map<String,Object> result = new ObjectMapper().readValue(jsonStr, HashMap.class);
        ArrayList items = (ArrayList) result.get("items");
        for (int i = 0; i < items.size(); i++) {
            Map deeper = (Map) items.get(i);
            String time = (String) deeper.get("timestamp");
            while (!time.equals(date.format(DateTimeFormatter.ofPattern("yyyyMMdd00")))) {
                date = date.plusDays(1);
                views.add(0);
            }
            views.add((int) deeper.get("views"));
            date = date.plusDays(1);
        }
        return views; //lista wyswietlen artykulu
    }
    //Dla jednego dnia tylko:
    public List<Integer> importViews(Domain domain, String article, LocalDate date) throws Exception {
        return this.importViews(domain, article, date, date);
    }

    //Zwraca wyswietlenia artykulu we wszystkich domenach, w zakresie dat.
    public Map importViewsByDomainDays(Domain domain, String article, LocalDate date, LocalDate date2) throws Exception { //domena, nazwa artykulu w domenie i zakres dat wyswietlen
        if (date.isAfter(date2))
            throw new Exception("Niepoprawny zakres dat.");
        Map names = importNames(domain, article);
        Map<String, List> viewsByDomain = new LinkedHashMap<>();
        for (Object x : names.entrySet()) {
            Map.Entry y = (Map.Entry) x;
            viewsByDomain.put((String) y.getKey(), importViews(Domain.valueOf((String) y.getKey()), (String) y.getValue(), date, date2));
        }
        return viewsByDomain; //Mapa przyporzadkowuje domenom listy wyswietlen artykulu.
    }

    //Zwraca sumaryczne wyswietlenia artykulu we wszystkich domenach, w zakresie dat.
    public Map importViewsByDomain(Domain domain, String article, LocalDate date, LocalDate date2) throws Exception { //domena, nazwa artykulu w domenie i zakres dat wyswietlen
        if (date.isAfter(date2))
            throw new Exception("Niepoprawny zakres dat.");
        Map viewsByDomain = this.importViewsByDomainDays(domain, article, date, date2);
        Map<String, Integer> viewsByDomainSum = new LinkedHashMap<>();
        for (Object x : viewsByDomain.entrySet()) {
            Map.Entry y = (Map.Entry) x;
            List<Integer> value = (List) y.getValue();
            viewsByDomainSum.put((String) y.getKey(), value.stream().mapToInt(Integer::intValue).sum());
        }
        return viewsByDomainSum; //Mapa przyporzadkowuje domenom sumaryczne wyswietlena artykulu.
    }

    //Dla jednego dnia tylko:
    public Map importViewsByDomain(Domain domain, String article, LocalDate date) throws Exception {
        return this.importViewsByDomain(domain, article, date, date);
    }

    public String importData(String url) {
        return readData(getConnection(url));
    } //zwraca null dla blednego url

    //Zwraca link do danego artykulu.
    public String getLink(Domain domain, String articleName) { //domena, nazwa artykulu w domenie
        return "https://" + domain + ".wikipedia.org/wiki/" + articleName;
    }

    private static HttpURLConnection getConnection ( String line ){
        URL url = null;
        try {
            url = new URL ( line );
            } catch ( MalformedURLException e) {
            //e.printStackTrace();
            return null;
        }
        HttpURLConnection connection = null;
        try {
            connection = ( HttpURLConnection ) url.openConnection();
            } catch ( Exception e) {
            //e.printStackTrace();
            return null;
            }
        return connection ;
        }

    private static String readData ( HttpURLConnection connection ){
        if (connection == null) return null;
        InputStream inStream = null;
        try {
            inStream = connection.getInputStream();
            } catch ( Exception e) {
            //e.printStackTrace();
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

        /*try {
            Map views = test.importViewsByDomain(Domain.pl, "Polska", LocalDate.of(2020, 12, 12), LocalDate.of(2020, 12, 20));
            //Map names = test.importNames(Domain.en, "Star_Wars");
            for (Object x : views.entrySet()) {
                Map.Entry y = (Map.Entry) x;
                System.out.println(y.getKey() + " : " + y.getValue());
            }
            Map views2 = test.importViewsByDomainDays(Domain.pl, "Polska", LocalDate.of(2020, 12, 12), LocalDate.of(2020, 12, 20));
            for (Object x : views2.entrySet()) {
                Map.Entry y = (Map.Entry) x;
                System.out.println(y.getKey() + " : " + y.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*for (Object x : names.entrySet()) {
            Map.Entry y = (Map.Entry) x;
            System.out.println(test.getLink(Domain.valueOf((String)y.getKey()), (String) y.getValue()));
        }*/

        /*int views = test.importViews(Domain.pl, "Gwiezdne_wojny", LocalDate.of(2020, 12, 12));
        System.out.println(views);*/

        /*List x = test.importViews(Domain.en, "Main_Page", LocalDate.of(2020,12,12), LocalDate.of(2020, 12, 15));
        System.out.println(x);*/
        System.out.println(test.importData("https://www.wikidata.org/w/api.php?action=wbgetentities&sites=plwiki&titles=" +
                URLEncoder.encode("Świdnica", StandardCharsets.UTF_8) + "&languages=en&format=json"));
    }

}
