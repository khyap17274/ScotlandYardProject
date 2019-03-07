package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.scotlandyard.model.MoveVisitor;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import static uk.ac.bris.cs.scotlandyard.model.Colour.BLACK;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.DOUBLE;

import uk.ac.bris.cs.scotlandyard.model.*;
import java.util.*;
import java.util.function.Consumer;


public class AIVisitor implements MoveVisitor {

    private int destination;
    private Optional<Ticket> ticket;
    private boolean isDouble;

   //doesn't make sense to make since Mr.X will more likely to always
  //have a possible alternative move

    public int getDestionation(){
        return destination;
    }

    public Optional<Ticket> getTicket(){
        return ticket;
    }

    public boolean getisDouble() {
        return isDouble;
    }


    @Override
    public void visit(TicketMove move){
        this.isDouble = false;
        //System.out.println("AiVisitor, 2");
        ticket = Optional.of(move.ticket());
        destination = move.destination();
    }

    @Override
    public void visit(DoubleMove move){
        this.isDouble = true;
        //System.out.println("AiVisitor, 3");
        destination = move.finalDestination();
        ticket = Optional.of(move.secondMove().ticket());
    }
}
