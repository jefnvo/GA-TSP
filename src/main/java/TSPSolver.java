import java.util.ArrayList;
import java.util.Collections;
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
//                ArrayList<Double> firstParent = new ArrayList<>(Arrays.asList(0.0, 5.0, 4.0, 3.0, 6.0, 1.0, 2.0));
//                ArrayList<Double> secondParent = new ArrayList<>(Arrays.asList(2.0, 5.0, 6.0, 4.0, 0.0, 3.0, 1.0));
                int randomNumber = rand.nextInt();
                if (randomNumber <= probCrossover) {
                    ArrayList<ArrayList<Double>> offsprings = cx2Operator(firstParent, secondParent);
                    //replace
                    double fitnessFirstOffspring = calcFitnessTour(offsprings.get(0), numCities, distanceMatrix);
                    double fitnessSecondOffspring = calcFitnessTour(offsprings.get(1), numCities, distanceMatrix);
                    if(fitnessFirstOffspring < fitnessPopulation.get(indexFirstParent)) {
                        randomPop.set(indexFirstParent, offsprings.get(0));
                        fitnessPopulation.set(indexFirstParent, calcFitnessTour(offsprings.get(0), numCities, distanceMatrix));
                    }
                    if(fitnessSecondOffspring < fitnessPopulation.get(indexSecondParent)) {
                        randomPop.set(indexSecondParent, offsprings.get(1));
                        fitnessPopulation.set(indexSecondParent, calcFitnessTour(offsprings.get(1), numCities, distanceMatrix));
                    }
                }
            }
            bestFitness.add(getBestIndex(fitnessPopulation));
            System.out.print("The max fitness in this generation is="+Collections.max(fitnessPopulation)+"\n");
        }
        System.out.print("The best fitness is="+Collections.max(bestFitness)+"\n");

        return randomPop.get(Collections.max(bestFitness));
    }

    private ArrayList<ArrayList<Double>> cx2Operator(ArrayList<Double> firstParent, ArrayList<Double> secondParent) {
        System.out.println("First parent ->"+ firstParent);
        System.out.println("Second parent ->"+ secondParent);

        ArrayList<Double> firstOffspring = new ArrayList<>();
        ArrayList<Double> secondOffspring = new ArrayList<>();

        while(firstOffspring.size() != numCities && secondOffspring.size()!= numCities) {
            cx2OperatorCycle(firstParent, firstOffspring, secondParent, secondOffspring);
            final Boolean[] childDiverge = {false};
            firstOffspring.forEach(it -> {
                if(!secondOffspring.contains(it)) {
                    childDiverge[0] = true;
                }
            });

            if(firstOffspring.size()  < numCities || secondOffspring.size() < numCities) {
                ArrayList<Double> newFirstParent = new ArrayList<>();
                ArrayList<Double> newSecondParent = new ArrayList<>();
                firstParent.forEach( it -> {
                    if(!firstOffspring.contains(it)) {
                        newFirstParent.add(it);
                    }
                });

                secondParent.forEach( it -> {
                    if(!secondOffspring.contains(it)) {
                        newSecondParent.add(it);
                    }
                });

                firstParent = newFirstParent;
                secondParent = newSecondParent;

                if(firstParent.size() == 3 || secondParent.size() == 3) {
                    firstOffspring.addAll(firstParent);
                    secondOffspring.addAll(secondParent);
                } else if(childDiverge[0]) {
                    firstOffspring.addAll(firstParent);
                    secondOffspring.addAll(secondParent);
                }
            }
        }
        System.out.println("First Offspring = "+ firstOffspring  +"\nSecond Offspring = "+ secondOffspring);
        ArrayList<ArrayList<Double>> result = new ArrayList<>();
        result.add(firstOffspring);
        result.add(secondOffspring);
        return result;
    }

    private void cx2OperatorCycle(ArrayList<Double> firstParent, ArrayList<Double> firstOffspring,
                                              ArrayList<Double> secondParent, ArrayList<Double> secondOffspring) {
        //step 2
        Double bit = secondParent.get(0);
        firstOffspring.add(bit);
        do {
            //step 3
            int indexBit = getCityIndex(firstParent, bit);
            bit = secondParent.get(indexBit);
            indexBit = getCityIndex(firstParent, bit);
            bit = secondParent.get(indexBit);
            if(!secondOffspring.contains(bit)){
                secondOffspring.add(bit);
            }


            //step 4
            indexBit = getCityIndex(firstParent, bit);
            bit = secondParent.get(indexBit);
            if(!firstOffspring.contains(bit)) {
                firstOffspring.add(bit);
            }

        } while(!secondOffspring.contains(firstParent.get(0)));//step 5

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
