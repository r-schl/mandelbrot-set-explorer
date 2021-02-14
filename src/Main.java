import mandelbrot.Application;

public class Main {
    
    public static void main(String... arguments) {
        if (arguments.length == 2) {
            Application app = new Application(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]));
        } else {
            Application app = new Application(600, 600);
        }
    }
}
