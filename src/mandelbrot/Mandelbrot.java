package mandelbrot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

public class Mandelbrot {

    // Set the number of threads to the available cores.
    private final int NUMTHREADS = Runtime.getRuntime().availableProcessors();
    private final double ESCAPE_RADIUS = 2; // The escape radius.
    private SwingWorker<int[], Integer>[] workers = new SwingWorker[NUMTHREADS];

    private int finished = 0;

    private int _height;
    private int _width;
    private double _minRe;
    private double _minIm;
    private double _maxRe;
    private double _maxIm;
    private int _iterations;
    private int[] _data;

    public int height;
    public int width;
    public double minRe;
    public double minIm;
    public double maxRe;
    public double maxIm;
    public int iterations;

    private void calculate(Runnable done) {

        // Update the values.
        this._height = height;
        this._width = width;
        this._minRe = minRe;
        this._minIm = minIm;
        this._maxRe = maxRe;
        this._maxIm = maxIm;
        this._iterations = iterations;
        this._data = new int[width * height];

        // Start threads.
        for (int i = 0; i < NUMTHREADS; i++) {
            final int k = i;
            if (workers[i] != null) {
                try {
                    workers[i].cancel(true);
                } catch (Exception ignored) {
                }
            }

            workers[i] = new SwingWorker<>() {

                @Override
                protected int[] doInBackground() {
                    int yBegin = k * (height / NUMTHREADS);
                    int yEnd = (k * (height / NUMTHREADS) + (height / NUMTHREADS));
                    int yRange = yEnd - yBegin;
                    int[] part = new int[width * yRange];
                    for (int py = 0; py < yRange; py++) {
                        for (int px = 0; px < width; px++) {
                            double re = toRe(0 + px);
                            double im = toIm(yBegin + py);
                            part[py * width + px] = iterate(re, im) == iterations ? 0x000000 : 0xFFFFFF;
                        }
                        publish(py);
                    }
                    return part;
                }

                @Override
                protected void process(List<Integer> lines) {
                    // progress.setValue(progress.getValue() + lines.size());
                }

                @Override
                public void done() {
                    try {
                        int[] part = get();
                        System.arraycopy(part, 0, _data, part.length * k, part.length);
                    } catch (InterruptedException | ExecutionException ignored) {
                        ignored.printStackTrace();
                    }
                    finished++;
                    // If all threads have finished
                    if (finished == workers.length) {
                        done.run();
                        finished = 0;
                    }
                }
            };
            workers[i].execute();
        }
    }

    public Mandelbrot set(int width, int height, double minRe, double minIm, double maxRe, double maxIm, int iterations) {
        this.width = width;
        this.height = height;
        this.minRe = minRe;
        this.minIm = minIm;
        this.maxRe = maxRe;
        this.maxIm = maxIm;
        this.iterations = iterations;
        return this;
    }

    public void render(BufferedImage img, Runnable next) {
        if (_minRe != minRe || _minIm != minIm || _maxRe != maxRe || _maxIm != maxIm || _iterations != iterations) {
            this.calculate(() -> {
                this.drawOn(img);
                next.run();
            });
        } else {
            this.drawOn(img);
            next.run();
        }
    }

    public void data(int[] rgb, Runnable next) {
        if (_minRe != minRe || _minIm != minIm || _maxRe != maxRe || _maxIm != maxIm || _iterations != iterations) {
            this.calculate(() -> {
                for (int i = 0; i < rgb.length; i++) rgb[i] = this._data[i];
                next.run();
            });
        } else {
            for (int i = 0; i < rgb.length; i++) rgb[i] = this._data[i];
            next.run();
        }
    }

    private void drawOn(BufferedImage img) {
        img.setRGB(0, 0, width, height, this._data, 0, width);
    }

    private int iterate(double cRe, double cIm) {
        // start with z = 0
        double zRe = 0;
        double zIm = 0;
        // for every iteration do...
        for (int i = 0; i < _iterations; i++) {
            // square z
            double sqrZRe = (zRe * zRe) - (zIm * zIm);
            double sqrZIm = (zRe * zIm) + (zIm * zRe);
            // add c
            zRe = sqrZRe + cRe;
            zIm = sqrZIm + cIm;
            // check if the sequence diverges
            if (zRe * zRe + zIm * zIm > ESCAPE_RADIUS * ESCAPE_RADIUS)
                return i;
        }
        return _iterations;
    }

    public void export(File outputFile) {
       /*  BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, _data, 0, width);
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } */
    }

    public int toPx(double re) {
        return (int) (((re - _minRe) * _width) / Math.abs(_maxRe - _minRe));
    }

    public int toPy(double im) {
        return (int) (_height - (((-im + _minIm) * _height) / -Math.abs(_maxIm - _minIm)));
    }

    public double toRe(int px) {
        return ((px * Math.abs(_maxRe - _minRe)) / _width + _minRe);
    }

    public double toIm(int py) {
        return (((_height - py) * Math.abs(_maxIm - _minIm)) / _height + _minIm);
    }

}

