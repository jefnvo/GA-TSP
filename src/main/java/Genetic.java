import java.io.IOException;

public class Genetic {
    public static void main(String[] args) throws IOException {
        TSPReader reader = new TSPReader("../resources/gr21.tsp.txt");

        TSPSolver geneticTSPSolver = new TSPSolver(reader.getNumCities(), 10000, 1000, reader.getDistanceWeight(),0.8,0.1);
        System.out.println(geneticTSPSolver.geneticSolver());

    }
}
