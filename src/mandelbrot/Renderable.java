package mandelbrot;

import java.awt.image.BufferedImage;

public abstract class Renderable {

    // The image dimension.
    int width;
    int height;

    BufferedImage image;

    double curRe;
    double curIm;

    // The view window.
    double minRe;
    double minIm;
    double maxRe;
    double maxIm;

    int iterations;

    Renderable nextRenderable;
    Runnable nextRunnable;

    public Renderable render(BufferedImage img, double curRe, double curIm, double minRe, double minIm, double maxRe,
            double maxIm, int iterations) {

        this.reload(img.getWidth(), img.getHeight(), curRe, curIm, minRe, minIm, maxRe, maxIm, iterations, () -> {
            // Reload is finished
            this.drawOn(img);
            if (nextRenderable != null) {
                System.out.println("rendndnd");
                nextRenderable.render(img, curRe, curIm, minRe, minIm, maxRe, maxIm, iterations);
                nextRenderable = null;
            }
            if (nextRunnable != null) {
                nextRunnable.run();
                nextRunnable = null;
            }
        });
        return this;
    }

    public Renderable next(Renderable renderable) {
        this.nextRenderable = renderable;
        return this;
    }

    public Renderable next(Runnable runnable) {
        this.nextRunnable = runnable;
        return this;
    }

    protected abstract void reload(int width, int height, double curRe, double curIm, double minRe, double minIm,
            double maxRe, double maxIm, int iterations, Runnable done);

    protected abstract void drawOn(BufferedImage img);


    protected boolean sameViewWindow(double minRe, double minIm, double maxRe, double maxIm) {
        return this.minRe == minRe && this.minIm == minIm && this.maxRe == maxRe && this.maxIm == maxIm;
    }

    protected boolean sameImageSize(int width, int height) {
        return this.width == width && this.height == height;
    }

    public void setViewWindow(double minRe, double minIm, double maxRe, double maxIm) {
        this.minRe = minRe;
        this.minIm = minIm;
        this.maxRe = maxRe;
        this.maxIm = maxIm;
    }

    public void setImageSize(int width, int height) {
        this.width = width;
        this.height = height;
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
