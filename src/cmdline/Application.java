package cmdline;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Application {

    private final int NUMTHREADS = Runtime.getRuntime().availableProcessors();

    double radius = 2;
    int iterations = 400;
    SwingWorker<int[], Integer>[] workers;
    int[] data;

    double minRe = -1.5;
    double minIm = -1.5;
    double maxRe = 1.5;
    double maxIm = 1.5;

    int width = 10000;
    int height = 10000;

    int finished = 0;

    public static void main(String... args) {
        Application mand = new Application();
    }

    public Application() {
        SwingUtilities.invokeLater(this::init);
    }

    void init() {
        build(this::onFinish);
    }

    void onFinish() {
        finished++;
        // all worker threads finished
        if (finished == workers.length){
            export(new File("D:/Robert/Desktop/lol22.jpg"));
            finished = 0;
        }
    }

    void build(Runnable onFinish) {

        this.data = new int[width * height];
        this.workers = new SwingWorker[NUMTHREADS];

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
                    xEnd = width;
                    xRange = xEnd - xBegin;
                    yBegin = finalK * (height / NUMTHREADS);
                    yEnd = (finalK * (height / NUMTHREADS) + (height / NUMTHREADS));
                    yRange = yEnd - yBegin;
                    int[] part = new int[xRange * yRange];
                    for (int py = 0; py < yRange; py++) {
                        for (int px = 0; px < xRange; px++) {
                            double re = toRe(xBegin + px);
                            double im = toIm(yBegin + py);
                            part[py * xRange + px] = iterate(re, im) == iterations ? 0x000000 : 0xFFFFFF;
                        }
                        publish(py);
                    }
                    return part;
                }

                @Override
                public void done() {
                    try {
                        int[] part = get();
                        System.arraycopy(part, 0, data, part.length * finalK, part.length);
                    } catch (InterruptedException | ExecutionException ignored) {
                        ignored.printStackTrace();
                    }
                    onFinish.run();
                }
            };
            workers[k].execute();
        }
    }

    int iterate(double cRe, double cIm) {
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
            if (zRe * zRe + zIm * zIm > radius * radius)
                return i;
        }
        return iterations;
    }

    void export(File outputFile) {
        System.out.println(outputFile);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, data, 0, width);
        try {
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int toPx(double re) {
        return (int) (((re - minRe) * width) / Math.abs(maxRe - minRe));
    }

    int toPy(double im) {
        return (int) (height - (((-im + minIm) * height) / -Math.abs(maxIm - minIm)));
    }

    double toRe(int px) {
        return ((px * Math.abs(maxRe - minRe)) / width + minRe);
    }

    double toIm(int py) {
        return (((height - py) * Math.abs(maxIm - minIm)) / height + minIm);
    }

}
