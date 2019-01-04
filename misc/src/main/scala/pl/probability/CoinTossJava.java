package pl.probability;


import java.util.Random;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

public class CoinTossJava {
    static long timeMarker = System.currentTimeMillis();
    static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        int tries = 10000000;
        int throwsNumber = 100;
        int heads = 50;

        int throwsResults = IntStream.range(1, tries).reduce(0, (successes, i) -> {
            if (System.currentTimeMillis() - timeMarker > 5000) {
                System.out.println("progress: " + i + " / " + tries + " (" + (100 * i / tries) + "%)");
                timeMarker = System.currentTimeMillis();
            }
            int coinsLandings = IntStream.range(1, throwsNumber).reduce(0, (sum, right1) -> sum + random.nextInt(2));
            if (coinsLandings == heads) return successes + 1;
            else return successes;
        });


        int successes = throwsResults;
        double probability = 1.0 * successes / tries;

        System.out.println("successes = " + successes);
        System.out.println("probability = " + probability);
    }
}
