package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.List;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.scotlandyard.model.*;
import java.util.*;
import java.util.function.Consumer;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;

import uk.ac.bris.cs.scotlandyard.ui.ai.DjEdge;
import uk.ac.bris.cs.scotlandyard.ui.ai.DjNode;


public class DjGraph {

    private ArrayList<DjNode> nodes;
    private int size;

    public DjGraph(ScotlandYardView view){
        //edges and graph of the game
        Graph<Integer,Transport> currentGraph = view.getGraph();
        Collection<Edge<Integer,Transport>> edges = currentGraph.getEdges();
        //all nodes in the game graph
        ArrayList<DjNode> allnodes = new ArrayList<DjNode>();
        //System.out.println("DjGraph, 1");
        //iterate through all edges to get all connections on the graph
        for(Edge<Integer,Transport> iterate : edges){

            ArrayList<DjEdge> djedges = new ArrayList<DjEdge>();
            DjNode temp = new DjNode(iterate.source().value(),djedges);
            //does the current node exist in the current allnodes for our new djikstra graph
            boolean exist = false;

            //if there already exists a source node for this
            for(DjNode i : allnodes){
                if(i.getNode().equals(iterate.source().value())){
                    exist = true;
                    djedges = i.getDjEdges();
                    for(DjEdge j : djedges){
                        if(j.getStart().getNode().equals(iterate.source().value())
                                && j.getEnd().getNode().equals(iterate.destination().value())) {

                        }
                        else{
                            ArrayList<DjEdge> mostlikelynull = new ArrayList<DjEdge>();
                            DjNode adding1 = new DjNode(iterate.destination().value(),mostlikelynull);
                            DjEdge adding = new DjEdge(j.getStart(),adding1,0);
                        }
                    }
                }
            }

            if(exist == false){
                allnodes.add(temp);
            }


        }

        this.nodes = allnodes;
        this.size = allnodes.size();
    }

    public ArrayList<DjNode> getNodes() {
        return nodes;
    }

    public int getSize() {
        return size;
    }
}

