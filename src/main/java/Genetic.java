import java.util.ArrayList;
import java.util.Random;

public class Genetic {
    public static void main(String[] args) {
        ArrayList<City> arrayCity = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 7; i++) {
            arrayCity.add(new City(rand.nextInt(100), rand.nextInt(100)));
        }
        TSPSolver geneticTSPSolver = new TSPSolver(7, 30, 10, arrayCity,0.8,0.1);
        System.out.println(geneticTSPSolver.geneticSolver());

    }
}
