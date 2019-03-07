package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.scotlandyard.model.ScotlandYardView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import uk.ac.bris.cs.scotlandyard.ui.ai.*;
import uk.ac.bris.cs.scotlandyard.model.*;
import java.util.*;



public class BoardState {

    private Boolean mrX_turn;
    private ValidMoves MoveScore;
    private ScotlandYardView view;
    private ArrayList<DetectiveInfo> detectives;
    private int currentRound;
    private int mrX_currentLocation;

    public BoardState(ScotlandYardView view,int[] detectives_location,Boolean[] rounds,
                      int currentRound,int mrX_currentLocation,Boolean mrX_turn, HashMap<Ticket, Integer> mrX_tickets){

        if(mrX_turn == false) this.mrX_turn = true;
        else this.mrX_turn = true;
        this.view = view;
        this.mrX_currentLocation = mrX_currentLocation;
        this.currentRound = currentRound;
        this.detectives = new ArrayList<DetectiveInfo>();
        int i = 0;
        for(Colour temp : view.getPlayers()){
            DetectiveInfo current = new DetectiveInfo(temp,detectives_location[i]);
            this.detectives.add(current);
            i++;
        }


        //TO-DO if loop to see whose turn it is
        if(mrX_turn == true){
            this.MoveScore = new MrXMoves(view,mrX_currentLocation,mrX_tickets,detectives_location);
        }

    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getMrX_currentLocation() {
        return mrX_currentLocation;
    }

    public ScotlandYardView getView() {
        return view;
    }

    public ArrayList<DetectiveInfo> getDetectives() {
        return detectives;
    }
}
