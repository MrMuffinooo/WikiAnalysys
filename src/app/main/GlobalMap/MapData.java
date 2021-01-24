package app.main.GlobalMap;

import app.main.DataImport.DataImporter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.time.LocalDate;
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
        data.forEach(x -> x.setNumberOfExposures(records.get(x.getLanguageDomain())));
    }

    private void setColors(){
        ColorPallete pallete = new ColorPallete(data.stream().map(MapRecord::getNumberOfExposures).collect(Collectors.toList()));
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

    public static void main(String[] args) {
        MapData x = new MapData();
        x.prepareData(new DataImporter().importViewsByDomain(DataImporter.Domain.en, "Han_Solo", LocalDate.of(2020, 10, 24)));
    }
}

class MapRecord {
    private String id;
    private String countryName;
    private String languageDomain;
    private Integer numberOfExposures;
    private String rgbColor;

    public MapRecord(String id, String countryName, String languageDomain) {
        this.id = id;
        this.countryName = countryName;
        this.languageDomain = languageDomain;
    }

    public void setNumberOfExposures(Integer numberOfExposures) {
        this.numberOfExposures = numberOfExposures;
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

    public Integer getNumberOfExposures() {
        return numberOfExposures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapRecord mapRecord = (MapRecord) o;
        return Objects.equals(id, mapRecord.id) && Objects.equals(countryName, mapRecord.countryName) && Objects.equals(numberOfExposures, mapRecord.numberOfExposures) && Objects.equals(rgbColor, mapRecord.rgbColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, countryName, numberOfExposures, rgbColor);
    }
}