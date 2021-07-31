import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

public class Orbit {

    static final double ESCAPE_RADIUS = 2;

    private double cRe;
    private double cIm;
    private Mandelbrot mandelbrot;

    private SwingWorker<int[], Integer> worker;
    private int[] data;

    private boolean isBuilding;
    private boolean isBuilt;

    public Orbit(double cRe, double cIm, Mandelbrot mandelbrot) {
        this.cRe = cRe;
        this.cIm = cIm;
        this.mandelbrot = mandelbrot;
    }

    public double getCRe() {
        return this.cRe;
    }

    public double getCIm() {
        return this.cIm;
    }

    public int[] getData() {
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
        this.worker = new SwingWorker<int[], Integer>() {

            @Override
            protected int[] doInBackground() throws Exception {
                double zRe = 0.0D;
                double zIm = 0.0D;
                ArrayList<Integer> data = new ArrayList<>();
                data.add(getPixX(zRe));
                data.add(getPixY(zIm));
                for (int n = 1; n <= mandelbrot.getNMax(); ++n) {
                    double sqrZRe = zRe * zRe - zIm * zIm;
                    double sqrZIm = zRe * zIm + zIm * zRe;
                    zRe = sqrZRe + cRe;
                    zIm = sqrZIm + cIm;
                    if (zRe * zRe + zIm * zIm > ESCAPE_RADIUS * ESCAPE_RADIUS) {
                        return data.stream().mapToInt(i -> i).toArray();
                    }
                    data.add(getPixX(zRe));
                    data.add(getPixY(zIm));
                }
                return data.stream().mapToInt(i -> i).toArray();
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

    private int getPixX(double re) {
        int pixX = (int) (((re - mandelbrot.getMinRe()) * mandelbrot.getWidth())
                / Math.abs(mandelbrot.getMaxRe() - mandelbrot.getMinRe()));
        return pixX;
    }

    private int getPixY(double im) {
        int pixY = (int) (mandelbrot.getHeight() - (((-im + mandelbrot.getMinIm()) * mandelbrot.getHeight())
                / -Math.abs(mandelbrot.getMaxIm() - mandelbrot.getMinIm())));
        return pixY;
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
