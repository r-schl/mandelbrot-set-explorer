package mandelbrot;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

public class Orbit {

    final double BOUND = 16;
    public double cursorX;
    public double cursorY;
    public int iterations;
    public double[] data;
    private SwingWorker<double[], Void> worker;


    public void reload(double cursorX, double cursorY, int iterations, Runnable done) {
        if (this.cursorX != cursorX || this.cursorY != cursorY || this.iterations != iterations) {
            
            // Update the values.
            this.cursorX = cursorX;
            this.cursorY = cursorY;
            this.iterations = iterations;

            // Cancel the worker thread if still running.
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
                    sequence(cursorX, cursorY, orbit);
                    return orbit;
                }

                @Override
                public void done() {
                    // from UI Thread
                    try {
                        data = get();
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                    done.run();
                }
            };
            worker.execute();
        }
    }

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
    
}
