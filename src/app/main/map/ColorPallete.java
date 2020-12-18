package app.main.map;

import java.util.Collections;
import java.util.List;

public class ColorPallete {
    List<Double> pallete;

    public ColorPallete(List<Double> pallete) {
        this.pallete = pallete;
    }

    private void normalize() {
        Double max = Collections.max(pallete);
        Double min = Collections.min(pallete);
        pallete.forEach(x -> x = ((x - min) / (max - min)));
        //paleta ma wartości z zakresu 0,1
    }

    public List<Double> getPallete() {
        return pallete;
    }

    public List<String> getRGBcodes(){

    }

}
