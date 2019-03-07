package uk.ac.bris.cs.scotlandyard.ui.ai;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import uk.ac.bris.cs.scotlandyard.model.*;
import java.util.*;
import java.util.function.Consumer;
import uk.ac.bris.cs.scotlandyard.ui.ai.*;

public class MrXMoves extends ValidMoves{
    private DjkSearch shortestPath;
    private ScotlandYardPlayer mrX;
    private Player player;
    private int MovedToLocate;
    private int score;

    public MrXMoves(ScotlandYardView view,  int MrX_location, HashMap<Ticket, Integer> tickets ,int[] detective_locations){

        this.mrX = new ScotlandYardPlayer(player,Colour.BLACK,MrX_location,tickets);
        this.validMoves = getvalidMoves(this.mrX);

        this.shortestPath = new DjkSearch(this.validMoves,view,MrX_location,detective_locations);
        this.MovedToLocate =
        this.score = this.shortestPath.getNearbydetectives();
    }

}
