package app.main.map;

import java.util.*;
import java.util.stream.Collectors;

public class MapData {
    List<MapRecord> data;

    public MapData(MapRecord... data) {
        this.data = Arrays.asList(data);
        prepareColors();
    }

    private void prepareColors(){
        ColorPallete pallete = new ColorPallete(data.stream().map(MapRecord::getNumberOfExposures).collect(Collectors.toList()));
        List<String> rgbCodes = pallete.getRGBcodes();
        for (int i = 0; i < rgbCodes.size(); i++){
            String color =  rgbCodes.get(i);
            data.get(i).setRGBColor(color);
        }
    }

    public List<MapRecord> getData() {
        return data;
    }
}

class MapRecord {
    private String id;
    private String countryName;
    private Integer numberOfExposures;
    private String rgbColor;

    public MapRecord(String id, String countryName, Integer numberOfExposures) {
        this.id = id;
        this.countryName = countryName;
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