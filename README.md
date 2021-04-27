Implementing the CX2 operator proposed in this article:
  - Genetic Algorithm for Traveling Salesman Problem with Modified Cycle Crossover Operator

Extra:
  - This code have a lot of sections that can be optimized, so will be nice improve this sections

The CX2 operator have some issues, for some instances the algorithm crashes, example:
  - parent1: 6 3 4 5 0 1 2
  - parent2: 2 5 6 0 1 3 4


  - parent1: 2 5 3 6 0 4 1
  - parent2: 5 2 3 0 1 4 6

So I implement the CX2 operator with some adapts
