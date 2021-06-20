package mandelbrot;

import java.util.concurrent.ExecutionException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static java.awt.Color.*;
import java.awt.*;

import javax.swing.SwingWorker;

public class Orbit {

    static final Color ORBIT_COLOR = RED;
    static final int ORBIT_THICKNESS = 1;
    private final double BOUND = 16;
    private SwingWorker<double[], Void> worker;

    private int _iterations;
    private double _curRe;
    private double _curIm;
    private double[] _numbers;

    public int height;
    public int width;
    public double minRe;
    public double minIm;
    public double maxRe;
    public double maxIm;
    public int iterations;
    private double curRe;
    private double curIm;


 

    private void calculate(Runnable done) {

        this._iterations = iterations;
        this._curRe = curRe;
        this._curIm = curIm;

        if (worker != null) {
            try {
               worker.cancel(true);
            } catch (Exception ignored) {
            }
        }

        worker = new SwingWorker<>() {
            @Override
            protected double[] doInBackground() {
                double[] orbit = new double[(iterations * 2) + 2];
                sequence(curRe, curIm, orbit);
                return orbit;
            }

            @Override
            public void done() {
                // from UI Thread
                try {
                    _numbers = get();
                } catch (InterruptedException | ExecutionException ignored) {
                }
                done.run();
            }
        };
        worker.execute();
    }

    public Orbit set(int width, int height, double curRe, double curIm, double minRe, double minIm, double maxRe, double maxIm, int iterations) {
        this.width = width;
        this.height = height;
        this.minRe = minRe;
        this.minIm = minIm;
        this.maxRe = maxRe;
        this.maxIm = maxIm;
        this.iterations = iterations;
        this.curRe = curRe;
        this.curIm = curIm;
        return this;
    }

    public void render(BufferedImage img, Runnable next) {
        if (_iterations != iterations || _curRe != curRe || _curIm != curIm) {
            this.calculate(() -> {
                this.drawOn(img);
                next.run();
            });
        } else {
            this.drawOn(img);
            next.run();
        }
    }

    private void drawOn(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // g.setRenderingHints(rh);
        g.setColor(ORBIT_COLOR);
        g.setStroke(new BasicStroke(ORBIT_THICKNESS));
        for (int i = 0; i < this._numbers.length - 2; i += 2) {
            g.drawLine(toPx(this._numbers[i]), toPy(this._numbers[i + 1]), toPx(this._numbers[i + 2]), toPy(this._numbers[i + 3]));
        }
    }

/*     @Override
    protected void drawOn(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // g.setRenderingHints(rh);
        g.setColor(ORBIT_COLOR);
        g.setStroke(new BasicStroke(ORBIT_THICKNESS));
        for (int i = 0; i < numbers.length - 2; i += 2) {
            g.drawLine(toPx(numbers[i]), toPy(numbers[i + 1]), toPx(numbers[i + 2]), toPy(numbers[i + 3]));
        }
    } */

    private void sequence(double cRe, double cIm, double[] orbit) {
        // start with z = 0
        double zRe = 0;
        double zIm = 0;
        orbit[0] = 0;
        orbit[1] = 0;
        // for every iteration do...
        for (int i = 0; i < iterations; i++) {
            // square z
            double sqrZRe = (zRe * zRe) - (zIm * zIm);
            double sqrZIm = (zRe * zIm) + (zIm * zRe);
            // add c
            zRe = sqrZRe + cRe;
            zIm = sqrZIm + cIm;
            orbit[(i + 1) * 2] = zRe;
            orbit[(i + 1) * 2 + 1] = zIm;
            // check if the sequence diverges
            if (zRe * zRe + zIm * zIm > BOUND * BOUND) {
                for (int a = i + 1; a < this.iterations; a++) {
                    orbit[(a + 1) * 2] = zRe;
                    orbit[(a + 1) * 2 + 1] = zIm;
                }
                return;
            }
        }
    }

    public int toPx(double re) {
        return (int) (((re - minRe) * width) / Math.abs(maxRe - minRe));
    }

    public int toPy(double im) {
        return (int) (height - (((-im + minIm) * height) / -Math.abs(maxIm - minIm)));
    }

    public double toRe(int px) {
        return ((px * Math.abs(maxRe - minRe)) / width + minRe);
    }

    public double toIm(int py) {
        return (((height - py) * Math.abs(maxIm - minIm)) / height + minIm);
    }

}
