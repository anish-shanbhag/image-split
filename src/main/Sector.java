package main;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
public class Sector {
    public int x, y, xmax, ymax, red, green, blue;
    public Color color;
    public int alpha;
    public Sector(int x, int y, int xmax, int ymax, List<Integer> color) {
        this.x = x;
        this.y = y;
        this.xmax = xmax;
        this.ymax = ymax;
        this.red = color.get(0);
        this.green = color.get(1);
        this.blue = color.get(2);
        this.alpha = 120;
        this.color = new Color(red, green, blue);
        Program.sectors.add(this);
        Program.in.add(this);
    }
    public boolean checkHover() {
        if (Program.mouseX > this.x && Program.mouseX < this.xmax && Program.mouseY > this.y && Program.mouseY < this.ymax) {
            return true;
        }
        return false;
    }
}
