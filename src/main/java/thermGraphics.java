import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class thermGraphics extends JPanel{

    private double thermTotal;
    private List<thermData> donors = new ArrayList<>();

    public thermGraphics(){
        setPreferredSize(new Dimension(1920,1080));
        initiateCanvas();
    }

    private void initiateCanvas(){
        JFrame frame = new JFrame("Thermomenter");
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920,1080);
        frame.pack();
        frame.setVisible(true);
    }

    public void addDonor(String name, double amount){
        thermTotal += amount;
        Color c;
        int size;
        if(amount < 1000) {
            c = Color.BLACK;
            size = 24;
        }
        else if(amount < 5000) {
            c = Color.BLUE;
            size = 28;
        }
        else if(amount < 10000) {
            c = Color.GREEN;
            size = 36;
        }
        else {
            c = Color.RED;
            size = 52;
        }
        donors.add(new thermData(name, amount, c, size));
    }

    public void removeDonor(){
        donors.remove(0);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        // determine thermometer height
        double fraction = thermTotal / 400000;
        if(fraction > 1) fraction = 1;
        int thermHeight = (int)Math.floor(675 * fraction);

        // draw thermometer
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));
        g2.drawOval(175,725,350,325);
        g2.drawOval(173,723,353,327);
        g2.drawRoundRect(263,50,175,760,200,100);
        g.setColor(Color.RED);
        g.fillOval(175,725,350,325);
        g.fillRoundRect(263,725-thermHeight, 175, thermHeight+100, 200, 100);

        // draw donor names and donation amount
        for(thermData current : donors){
            g.setColor(current.color);
            g.setFont(new Font("TimesRoman", Font.BOLD, current.size));
            g.drawString((current.name + " $" + String.format("%.2f", current.amount)), current.x, current.y);
            current.y--;
            if(current.y > 980) break;
            if(current.y < -30) removeDonor();
        }
        try{
            TimeUnit.MILLISECONDS.sleep(30);
        } catch (InterruptedException x){

        }
        repaint();
    }



}
