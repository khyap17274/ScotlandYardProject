package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.List;
import java.util.*;

public class DjNode {
    private Integer node;
    private ArrayList<DjEdge> connections;

    public DjNode(int node,ArrayList<DjEdge> cons){
        this.node = node;
        this.connections = cons;
    }

    public Integer getNode(){
        return this.node;
    }

    public ArrayList<DjEdge> getDjEdges(){
        return this.connections;
    }
}
