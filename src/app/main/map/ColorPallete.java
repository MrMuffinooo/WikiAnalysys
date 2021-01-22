package app.main.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColorPallete {
    private final List<Integer> values;
    private List<Double> pallete;

    public ColorPallete(List<Integer> values) { this.values = values;
    this.pallete = normalize();}

    private List<Double> normalize() {
        Double max = Collections.max(values).doubleValue();
        Double min = Collections.min(values).doubleValue();
        return values.stream().map(Integer::doubleValue).map(x -> (x - min) / (max - min)).collect(Collectors.toList());
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
