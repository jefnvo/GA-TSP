import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class TSPSolver {
    int numCities;
    int populationSize;
    int generations;
    double probCrossover;
    double probMutation;
    ArrayList<City> randomPopulation;

    public TSPSolver(int numCities, int populationSize, int generations, ArrayList<City> randomPopulation,
                     double probCrossover, double probMutation) {
        this.numCities = numCities;
        this.populationSize = populationSize;
        this.generations = generations;
        this.randomPopulation = randomPopulation;
        probCrossover = probCrossover;
        probMutation = probMutation;

    }

    public ArrayList<Double> geneticSolver() {
        ArrayList<Integer> bestFitness = new ArrayList<>();
        Random rand = new Random();
        double[][] distanceMatrix = getDistanceMatrix(numCities, randomPopulation);
        ArrayList<ArrayList<Double>> randomPop = generateRandomPopulation(numCities, populationSize);
        for (int i = 0; i < generations; i++) {
            ArrayList<Double> fitnessPopulation = calcFitnessAllPopulation(numCities, distanceMatrix, randomPop);
            for (int j = 0; j < populationSize; j++) {
                int indexFirstParent = rouletteWheelSelection(fitnessPopulation, populationSize);
                int indexSecondParent = rouletteWheelSelection(fitnessPopulation, populationSize);

                ArrayList<Double> firstParent = randomPop.get(indexFirstParent);
                ArrayList<Double> secondParent = randomPop.get(indexSecondParent);
                int randomNumber = rand.nextInt();
                if (randomNumber <= probCrossover) {
                    ArrayList<ArrayList<Double>> offsprings = cx2Operator(firstParent, secondParent);
                    //replace
                    randomPop.set(indexFirstParent, offsprings.get(0));
                    randomPop.set(indexSecondParent, offsprings.get(1));
                    //update fitness
                    fitnessPopulation.set(indexFirstParent, calcFitnessTour(offsprings.get(0), numCities, distanceMatrix));
                    fitnessPopulation.set(indexSecondParent, calcFitnessTour(offsprings.get(1), numCities, distanceMatrix));
                }
            }
            bestFitness.add(getBestIndex(fitnessPopulation));
            System.out.print("The max fitness in this generation is="+Collections.max(fitnessPopulation));
        }
        System.out.print("The best fitness is="+Collections.max(bestFitness));

        return randomPop.get(Collections.max(bestFitness));
    }

    private ArrayList<ArrayList<Double>> cx2Operator(ArrayList<Double> firstParent, ArrayList<Double> secondParent) {
        ArrayList<Double> firstOffspring = new ArrayList<Double>(Collections.nCopies(firstParent.size(), null));
        ArrayList<Double> secondOffspring =new ArrayList<Double>(Collections.nCopies(secondParent.size(), null));;

        int i1 = 0;
        int i2 = 0;

        double initialCity = firstParent.get(0);
        firstOffspring.set(i1, secondParent.get(i1));
        i1++;
        boolean check = true;

        while (i1 < firstParent.size() && i2 < secondParent.size()) {
            int index1 = getCityIndex(firstParent, firstOffspring.get(i1 - 1));
            index1 = getCityIndex(firstParent, secondParent.get(index1));
            double latestUpdated2 = secondParent.get(index1);
            System.out.println("First parent ->"+ Arrays.toString(firstParent.toArray()));
            System.out.println("Second parent ->"+ Arrays.toString(secondParent.toArray()));
            System.out.println("\n\n");
            if (latestUpdated2 == initialCity) {
                secondOffspring.set(i2, latestUpdated2);
                i2++;
                check = false;
                System.out.println("First child ->"+ Arrays.toString(firstOffspring.toArray()));
                System.out.println("Second child ->"+ Arrays.toString(secondOffspring.toArray()));

                ArrayList<Double> remainingCities1 = getUnusedIndex(firstParent, secondOffspring);
                ArrayList<Double> remainingCities2 = getUnusedIndex(secondParent, firstOffspring);
                System.out.println("Remaining from parent 1 and child 2->"+ Arrays.toString(remainingCities1.toArray()));
                System.out.println("Remaining from parent 2 and child 1->"+ Arrays.toString(remainingCities2.toArray()));

                ArrayList<ArrayList<Double>> remaining = cx2Operator(remainingCities1, remainingCities2);

                for(int i = i1, j=0; i < remaining.get(0).size() && j < remaining.get(0).size(); i++, j++) {
                    firstOffspring.set(i, remaining.get(0).get(j));
                }
                for(int i = i1, j=0; i < remaining.get(1).size() && j < remaining.get(1).size(); i++, j++) {
                    secondOffspring.set(i, remaining.get(1).get(j));
                }

                check = false;
                break;
            } else {
                secondOffspring.set(i2, secondParent.get(index1));
                i2++;
                index1 = getCityIndex(firstParent, secondOffspring.get(i2 - 1));
                firstOffspring.set(i1, secondParent.get(index1));
                i1++;
            }
        }
        if (check) {
            int index1 = getCityIndex(firstParent, firstOffspring.get(i1 - 1));
            index1 = getCityIndex(firstParent, secondParent.get(index1));
            Double latestUpdated2 = secondParent.get(index1);
            secondOffspring.set(i2, latestUpdated2);
            i2++;
        }
        ArrayList<ArrayList<Double>> resultArray = new ArrayList<>(2);
        resultArray.add(firstOffspring);
        resultArray.add(secondOffspring);
        return resultArray;

    }

    private int getCityIndex(ArrayList<Double> tour, double city) {
        for (int i = 0; i < tour.size(); i++) {
            if (tour.get(i) != null && tour.get(i) == city) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Double> getUnusedIndex(ArrayList<Double> parent, ArrayList<Double> offspring) {
        ArrayList<Double> unused = new ArrayList<>();
        for (double i : parent) {
            if (getCityIndex(offspring, i) == -1) {
                unused.add(i);
            }
        }
        return unused;
    }

    private ArrayList<Double> calcFitnessAllPopulation(int numEdges,
                                                        double[][] distanceMatrix,
                                                        ArrayList<ArrayList<Double>> randomPop) {
        ArrayList<Double> fitnessPop = new ArrayList<>();
        for (ArrayList<Double> tour : randomPop) {
            fitnessPop.add(calcFitnessTour(tour, numEdges, distanceMatrix));
        }
        return fitnessPop;
    }

    private double calcFitnessTour(ArrayList<Double> tour, int numEdges, double[][] distanceMatrix) {
        int startCity = tour.get(0).intValue();
        int lastCity = tour.get(numEdges - 1).intValue();
        double distanceFirstAndLastCity = distanceMatrix[startCity][lastCity];
        double totalDistance = 0;
        for (int k = 0; k < numEdges - 1; k++) {
            totalDistance += distanceMatrix[tour.get(k).intValue()][tour.get(k + 1).intValue()];
        }
        return  distanceFirstAndLastCity + totalDistance;
    }

    private ArrayList<ArrayList<Double>> generateRandomPopulation(int numCities, int populationSize) {
        ArrayList<ArrayList<Double>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            ArrayList<Double> tour = new ArrayList<>();
            for (int j = 0; j < numCities; j++) {
                tour.add((double) j);
            }
            Collections.shuffle(tour);
            population.add(tour);
        }
        return population;
    }

    private double[][] getDistanceMatrix(int numEdges, ArrayList<City> randomPopulation) {
        double[][] distanceMatrix = new double[numEdges][numEdges];
        for (int i = 0; i < numEdges; i++) {
            for (int j = 0; j < numEdges; j++) {
                distanceMatrix[i][j] = euclideanDistance(randomPopulation.get(i), randomPopulation.get(j));
            }
        }
        return distanceMatrix;
    }

    private double euclideanDistance(City c1, City c2) {
        return sqrt(pow((c1.getX() - c2.getX()), 2) + pow((c1.getY() - c2.getY()), 2));
    }

    private int rouletteWheelSelection(ArrayList<Double> fitnessPop, int populationSize) {
        Random rand = new Random();
        int s = 0;
        int partial_s = 0;
        int ind = 0;
        for (Double i : fitnessPop) {
            s += i.intValue();
        }
        int randomNumber = rand.nextInt(s);
        for (Double i : fitnessPop) {
            if (partial_s < randomNumber) {
                partial_s += i.intValue();
                ind++;
            }
        }
        if (ind == populationSize) {
            ind = populationSize - 1;
        }
        return ind;
    }
    private int getBestIndex(ArrayList<Double> fitnessPop) {
        int maxAt = 0;

        for (int i = 0; i < fitnessPop.size(); i++) {
            maxAt = fitnessPop.get(i) > fitnessPop.get(maxAt) ? i : maxAt;
        }
        return maxAt;
    }
}
