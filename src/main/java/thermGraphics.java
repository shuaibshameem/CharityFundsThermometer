import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;
import java.util.concurrent.TimeUnit;

public class thermGraphics extends JPanel{

    private double thermTotal;
    private List<thermData> donors = new ArrayList<>();

    public thermGraphics(){

        ActionListener listener = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        };
        Timer timer = new Timer(60, listener);
        timer.start();

        setPreferredSize(new Dimension(1920,1080));
        initiateCanvas();
    }



    private void initiateCanvas(){
        JFrame frame = new JFrame("Thermomenter");
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // below line will move graphics to second screen
        // frame.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1].getDefaultConfiguration().getBounds().x, frame.getY());
        // below line moves graphics to right side of main window (for my screen 3440x1440)
        frame.setLocation(1520, 0);
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

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,1920,1080);

        // draw thermometer
        drawThermometer(g2);

        // draw Goal Lines
        drawGoalLines(g2);

        // draw donor names and donation amount
        drawDonors(g2);

        // draw TCF Logo
        drawLogo(g2);

        /*try{
            TimeUnit.MILLISECONDS.sleep(30);
        } catch (InterruptedException x){

        }
        repaint();*/
    }

    private void drawLogo(Graphics2D g2){

        g2.setColor(Color.WHITE);
        g2.fillRect(700,0,1220,350);
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("D:\\Windows Default Directory Backup\\Documents\\GitHub\\CharityFundsThermometer\\src\\main\\resources\\TCF-USA-newLogo.png"));
        }catch (IOException e){
            System.out.println("Nope.");
        }
        g2.drawImage(img, 975, 50, this);
    }

    private void drawGoalLines(Graphics2D g2){
        int x1 = 267;
        int x2 = 475;
        int y50 = 640;
        int y100 = 556;
        int y150 = 472;
        int y200 = 388;
        int y250 = 303;
        int y300 = 214;
        int y350 = 134;
        int y400 = 50;
        int xOffset = 16;
        int yOffset = 12;


        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(5));
        g2.drawLine(x1, y50, x2, y50);      //50k
        g2.drawLine(x1, y100, x2, y100);    //100k
        g2.drawLine(x1, y150, x2, y150);    //150k
        g2.drawLine(x1, y200, x2, y200);    //200k
        g2.drawLine(x1, y250, x2, y250);    //250k
        g2.drawLine(x1, y300, x2, y300);    //300k
        g2.drawLine(x1, y350, x2, y350);    //350k
        g2.drawLine(350, y400, x2, y400); //400k

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("TimesRoman", Font.BOLD, 26));
        g2.drawString("$50,000",x2+xOffset,y50+yOffset);
        g2.drawString("$100,000",x2+xOffset,y100+yOffset);
        g2.drawString("$150,000",x2+xOffset,y150+yOffset);
        g2.drawString("$200,000",x2+xOffset,y200+yOffset);
        g2.drawString("$250,000",x2+xOffset,y250+yOffset);
        g2.drawString("$300,000",x2+xOffset,y300+yOffset);
        g2.drawString("$350,000",x2+xOffset,y350+yOffset);

        g2.setColor(Color.RED);
        g2.setFont(new Font("TimeRoman", Font.BOLD, 32));
        g2.drawString("$400,000",x2+xOffset, y400+yOffset);

    }

    private void drawDonors(Graphics2D g2){
        for(thermData current : donors){
            g2.setColor(current.color);
            g2.setFont(new Font("TimesRoman", Font.BOLD, current.size));
            g2.drawString((current.name + " $" + String.format("%,.2f", current.amount)), current.x, current.y);
            current.y-= 2;
            if(current.y > 980) break;
            if(current.y < -30) removeDonor();
        }
    }

    private void drawThermometer(Graphics2D g2){
        // determine thermometer height
        double fraction = thermTotal / 400000;
        if(fraction > 1) fraction = 1;
        int thermHeight = (int)Math.floor(675 * fraction);

        // draw thermometer
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));

        // thermometer bottom outline
        g2.drawOval(175,725,350,325);
        g2.drawOval(173,723,353,327);

        // thermometer top outline
        g2.drawRoundRect(263,50,175,760,200,100);

        g2.setColor(Color.RED);

        // thermometer bottom fill
        g2.fillOval(175,725,350,325);

        // thermometer top fill (dynamic to amount collected)
        g2.fillRoundRect(263,725-thermHeight, 175, thermHeight+100, 200, 100);
    }


}
