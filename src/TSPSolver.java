import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class TSPSolver {
    int numCities;
    int populationSize;
    int generations;
    double probCrossover;
    double probMutation;
    ArrayList<City> randomPopulation;
    public TSPSolver(int numCities, int populationSize, int generations, ArrayList<City> randomPopulation, double probCrossover, double probMutation ){
        this.numCities = numCities;
        this.populationSize = populationSize;
        this.generations =  generations;
        this.randomPopulation = randomPopulation;
        probCrossover = probCrossover;
        probMutation = probMutation;

    }

    public ArrayList<City> geneticSolver(int numEdges, int populationSize, int generations, ArrayList<City> randomPopulation) {
        Random rand = new Random();
        double [][] distanceMatrix =  getDistanceMatrix(numEdges, randomPopulation);
        ArrayList<ArrayList<Double>> randomPop = generateRandomPopulation(numEdges, populationSize);
        for(int i = 0; i < generations; i++) {
            calcFitnessAllPopulation(numEdges, distanceMatrix, randomPop);
            for (int j = 0; j < populationSize; j++) {
                ArrayList<Double> firstParent = randomPop.get(rouletteWheelSelection(randomPop, populationSize));
                ArrayList<Double> secondParent = randomPop.get(rouletteWheelSelection(randomPop, populationSize));
                int randomNumber = rand.nextInt();
                if (randomNumber <= probCrossover) {
                    //TODO implement CX2 operator to replace first and second parent
                }
            }
        }
        return new ArrayList<>();
    }

    private void calcFitnessAllPopulation(int numEdges, double[][] distanceMatrix, ArrayList<ArrayList<Double>> randomPop) {
        for (ArrayList<Double> tour : randomPop) {
            int startCity = tour.get(1).intValue();
            int lastCity = tour.get(numEdges).intValue();
            double distanceFirstAndLastCity = distanceMatrix[startCity][lastCity];
            double totalDistance = 0;
            for(int k = 0; k < numEdges - 1; k++) {
                totalDistance += distanceMatrix[tour.get(k).intValue()][tour.get(k+1).intValue()];
            }
            tour.add(distanceFirstAndLastCity + totalDistance);
        }
    }

    private ArrayList<ArrayList<Double>> generateRandomPopulation(int numCities, int populationSize) {
        ArrayList<ArrayList<Double>> population = new ArrayList<>();
        ArrayList<Double> tour = new ArrayList<>();
        for(int i = 0; i< populationSize; i++) {
            for(int j = 0; j < numCities; j++) {
                tour.add((double) j);
            }
            Collections.shuffle(tour);
            population.add(tour);
        }
        return population;
    }

    private double[][] getDistanceMatrix(int numEdges, ArrayList<City> randomPopulation) {
        double[][] distanceMatrix = new double[numEdges][numEdges];
        for(int i = 0; i< numEdges; i++) {
            for(int j = 0; j < numEdges; j++) {
                distanceMatrix[i][j] = euclideanDistance(randomPopulation.get(i), randomPopulation.get(j));
            }
        }
        return distanceMatrix;
    }

    private double euclideanDistance(City c1, City c2) {
        return sqrt( pow( ( c1.getX() - c2.getX() ), 2) + pow( ( c1.getY() - c2.getY() ), 2));
    }

    private int rouletteWheelSelection(ArrayList<ArrayList<Double>> fitnessPop, int populationSize) {
        Random rand = new Random();
        int s = 0;
        int partial_s = 0;
        int ind = 0;
        for(ArrayList<Double> tour : fitnessPop) {
            s += tour.get(numCities).intValue();
        }
        int randomNumber = rand.nextInt(s);
        for(ArrayList<Double> tour : fitnessPop) {
            if(partial_s < randomNumber) {
                partial_s += tour.get(numCities).intValue();
                ind++;
            }
        }
        if(ind == populationSize) {
            ind = populationSize-1;
        }
        return ind;
    }
}
