
My AI : Vincent Honeybun

Dijkstra's Algorithm
---------------------
This is an implementation of AI using Dijkstra's Algorithm and Minimax Game Tree.

DjNode,DjEdge and DjGraph are the graph of the game (which location is connected to which). This is used to calculate
the shortest distance the detectives can get to Mr.X, in which each edge is being counted as 1. Dijkstra's algorithm
will explore all the possible path and return the shortest path.

Mr.X will move according to the move that will be furthest from all the detectives. Mr.X will also choose a non-Double
move before a double move since double move can be saved to used when it is needed most.

A new visit class is also implemented called AIVisitor. This will Override the current accept method.

The Scores class will be used to determine the score of each move and select the move which has the maxmimum score
calculated by our algorithm.


Minimax Game Tree, BoardState Class and Detective Moves
--------------------------------------------------------
