import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

public class Orbit {

    static final double ESCAPE_RADIUS = 2;

    private double cRe;
    private double cIm;
    private Mandelbrot mandelbrot;

    private SwingWorker<double[], Integer> worker;
    private double[] data;

    private boolean isBuilding;
    private boolean isBuilt;

    public Orbit(double cRe, double cIm, Mandelbrot mandelbrot) {
        this.cRe = cRe;
        this.cIm = cIm;
        this.mandelbrot = mandelbrot;
    }

    public double[] getData() {
        return this.data;
    }

    public boolean isBuilding() {
        return this.isBuilding;
    }

    public boolean isBuilt() {
        return this.isBuilt;
    }

    public void build(Runnable onFinish) {
        this.isBuilding = true;
        this.worker = new SwingWorker<double[], Integer>() {

            @Override
            protected double[] doInBackground() throws Exception {
                double zRe = 0.0D;
                double zIm = 0.0D;
                ArrayList<Double> data = new ArrayList<>();
                data.add(zRe);
                data.add(zIm);
                for (int n = 1; n <= mandelbrot.getNMax(); ++n) {
                    double sqrZRe = zRe * zRe - zIm * zIm;
                    double sqrZIm = zRe * zIm + zIm * zRe;
                    zRe = sqrZRe + cRe;
                    zIm = sqrZIm + cIm;
                    if (zRe * zRe + zIm * zIm > ESCAPE_RADIUS * ESCAPE_RADIUS) {
                        return data.stream().mapToDouble(d -> d).toArray();
                    }
                    data.add(zRe);
                    data.add(zIm);
                }
                return data.stream().mapToDouble(d -> d).toArray();
            }

            public void done() {
                try {
                    data = this.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                isBuilt = true;
                isBuilding = false;
                onFinish.run();
            }
        };

        this.worker.execute();
    }

    public void abort() {
        isBuilt = false;
        isBuilding = false;
        if (this.worker != null) {
            try {
                this.worker.cancel(true);
            } catch (Exception var9) {
            }
        }
    }
}
