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
        Double max = values.stream().filter(Objects::nonNull).mapToDouble(v -> v).max().orElse(-1);
        Double min = values.stream().filter(Objects::nonNull).mapToDouble(v -> v).min().orElse(-1);
        for (int i = 0; i < values.size(); i++){
            Integer temp = values.get(i);
            if (temp == null)
                pallete.add(null);
            else
                pallete.add(((double) temp - min)/(max - min));
        }
        //paleta ma wartości z zakresu 0,1, oraz null
    }

    public List<String> getRGBcodes(){
        List<String> rgbCodes = new ArrayList<>();
        for (Double color : pallete){
            if (color == null){
                rgbCodes.add("rgb(0,0,0)"); //zwracamy czarny
            }
            else{
                int r = (int) (color*255);
                int g = (int) ((1-color)*255);
                rgbCodes.add("rgb(" + r +"," + g + ",0)");
            }
        }
        return rgbCodes;
    }
}
