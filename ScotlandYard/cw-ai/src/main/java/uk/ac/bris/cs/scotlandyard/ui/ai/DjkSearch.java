package uk.ac.bris.cs.scotlandyard.ui.ai;

//package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.ui.ai.*;
import java.util.*;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class DjkSearch {
    //count of how many steps total detectives need to get there
    private int nearbydetectives;
    private int freedomOfMoves;
    private int index;
    private ScotlandYardView view;

    public DjkSearch(Set<Move> moves, ScotlandYardView view, int current_location, int[] goal_locations) {
        this.freedomOfMoves = moves.size();
        this.view = view;
        this.nearbydetectives = neardet(view, current_location, goal_locations);
        this.index = index;

    }

    public int getFreedomOfMoves() {
        return freedomOfMoves;
    }

    public int getIndex() {
        return index;
    }

    public int getNearbydetectives() {
        return nearbydetectives;
    }


    private int neardet(ScotlandYardView view, int location, int[] goal_locations) {

        DjGraph djikstraGraph = new DjGraph(view);
        ArrayList<DjNode> allnodes = djikstraGraph.getNodes();
        int size = djikstraGraph.getSize();

        int[] shortestPaths = new int[size];
        Boolean minBool[] = new Boolean[size];
        int[][] validNodes = new int[size][size];

        int sum = 0;

        validNodes = transform(allnodes);
        DiSearch(validNodes, location,shortestPaths, minBool,size);

        for(int i =0; i < goal_locations.length; i++){
            sum = sum + shortestPaths[goal_locations[i]];
        }
        return sum;
    }

    //transform an array of nodes to be 1
    public int[][] transform(ArrayList<DjNode> allnodes){

        int size = allnodes.size();
        int[][] validNodes = new int[size][size];

        //initialize all to be zero
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                validNodes[i][j] = 0 ;
            }
        }

        //set array to be validNodes[start][end] to be 1
        for(DjNode i : allnodes){
            for(DjEdge temp : i.getDjEdges()){
                validNodes[temp.getStart().getNode()][temp.getEnd().getNode()] = 1;
            }
        }
        return validNodes;
    }

    //Dijkstra's Algorithm to find the longest paths to the detectives
    // *********************************************************************************
    //Example Code and Theory from
    //  Works Cited : 1) https://www.geeksforgeeks.org/greedy-algorithms-set-6-dijkstras-shortest-path-algorithm/
    //  **********************************************************************************
    private void DiSearch(int[][] validNodes, int start,int[] paths, Boolean[] minBool,int size) {

        for (int i = 0; i < size; i++) {
            paths[i] = Integer.MIN_VALUE;
            minBool[i] = false;
        }


        int temp = minDistance(paths,minBool,size);

        minBool[temp] = true;
        paths[start] = 0;


        for(int count = 0; count < size; count++) {
            for (int j =0;j<size ;j++){

                if (!minBool[j] && validNodes[temp][j] != 0 && paths[j] != Integer.MAX_VALUE
                        && paths[temp] + 1 > paths[j])
                    paths[j] = paths[temp] + 1;
            }
        }

    }

    int minDistance(int dist[], Boolean minBool[], int size)
    {
        // Initialize min value
        int max = Integer.MIN_VALUE, max_index=-1;

        for (int v = 0; v < size; v++)
            if (minBool[v] == false && dist[v] >= max)
            {
                max = dist[v];
                max_index = v;
            }

        return max_index;
    }

}


