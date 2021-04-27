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
    Long[][] distanceWeight;

    public TSPSolver(int numCities, int populationSize, int generations, Long[][] distanceWeight, double probCrossover, double probMutation) {
        this.numCities = numCities;
        this.populationSize = populationSize;
        this.generations = generations;
        this.distanceWeight = distanceWeight;
        this.probCrossover = probCrossover;
        this.probMutation = probMutation;

    }

    public ArrayList<Long> geneticSolver() {
        ArrayList<Integer> bestTour = new ArrayList<>();
        ArrayList<Long> bestFitness = new ArrayList<>();

        Random rand = new Random();
        ArrayList<ArrayList<Long>> randomPop = generateRandomPopulation(numCities, populationSize);
        for (int i = 0; i < generations; i++) {
            ArrayList<Long> fitnessPopulation = calcFitnessAllPopulation(numCities, distanceWeight, randomPop);
            for (int j = 0; j < populationSize; j++) {
                int indexFirstParent = rouletteWheelSelection(fitnessPopulation, populationSize);
                int indexSecondParent = rouletteWheelSelection(fitnessPopulation, populationSize);

                ArrayList<Long> firstParent = randomPop.get(indexFirstParent);
                ArrayList<Long> secondParent = randomPop.get(indexSecondParent);

                double randomNumber = Math.random();
                if (randomNumber <= probCrossover) {
                    ArrayList<ArrayList<Long>> offsprings = cx2Operator(firstParent, secondParent);
                    //replace
                    double fitnessFirstOffspring = calcFitnessTour(offsprings.get(0), numCities, distanceWeight);
                    double fitnessSecondOffspring = calcFitnessTour(offsprings.get(1), numCities, distanceWeight);
                    if(fitnessFirstOffspring < fitnessPopulation.get(indexFirstParent)) {
                        randomPop.set(indexFirstParent, offsprings.get(0));
                        fitnessPopulation.set(indexFirstParent, calcFitnessTour(offsprings.get(0), numCities, distanceWeight));
                    }
                    if(fitnessSecondOffspring < fitnessPopulation.get(indexSecondParent)) {
                        randomPop.set(indexSecondParent, offsprings.get(1));
                        fitnessPopulation.set(indexSecondParent, calcFitnessTour(offsprings.get(1), numCities, distanceWeight));
                    }

                }
                randomNumber = Math.random();
                if(randomNumber <= probMutation) {
                    int index = rouletteWheelSelection(fitnessPopulation, populationSize);
                    ArrayList<Long> mutated = mutation(index, randomPop);
                    randomPop.set(index, mutated);
                    fitnessPopulation.set(index, calcFitnessTour(randomPop.get(index), numCities, distanceWeight));
                }

            }
            bestTour.add(getBestIndex(fitnessPopulation));
            bestFitness.add(Collections.min(fitnessPopulation));
            System.out.print("The best fitness in this generation is="+Collections.min(fitnessPopulation)+"\n");
        }
        System.out.print("The best fitness is="+Collections.min(bestFitness)+"\n");

        return randomPop.get(Collections.max(bestTour));
    }

    private ArrayList<Long> mutation(int index, ArrayList<ArrayList<Long>> pop) {
        final int[] swapArray = new Random().ints(0, numCities).distinct().limit(2).toArray();

        Long temp = pop.get(index).get(swapArray[0]);
        pop.get(index).set(swapArray[0], pop.get(index).get(swapArray[1]));
        pop.get(index).set(swapArray[1], temp);

        return pop.get(index);
    }

    private ArrayList<ArrayList<Long>> cx2Operator(ArrayList<Long> firstParent, ArrayList<Long> secondParent) {

        ArrayList<Long> firstOffspring = new ArrayList<>();
        ArrayList<Long> secondOffspring = new ArrayList<>();

        while(firstOffspring.size() != numCities && secondOffspring.size()!= numCities) {
            cx2OperatorCycle(firstParent, firstOffspring, secondParent, secondOffspring);
            final Boolean[] childDiverge = {false};
            firstOffspring.forEach(it -> {
                if(!secondOffspring.contains(it)) {
                    childDiverge[0] = true;
                }
            });

            if(firstOffspring.size()  < numCities || secondOffspring.size() < numCities) {
                ArrayList<Long> newFirstParent = new ArrayList<>();
                ArrayList<Long> newSecondParent = new ArrayList<>();
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
        ArrayList<ArrayList<Long>> result = new ArrayList<>();
        result.add(firstOffspring);
        result.add(secondOffspring);
        return result;
    }

    private void cx2OperatorCycle(ArrayList<Long> firstParent, ArrayList<Long> firstOffspring,
                                              ArrayList<Long> secondParent, ArrayList<Long> secondOffspring) {
        //step 2
        Long bit = secondParent.get(0);
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
    private int getCityIndex(ArrayList<Long> tour, double city) {
        for (int i = 0; i < tour.size(); i++) {
            if (tour.get(i) != null && tour.get(i) == city) {
                return i;
            }
        }
        return -1;
    }


    private ArrayList<Long> calcFitnessAllPopulation(int numEdges,
                                                        Long[][] distanceWeight,
                                                        ArrayList<ArrayList<Long>> randomPop) {
        ArrayList<Long> fitnessPop = new ArrayList<>();
        for (ArrayList<Long> tour : randomPop) {
            fitnessPop.add(calcFitnessTour(tour, numEdges, distanceWeight));
        }
        return fitnessPop;
    }

    private long calcFitnessTour(ArrayList<Long> tour, int numEdges, Long[][] distanceWeight) {
        int startCity = tour.get(0).intValue();
        int lastCity = tour.get(numEdges - 1).intValue();
        long distanceFirstAndLastCity = distanceWeight[startCity][lastCity];
        long totalDistance = 0;
        for (int k = 0; k < numEdges - 1; k++) {
            totalDistance += distanceWeight[tour.get(k).intValue()][tour.get(k + 1).intValue()];
        }
        return  distanceFirstAndLastCity + totalDistance;
    }

    private ArrayList<ArrayList<Long>> generateRandomPopulation(int numCities, int populationSize) {
        ArrayList<ArrayList<Long>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            ArrayList<Long> tour = new ArrayList<>();
            for (int j = 0; j < numCities; j++) {
                tour.add((long) j);
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

    private int rouletteWheelSelection(ArrayList<Long> fitnessPop, int populationSize) {
        Random rand = new Random();
        int s = 0;
        int partial_s = 0;
        int ind = 0;
        for (Long i : fitnessPop) {
            s += i.intValue();
        }
        int randomNumber = rand.nextInt(s);
        for (Long i : fitnessPop) {
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
    private int getBestIndex(ArrayList<Long> fitnessPop) {
        int maxAt = 0;

        for (int i = 0; i < fitnessPop.size(); i++) {
            maxAt = fitnessPop.get(i) > fitnessPop.get(maxAt) ? i : maxAt;
        }
        return maxAt;
    }
}
