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
        ColorPallete pallete = new ColorPallete(data.stream().map(MapRecord::getNumber_of_exposures).collect(Collectors.toList()));
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
    private String country_name;
    private Integer number_of_exposures;
    private String rgbColor;

    public MapRecord(String id, String country_name, Integer number_of_exposures) {
        this.id = id;
        this.country_name = country_name;
        this.number_of_exposures = number_of_exposures;
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

    public String getCountry_name() {
        return country_name;
    }

    public Integer getNumber_of_exposures() {
        return number_of_exposures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapRecord mapRecord = (MapRecord) o;
        return Objects.equals(id, mapRecord.id) && Objects.equals(country_name, mapRecord.country_name) && Objects.equals(number_of_exposures, mapRecord.number_of_exposures) && Objects.equals(rgbColor, mapRecord.rgbColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country_name, number_of_exposures, rgbColor);
    }
}