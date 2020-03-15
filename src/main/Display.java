package main;
import javax.swing.JFrame;
import java.awt.*;
public class Display {
    private JFrame frame;
    private Canvas canvas;
    private String title;
    private int sizeX, sizeY;
    public Display(String title, int sizeX, int sizeY) {
        this.title = title;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        createDisplay();
    }
    private void createDisplay() {
        frame = new JFrame(title);
        frame.setSize(sizeX, sizeY);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //Toolkit toolkit = Toolkit.getDefaultToolkit();
        //Image image = ImageLoader.loadImage("creation_ui");
        //frame.setCursor(toolkit.createCustomCursor(image, new Point(0, 0), ""));
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(sizeX, sizeY));
        canvas.setMaximumSize(new Dimension(sizeX, sizeY));
        canvas.setMinimumSize(new Dimension(sizeX, sizeY));
        frame.add(canvas);
        frame.pack();

    }
    public Canvas getCanvas() {
        return canvas;
    }
    public JFrame getFrame() {
        return frame;
    }

}
