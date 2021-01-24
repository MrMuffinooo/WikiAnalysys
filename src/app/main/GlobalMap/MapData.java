package app.main.GlobalMap;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapData {
    static final String path = "resources" + File.separator + "data.csv";
    private List<MapRecord> data;

    public MapData() {
        try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(path)))){
            reader.skip(1);
            data = reader.readAll().stream().map(strings -> new MapRecord(strings[3], strings[1], strings[2]))
                    .collect(Collectors.toList());
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private void setNumbers(Map<String, Integer> records){
        data.forEach(x -> x.setNumberOfViews(records.get(x.getLanguageDomain())));
    }

    private void setColors(){
        ColorPallete pallete = new ColorPallete(data.stream().map(MapRecord::getNumberOfViews).collect(Collectors.toList()));
        List<String> rgbCodes = pallete.getRGBcodes();
        for (int i = 0; i < rgbCodes.size(); i++){
            String color =  rgbCodes.get(i);
            data.get(i).setRGBColor(color);
        }
    }

    public void prepareData(Map<String, Integer> records){
        setNumbers(records);
        setColors();
    }

    public List<MapRecord> getData() {
        return data;
    }
}

class MapRecord {
    private String id;
    private String countryName;
    private String languageDomain;
    private Integer numberOfViews;
    private String rgbColor;

    public MapRecord(String id, String countryName, String languageDomain) {
        this.id = id;
        this.countryName = countryName;
        this.languageDomain = languageDomain;
    }

    public void setNumberOfViews(Integer numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public void setRGBColor(String rgbColor) {
        this.rgbColor = rgbColor;
    }

    public String getRGBColor() {
        return rgbColor;
    }

    public String getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getLanguageDomain() { return languageDomain; }

    public Integer getNumberOfViews() {
        return numberOfViews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapRecord mapRecord = (MapRecord) o;
        return Objects.equals(id, mapRecord.id) && Objects.equals(countryName, mapRecord.countryName) && Objects.equals(numberOfViews, mapRecord.numberOfViews) && Objects.equals(rgbColor, mapRecord.rgbColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, countryName, numberOfViews, rgbColor);
    }
}