import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class TSPSolver {
    int numEdges;
    int populationSize;
    int generations;
    ArrayList<City> randomPopulation;
    public TSPSolver(int numEdges, int populationSize, int generations, ArrayList<City> randomPopulation ){
        this.numEdges = numEdges;
        this.populationSize = populationSize;
        this.generations =  generations;
        this.randomPopulation = randomPopulation;

    }

    public ArrayList<City> geneticSolver(int numEdges, int populationSize, int generations, ArrayList<City> randomPopulation) {
        double [][] distanceMatrix =  getDistanceMatrix(numEdges, randomPopulation);
        ArrayList<ArrayList<Integer>> randomPop = generateRandomPopulation(numEdges, populationSize);
        for(int i = 0; i < generations; i++) {
            HashMap<ArrayList<Integer>, Double> fitnessPop = calcFitnessAllPopulation(numEdges, populationSize, distanceMatrix, randomPop);
        }
        return new ArrayList<>();
    }

    private HashMap<ArrayList<Integer>, Double> calcFitnessAllPopulation(int numEdges, int populationSize, double[][] distanceMatrix, ArrayList<ArrayList<Integer>> randomPop) {
        HashMap<ArrayList<Integer>, Double> fitnessPop = new HashMap<>();
        for(int j = 0; j < populationSize; j++) {
            int startCity = randomPop.get(j).get(1);
            int lastCity = randomPop.get(j).get(numEdges);
            double distanceFirstAndLastCity = distanceMatrix[startCity][lastCity];
            double totalDistance = 0;
            for(int k = 0; k < numEdges -1; k++) {
                totalDistance += distanceMatrix[randomPop.get(j).get(k)][randomPop.get(j).get(k+1)];
            }
            fitnessPop.put(randomPop.get(j), distanceFirstAndLastCity + totalDistance);
        }
        return fitnessPop;
    }

    private ArrayList<ArrayList<Integer>> generateRandomPopulation(int numCities, int populationSize) {
        ArrayList<ArrayList<Integer>> population = new ArrayList<>();
        ArrayList<Integer> tour = new ArrayList<>();
        for(int i = 0; i< populationSize; i++) {
            for(int j = 0; j < numCities; j++) {
                tour.add(j);
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
}
