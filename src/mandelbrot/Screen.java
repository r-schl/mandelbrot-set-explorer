package mandelbrot;

import static java.awt.Color.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

public class Screen extends Canvas {

    // Default values
    static final Color ORBIT_COLOR = RED;
    static final int ORBIT_THICKNESS = 1;
    static final Color CURSOR_COLOR = BLACK;
    static final int CURSOR_WIDTH = 10;
    static final int CURSOR_HEIGHT = 10;
    static final int CURSOR_THICKNESS = 1;
    static final Color AXES_COLOR = BLACK;
    static final int AXES_THICKNESS = 1;
    static final int ARROW_LENGTH = 18;
    static final int ARROW_WIDTH = 14;

    public int width;
    public int height;

    // Render stuff
    private BufferStrategy bufferStrategy;
    private Graphics2D g;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void prepare() {
        if (!SwingUtilities.isEventDispatchThread())
            new Exception("Not EventDispatchThread").printStackTrace();
        // at this point we are on the Swing UI Thread
        bufferStrategy = this.getBufferStrategy();
        if (bufferStrategy == null) {
            this.createBufferStrategy(2);
            prepare();
            return;
        }
        Graphics graphics = bufferStrategy.getDrawGraphics();
        g = (Graphics2D) graphics;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        g.clearRect(0, 0, width, height);
    }

    public void finish() {
        if (!SwingUtilities.isEventDispatchThread())
            new Exception("Not EventDispatchThread").printStackTrace();
        if (g == null)
            return;
        g.dispose();
        bufferStrategy.show();
    }

    public void orbit(Orbit orbit, View v) {
        double[] data = orbit.data;
        if (data == null)
            return;
        g.setColor(ORBIT_COLOR);
        g.setStroke(new BasicStroke(ORBIT_THICKNESS));
        for (int i = 0; i < data.length - 2; i += 2) {
            g.drawLine(toPx(v.minRe, v.maxRe, data[i]), toPy(v.minIm, v.maxIm, data[i + 1]), toPx(v.minRe, v.maxRe, data[i + 2]), toPy(v.minIm, v.maxIm, data[i + 3]));
        }
    }

    public void mandelbrot(Mandelbrot mand) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, mand.data, 0, width);
        g.drawImage(image, 0, 0, null);
    }

    public void cursor(double re, double im) {
        
    }

    public int toPx(double minRe, double maxRe, double re) {
        return (int) (((re - minRe) * width) / Math.abs(maxRe - minRe));
    }

    public int toPy(double minIm, double maxIm, double im) {
        return (int) (height - (((-im + minIm) * height) / -Math.abs(maxIm - minIm)));
    }

}
