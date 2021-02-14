package mandelbrot;

public class View {

    static final View DEFAULT = new View(-1.5, -1.5, 1.5, 1.5);
    static final View SEAHORSES = new View(-0.7668749999999999, 0.039687500000000195, -0.7356249999999999,
            0.0709375000000002);
    static final View ELEFANTS = new View(0.25110595703124994, -1.3183593749999155E-4, 0.25135009765624994,
            1.1230468750000845E-4);

    public double minRe;
    public double minIm;
    public double maxRe;
    public double maxIm;

    public View(double minRe, double minIm, double maxRe, double maxIm) {
        this.set(minRe, minIm, maxRe, maxIm);
    }

    public View() {
        this.set(DEFAULT);
    }

    public View(View other) {
        this.set(other);
    }

    public View(double[] values) {
        this.set(values);
    }

    public void set(double[] values) {
        this.minRe = values[0];
        this.minIm = values[1];
        this.maxRe = values[2];
        this.maxIm = values[3];
    }

    public void set(double minRe, double minIm, double maxRe, double maxIm) {
        this.minRe = minRe;
        this.minIm = minIm;
        this.maxRe = maxRe;
        this.maxIm = maxIm;
    }

    public void set(View other) {
        this.minRe = other.minRe;
        this.minIm = other.minIm;
        this.maxRe = other.maxRe;
        this.maxIm = other.maxIm;
    }

    public double rangeRe() {
        return Math.abs(maxRe - minRe);
    }

    public double rangeIm() {
        return Math.abs(maxIm - minIm);
    }

    public void zoom(double re, double im, double r) {
        double rangeRe = rangeRe();
        double rangeIm = rangeIm();
        this.minRe = -rangeRe / (2 * r) + re;
        this.minIm = -rangeIm / (2 * r) + im;
        this.maxRe = rangeRe / (2 * r) + re;
        this.maxIm = rangeIm / (2 * r) + im;
    }

    public double toRe(int width, int px) {
        return ((px * rangeRe()) / width + minRe);
    }

    public double toIm(int height, int py) {
        return (((height - py) * rangeIm()) / height + minIm);
    }

    public void readMinRe(String val) {
        try {
            this.minRe = Double.parseDouble(val);
        } catch (Exception ignored) {
        }
    }

    public void readMinIm(String val) {
        try {
            this.minIm = Double.parseDouble(val);
        } catch (Exception ignored) {
        }
    }

    public void readMaxRe(String val) {
        try {
            this.maxRe = Double.parseDouble(val);
        } catch (Exception ignored) {
        }
    }

    public void readMaxIm(String val) {
        try {
            this.maxIm = Double.parseDouble(val);
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof View)) return false;
        View v = (View) object;
        return this.minRe == v.minRe && this.minIm == v.minIm && this.maxRe == v.maxRe && this.maxIm == v.maxIm;
    }

}
