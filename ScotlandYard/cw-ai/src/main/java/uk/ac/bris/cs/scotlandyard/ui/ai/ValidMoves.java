package uk.ac.bris.cs.scotlandyard.ui.ai;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import uk.ac.bris.cs.scotlandyard.model.*;
import java.util.*;
import java.util.function.Consumer;
import uk.ac.bris.cs.scotlandyard.ui.ai.*;


import static uk.ac.bris.cs.scotlandyard.model.Colour.BLACK;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.DOUBLE;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.SECRET;

public class ValidMoves {
    //stores information of the validmoves of each player
    public Set<Move> validMoves;
    public List<ScotlandYardPlayer> detectives;
    public Graph<Integer, Transport> graph;
    public List<Boolean> rounds;
    public int[] detectiveLocations;
    public int currentRound;
    public int score;


    public Set<Move> getvalidMoves(ScotlandYardPlayer player) {
        Collection<Edge<Integer,Transport>> edges = graph.getEdgesFrom(graph.getNode(player.location()));
        Set<Move> validMovesTemp = new HashSet<>();

        for(Edge<Integer,Transport> edge : edges) {
            Ticket myTicket = Ticket.fromTransport(edge.data());
            int destination = edge.destination().value();

            boolean detectivesAtDestination = false;
            for(int i = 0; i < detectiveLocations.length; i++){
                if(detectiveLocations[i] == destination) detectivesAtDestination = true;
            }

            if (player.hasTickets(myTicket) && !detectivesAtDestination) {
                validMovesTemp.add(new TicketMove(player.colour(), myTicket, destination));
            }
            if (player.hasTickets(SECRET) && !detectivesAtDestination) {
                validMovesTemp.add(new TicketMove(BLACK, SECRET, destination));
            }


            if (player.hasTickets(DOUBLE)
                    && (rounds.size() - currentRound > 1)
                    && !detectivesAtDestination) {
                Collection<Edge<Integer, Transport>> secondEdges = graph.getEdgesTo(graph.getNode(destination));
                for (Edge<Integer, Transport> edge2 : secondEdges) {
                    Ticket myTicket2 = Ticket.fromTransport(edge2.data());
                    int destination2 = edge2.destination().value();

                    boolean detectivesAtDestination2 = false;
                    for(int i = 0; i < detectiveLocations.length; i++){
                        if(detectiveLocations[i] == destination2) detectivesAtDestination = true;
                    }

                    if (!detectivesAtDestination2) {
                        if(player.hasTickets(SECRET)) {
                            TicketMove ticketMove1 = new TicketMove(BLACK, SECRET, destination);
                            TicketMove ticketMove2 = new TicketMove(BLACK, SECRET, destination2);
                            validMovesTemp.add(new DoubleMove(BLACK, ticketMove1, ticketMove2));
                        }
                        if(player.hasTickets(SECRET) && player.hasTickets(myTicket)){
                            TicketMove ticketMove1 = new TicketMove(BLACK, myTicket, destination);
                            TicketMove ticketMove2 = new TicketMove(BLACK, SECRET, destination2);
                            validMovesTemp.add(new DoubleMove(BLACK, ticketMove1, ticketMove2));
                        }
                        if(player.hasTickets(SECRET) && player.hasTickets(myTicket2)){
                            TicketMove ticketMove1 = new TicketMove(BLACK, SECRET, destination);
                            TicketMove ticketMove2 = new TicketMove(BLACK, myTicket2, destination2);
                            validMovesTemp.add(new DoubleMove(BLACK, ticketMove1, ticketMove2));
                        }
                        if(myTicket.equals(myTicket2) && player.tickets().get(myTicket) > 1) {
                            TicketMove ticketMove1 = new TicketMove(BLACK, myTicket, destination);
                            TicketMove ticketMove2 = new TicketMove(BLACK, myTicket2, destination2);
                            validMovesTemp.add(new DoubleMove(BLACK, ticketMove1, ticketMove2));
                        }
                        if(player.hasTickets(myTicket2) && player.hasTickets(myTicket)
                                && !myTicket.equals(myTicket2)) {
                            TicketMove ticketMove1 = new TicketMove(BLACK, myTicket, destination);
                            TicketMove ticketMove2 = new TicketMove(BLACK, myTicket2, destination2);
                            validMovesTemp.add(new DoubleMove(BLACK, ticketMove1, ticketMove2));
                        }
                    }
                }
            }
        }
        if(validMovesTemp.isEmpty() ) {
            validMovesTemp.add(new PassMove(player.colour()));
        }

        return validMovesTemp;
    }


}
