import java.awt.*;
public class thermData {

    public String name;
    public double amount;
    public int x, y, size;
    public Color color;

    public thermData(String name_In, double amount_In, Color color_In, int size_In){
        name = name_In;
        amount = amount_In;
        y = 1080;
        x = (int)Math.floor(Math.random()*500 + 700);
        color = color_In;
        size = size_In;
    }

}
