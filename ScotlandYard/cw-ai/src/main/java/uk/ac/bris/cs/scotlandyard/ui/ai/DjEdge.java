package uk.ac.bris.cs.scotlandyard.ui.ai;

public class DjEdge {
    private DjNode start;
    private DjNode end;
    private int weight;

    public DjEdge(DjNode start, DjNode end, int weight){
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public DjNode getEnd() {
        return end;
    }

    public DjNode getStart() {
        return start;
    }

    public int getWeight() {
        return weight;
    }
}
