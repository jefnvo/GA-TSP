import java.io.IOException;

public class Genetic {
    public static void main(String[] args) throws IOException {
        TSPReader reader = new TSPReader();
        Long[][] distanceWeight = reader.symmetricMatrix("../resources/gr21.tsp.txt");

        TSPSolver geneticTSPSolver = new TSPSolver(reader.getNumCities(), 150, 500, distanceWeight,0.8,0.1);
        System.out.println(geneticTSPSolver.geneticSolver());

    }
}
