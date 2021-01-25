package app.main.GlobalMap;

import java.util.*;

public class ColorPallete {
    private final List<Integer> values;
    private List<Double> pallete;

    public ColorPallete(List<Integer> values) {
        this.values = values;
        normalize();
    }

    private void normalize() {
        pallete = new ArrayList<>();
        Double max = values.stream().filter(Objects::nonNull).mapToDouble(v -> v).max().orElse(0);
        Double min = values.stream().filter(Objects::nonNull).mapToDouble(v -> v).min().orElse(0);
        for (int i = 0; i < values.size(); i++){
            Integer temp = values.get(i);
            if (temp == null)
                pallete.add((double) -1);
            else
                pallete.add(((double) temp - min)/(max - min));
        }
        //paleta ma wartości z zakresu 0,1,
    }

    public List<String> getRGBcodes(){
        List<String> rgbCodes = new ArrayList<>();
        for (Double color : pallete){
            if (color == -1){
                rgbCodes.add("rgb(128,128,128)"); //zwracamy szary
            }
            else{
                int g = (int) ((1-color)*255);
                rgbCodes.add("rgb(255," + g + ",0)");
            }
        }
        return rgbCodes;
    }

}
