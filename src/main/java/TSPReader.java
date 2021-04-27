import java.io.File;
import java.io.IOException;
import java.util.Scanner;

class TSPReader {
    int numCities;

    public int getNumCities() {
        return numCities;
    }

    public void setNumCities(int numCities) {
        this.numCities = numCities;
    }

    public long[][] getDistanceWeight() {
        return distanceWeight;
    }

    public void setDistanceWeight(long[][] distanceWeight) {
        this.distanceWeight = distanceWeight;
    }

    long[][] distanceWeight;

    public TSPReader(String pathname) throws IOException {
        try (Scanner input = new Scanner(new File(pathname))) {
            // The input files follow the TSPLib "explicit" format.
            String str = new String();
            String[] pch = new String[2];
            while (true) {
                str = input.nextLine();
                pch = str.split(":");
                if (pch[0].compareTo("DIMENSION")==0) {
                    numCities = Integer.parseInt(pch[1].trim());
                    System.out.println("Number of cities = " + numCities);
                } else if (pch[0].compareTo("EDGE_WEIGHT_SECTION")==0) {
                    break;
                }
            }
            distanceWeight = new long[numCities][numCities];
            // Distance from i to j
            for (int i = 0; i < numCities; i++) {
                for (int j = 0; j < i+1; j++) {
                    distanceWeight[i][j] = input.nextInt();
                    distanceWeight[j][i] = distanceWeight[i][j];
                }
            }
        }
    }

}

