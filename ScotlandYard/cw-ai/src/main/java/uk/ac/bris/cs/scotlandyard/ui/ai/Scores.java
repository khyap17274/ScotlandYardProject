package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.ui.ai.*;
import java.util.*;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class Scores {
    //count of how many steps total detectives need to get there
    private int nearbydetectives;
    private int freedomOfMoves;
    private int index;
    private ScotlandYardView view;

    public Scores(Set<Move> moves, ScotlandYardView view, int location, int moveLocation,int index, Boolean isDouble) {
        this.freedomOfMoves = moves.size();
        this.view = view;
        this.nearbydetectives = neardet(view, moveLocation, location);
        this.index = index;

        if(isDouble == true){
            this.nearbydetectives = this.nearbydetectives - 5;
        }
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


    private int neardet(ScotlandYardView view, int validMovePos, int location) {
       // System.out.println("NEARDET, 41");
        DjGraph djikstraGraph = new DjGraph(view);
        ArrayList<DjNode> allnodes = djikstraGraph.getNodes();
        int size = djikstraGraph.getSize();
        int[] shortestPaths = new int[size];
        Boolean minBool[] = new Boolean[size];
        int[][] validNodes = new int[size][size];

        validNodes = transform(allnodes);
        DiSearch(validNodes, location,shortestPaths, minBool,size);

        //System.out.println("NEARDET, 51");

        int count = 0;
        List<Colour> all = view.getPlayers();
        for (Colour player : all) {
            if(!player.equals(Colour.BLACK)){
                //System.out.print(count + " " );
                if((view.getPlayerLocation(player)).orElse(0) == 0) System.exit(-1);
                //System.out.println((view.getPlayerLocation(player)).orElse(0));
                count = count + shortestPaths[(view.getPlayerLocation(player)).orElse(0) - 1];
                //System.out.print(count + " " );
            }
        }
        //System.out.println("NEARDET, 60");
        return count;
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

