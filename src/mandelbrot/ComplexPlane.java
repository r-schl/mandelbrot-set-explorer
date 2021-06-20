package mandelbrot;

import java.awt.image.BufferedImage;

public class ComplexPlane extends Renderable {

    @Override
    protected void reload(int width, int height, double curRe, double curIm, double minRe, double minIm, double maxRe,
            double maxIm, int iterations, Runnable done) {
        this.minRe = minRe;
        this.minIm = minIm;
        this.maxRe = maxRe;
        this.maxIm = maxIm;

        done.run();

    }

    @Override
    protected void drawOn(BufferedImage img) {
        // TODO Auto-generated method stub

    }

}
