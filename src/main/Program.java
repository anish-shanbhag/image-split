package main;
import main.Display;
import main.MouseManager;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
public class Program implements Runnable {
    public static boolean running = false;
    private static Thread thread;
    public static int mouseX, mouseY;
    private static MouseManager mouse;
    private static Display display;
    public static BufferedImage image;
    private static BufferStrategy bs;
    private static Graphics g;
    private static Graphics2D drawer;
    public static List<Sector> sectors = new ArrayList<>();
    public static Sector mainSector;
    public static boolean rendering = false;
    private static Sector hovered;
    public static long hoverTime = System.nanoTime(), renderTime = System.nanoTime();
    public static List<Sector> children = new ArrayList<>();
    public static List<Sector> out = new ArrayList<>();
    public static List<Sector> in = new ArrayList<>();
    public static boolean mouseMoved = true;
    public Program() {
        this.start();
    }
    public synchronized void start() {
        if (running) {
            return;
        }
        this.mouse = new MouseManager(this);
        display = new Display("ImageSplit", 1024, 1024);
        display.getFrame().addMouseMotionListener(mouse);
        display.getCanvas().addMouseMotionListener(mouse);
        try {
            URL url = new URL("https://picsum.photos/1024/1024/?random");
            image = ImageIO.read(url);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        mainSector = new Sector(0, 0, 1024, 1024, getAvg(0, 0, 1024, 1024));
        running = true;
        thread = new Thread(this);
        thread.start();

    }
    public void run() {
        while (running) {
            if (mouseMoved) {
                mouseMoved = false;
                mouseX = mouse.mouseX;
                mouseY = mouse.mouseY;
                mouseMoved();
            }
            if (System.nanoTime() - hoverTime > 1000000) {
                List<Sector> remove = new ArrayList<>();
                in.removeIf(e -> (e.alpha >= 240));
                out.removeIf(e -> (e.alpha <= 10));
                for (Sector sector : in) {
                    sector.alpha += (System.nanoTime() - hoverTime) / 1000000;
                    if (sector.alpha > 240) {
                        sector.alpha = 240;
                    }
                }
                for (Sector sector : out) {
                    sector.alpha -= (System.nanoTime() - hoverTime) / 1000000;
                    if (sector.alpha < 0) {
                        sector.alpha = 0;
                    }
                }
                hoverTime = System.nanoTime();
            }
            if (System.nanoTime() - renderTime > 10000000) {
                rendering = true;
                renderTime = System.nanoTime();
                if (bs == null) {
                    display.getCanvas().createBufferStrategy(3);
                    bs = display.getCanvas().getBufferStrategy();

                }
                g = bs.getDrawGraphics();
                drawer = (Graphics2D) g;
                drawer.setColor(Color.WHITE);
                drawer.fillRect(0, 0, 1024, 1024);
                for (Sector sector : out) {
                    drawer.setColor(new Color(sector.red, sector.green, sector.blue, sector.alpha));
                    int width = sector.xmax - sector.x;
                    int height = sector.ymax - sector.y;
                    drawer.fillRect(sector.x + (width / 2 * (250 - sector.alpha) / 250), sector.y + (height / 2 * (250 - sector.alpha) / 250), sector.xmax - sector.x - (width * (250 - sector.alpha) / 250), sector.ymax - sector.y - (height * (250 - sector.alpha) / 250));
                }
                for (Sector sector : in) {
                    drawer.setColor(new Color(sector.red, sector.green, sector.blue, sector.alpha));
                    int width = sector.xmax - sector.x;
                    int height = sector.ymax - sector.y;
                    drawer.fillRect(sector.x + (width / 2 * (250 - sector.alpha) / 250), sector.y + (height / 2 * (250 - sector.alpha) / 250), sector.xmax - sector.x - (width * (250 - sector.alpha) / 250), sector.ymax - sector.y - (height * (250 - sector.alpha) / 250));
                    //drawer.fillRect(sector.x + (width / 2 * sector.alpha / 250), sector.y + (height / 2 * sector.alpha / 250), sector.xmax - sector.x - (width * sector.alpha / 250), sector.ymax - sector.y - (height * sector.alpha / 250));
                }
                for (Sector sector : sectors) {
                    if (!out.contains(sector) && !in.contains(sector)) {
                        drawer.setColor(new Color(sector.red, sector.green, sector.blue, sector.alpha));
                        drawer.fillRect(sector.x, sector.y, sector.xmax - sector.x, sector.ymax - sector.y);
                    }
                }
                /*
                for (Sector sector : out) {
                    drawer.setColor(new Color(sector.red, sector.green, sector.blue, sector.alpha));
                    drawer.fillOval(sector.x, sector.y, sector.xmax - sector.x, sector.ymax - sector.y);
                }
                */
                bs.show();
                rendering = false;

            }

        }
        stop();
    }
    private synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static List<Integer> getAvg(int sx, int sy, int xmax, int ymax) {
        long totalRed = 0;
        long totalBlue = 0;
        long totalGreen = 0;
        int color;
        for (int x = sx; x < xmax; x++) {
            for (int y = sy; y < ymax; y++) {
                color = image.getRGB(x, y);
                totalRed += (color >> 16) & 0x000000FF;
                totalGreen += (color >> 8) & 0x000000FF;
                totalBlue += (color) & 0x000000FF;
            }
        }
        return Arrays.asList(Math.round(totalRed / ((xmax - sx) * (ymax - sy))), Math.round(totalGreen / ((xmax - sx) * (ymax - sy))), Math.round(totalBlue / ((xmax - sx) * (ymax - sy))));
    }
    public static void mouseMoved() {
        for (Sector item : sectors) {
            if (item.checkHover() && item != hovered) {
                hovered = item;
                children = new ArrayList<>();
                if (hovered != null) {
                    children.add(new Sector(hovered.x, hovered.y, ((hovered.xmax - hovered.x) / 2) + hovered.x, ((hovered.ymax - hovered.y) / 2) + hovered.y, getAvg(hovered.x, hovered.y, ((hovered.xmax - hovered.x) / 2) + hovered.x, ((hovered.ymax - hovered.y) / 2) + hovered.y)));
                    children.add(new Sector(((hovered.xmax - hovered.x) / 2) + hovered.x, hovered.y, hovered.xmax, ((hovered.ymax - hovered.y) / 2) + hovered.y, getAvg(((hovered.xmax - hovered.x) / 2) + hovered.x, hovered.y, hovered.xmax, ((hovered.ymax - hovered.y) / 2) + hovered.y)));
                    children.add(new Sector(hovered.x, ((hovered.ymax - hovered.y) / 2) + hovered.y, ((hovered.xmax - hovered.x) / 2) + hovered.x, hovered.ymax, getAvg(hovered.x, ((hovered.ymax - hovered.y) / 2) + hovered.y, ((hovered.xmax - hovered.x) / 2) + hovered.x, hovered.ymax)));
                    children.add(new Sector(((hovered.xmax - hovered.x) / 2) + hovered.x, ((hovered.ymax - hovered.y) / 2) + hovered.y, hovered.xmax, hovered.ymax, getAvg(((hovered.xmax - hovered.x) / 2) + hovered.x, ((hovered.ymax - hovered.y) / 2) + hovered.y, hovered.xmax, hovered.ymax)));
                    out.add(hovered);
                    in.remove(hovered);
                    sectors.remove(hovered);
                }
                for (Sector sector : children) {
                    if (sector.checkHover()) {
                        hovered = sector;
                        break;
                    }
                }
                break;
            }
        }
    }
}
