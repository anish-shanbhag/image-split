package main;
import java.awt.event.*;
public class MouseManager implements MouseMotionListener {
    Program program;
    private boolean mouseMoved;
    public int mouseX, mouseY;

    public MouseManager(Program program) {
        this.program = program;
        this.mouseMoved = false;

    }
    @Override
    public void mouseMoved(MouseEvent e) {
        Program.mouseMoved = true;
        mouseX = e.getX();
        mouseY = e.getY();
    }
    /*
    public void update() {
        if (Program.running && mouseMoved) {
            Program.mouseMoved = false;
            Program.mouseX = mouseX;
            Program.mouseY = mouseY;
        }
    }
    */
    @Override
    public void mouseDragged(MouseEvent e) {

    }
}
