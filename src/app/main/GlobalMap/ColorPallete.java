package app.main.GlobalMap;

import java.util.*;
import java.util.stream.Collectors;

public class ColorPallete {
    private final List<Integer> values;
    private List<Double> pallete;

    public ColorPallete(List<Integer> values) {
        this.values = values;
        this.pallete = normalize();}

    private List<Double> normalize() {
        Double max = values.stream().filter(Objects::nonNull).mapToDouble(v -> v).max().orElse(-1);
        Double min = values.stream().filter(Objects::nonNull).mapToDouble(v -> v).min().orElse(-1);
        return values.stream().filter(Objects::nonNull).map(Integer::doubleValue).map(x -> (x - min) / (max - min)).collect(Collectors.toList());
        //paleta ma wartości z zakresu 0,1
    }

    public List<String> getRGBcodes(){
        List<String> rgbCodes = new ArrayList<>();
        normalize();
        for (Double color : pallete){
            int r = (int) (color*255);
            int g = (int) ((1-color)*255);
            rgbCodes.add("rgb(+" + r +"," + g + ",0)");
        }
        return rgbCodes;
    }
}
