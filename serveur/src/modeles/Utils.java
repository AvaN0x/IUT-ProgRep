package serveur.src.modeles;

public class Utils {
    public static int randomInt(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }
}
