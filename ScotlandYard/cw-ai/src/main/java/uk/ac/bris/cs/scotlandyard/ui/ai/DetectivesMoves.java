package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.scotlandyard.model.*;
import java.util.*;


import java.util.Map;


public class DetectivesMoves extends ValidMoves {
    private DjkSearch shortestPath;
    private ScotlandYardPlayer detective;
    private Player player;
    private  int score;

    //score for each detective
    public DetectivesMoves(ScotlandYardView view, int location, HashMap<Ticket, Integer> tickets,
                           Colour colour,int MrX_location,int current_round) {

        this.detective = new ScotlandYardPlayer(this.player,colour,location,tickets);
        this.validMoves = getvalidMoves(this.detective);

        int black_node = (view.getPlayerLocation(Colour.BLACK)).orElse(0);
        if(black_node == 0) System.exit(-1);
        int[] MrX_current=  {black_node};

        this.shortestPath = new DjkSearch(this.validMoves,view,location,MrX_current);
        this.score = this.shortestPath.getNearbydetectives();
    }

}
