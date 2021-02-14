package mandelbrot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Mandelbrot {

     // set the number of threads to the available cores
     private final int NUMTHREADS = Runtime.getRuntime().availableProcessors();
     public final double BOUND = 2;
     public View view;
     public int iterations;
     public int[] data;
     public int[] gradient;
     SwingWorker<int[], Integer>[] workers;
     Screen screen;

     public Mandelbrot(Screen screen) {
        this.screen = screen;
        this.data = new int[screen.width * screen.height];
        this.workers = new SwingWorker[NUMTHREADS];
     }

     public void reload(View view, int iterations, JProgressBar progress, Runnable done) {
        if (!view.equals(this.view) || this.iterations != iterations) {
            // Update the values.
            this.view = new View(view);
            this.iterations = iterations;
            // Reset the progress bar.
            progress.setMinimum(0);
            progress.setMaximum(screen.width);
            progress.setValue(0);
            // Start threads.
            for (int k = 0; k < NUMTHREADS; k++) {
                int finalK = k;
                if (workers[k] != null) {
                    try {
                        workers[k].cancel(true);
                    } catch (Exception ignored) {
                    }
                }
                workers[k] = new SwingWorker<>() {
                    int xBegin;
                    int xEnd;
                    int xRange;
                    int yBegin;
                    int yEnd;
                    int yRange;

                    @Override
                    protected int[] doInBackground() {
                        xBegin = 0;
                        xEnd = screen.width;
                        xRange = xEnd - xBegin;
                        yBegin = finalK * (screen.height / NUMTHREADS);
                        yEnd = (finalK * (screen.height / NUMTHREADS) + (screen.height / NUMTHREADS));
                        yRange = yEnd - yBegin;
                        int[] part = new int[xRange * yRange];
                        for (int py = 0; py < yRange; py++) {
                            for (int px = 0; px < xRange; px++) {
                                double re = view.toRe(screen.width, xBegin + px);
                                double im = view.toIm(screen.height, yBegin + py);
                                part[py * xRange + px] = iterate(re, im) == iterations ? 0x000000 : 0xFFFFFF;
                            }
                            publish(py);
                        }
                        return part;
                    }

                    @Override
                    protected void process(List<Integer> lines) {
                        progress.setValue(progress.getValue() + lines.size());
                    }

                    @Override
                    public void done() {
                        try {
                            int[] part = get();
                            System.arraycopy(part, 0, data, part.length * finalK, part.length);
                        } catch (InterruptedException | ExecutionException ignored) {
                            ignored.printStackTrace();
                        }
                        done.run();
                    }
                };
                workers[k].execute();
            }
        }
    }


    private int iterate(double cRe, double cIm) {
        // start with z = 0
        double zRe = 0;
        double zIm = 0;
        // for every iteration do...
        for (int i = 0; i < iterations; i++) {
            // square z
            double sqrZRe = (zRe * zRe) - (zIm * zIm);
            double sqrZIm = (zRe * zIm) + (zIm * zRe);
            // add c
            zRe = sqrZRe + cRe;
            zIm = sqrZIm + cIm;
            // check if the sequence diverges
            if (zRe * zRe + zIm * zIm > BOUND * BOUND) return i;
        }
        return iterations;
    }

    public void export(File outputFile) {
        BufferedImage image = new BufferedImage(screen.width, screen.height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, screen.width, screen.height, data, 0, screen.width);
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
