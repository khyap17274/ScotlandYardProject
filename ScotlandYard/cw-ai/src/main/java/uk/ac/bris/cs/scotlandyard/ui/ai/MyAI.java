package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.scotlandyard.ai.ManagedAI;
import uk.ac.bris.cs.scotlandyard.ai.PlayerFactory;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.ui.ai.AIVisitor;
import uk.ac.bris.cs.scotlandyard.ui.ai.Scores;
import uk.ac.bris.cs.scotlandyard.ui.ai.*;

import java.util.*;
import java.util.function.Consumer;

// TODO name the AI
@ManagedAI("Vincent Honeybun")
//capable of returning object and creating object upon request
public class MyAI implements PlayerFactory {

	// TODO create a new player here
	@Override
	public Player createPlayer(Colour colour) {
		return new MyPlayer();
	}

	// TODO A sample player that selects a random move
	private static class MyPlayer implements Player {

		private final Random random = new Random();

		@Override
		public void makeMove(ScotlandYardView view, int location, Set<Move> moves,
							 Consumer<Move> callback) {
			//create a set of scores (Linked List)
			//lists of information for each move
			ArrayList<AIVisitor> visitors = new ArrayList<AIVisitor>();
			ArrayList<Scores> scoreCollection = new ArrayList<Scores>();
			DjGraph djikstraGraph = new DjGraph(view);
			int index = 0;

			int[] MrXMoves = new int[moves.size()];
			Scores head = new Scores(moves,view,0,0,0,false);
			//adding into scores
			int j = 0;
			for(Move move : moves){
				AIVisitor temp = new AIVisitor();
				visitors.add(temp);
				move.visit(temp);

				//MrXTickets.add(temp.getTicket());
				MrXMoves[j] = temp.getDestionation();
				j++;
				Scores score = new Scores(moves,view,location,temp.getDestionation(),index,temp.getisDouble());
				scoreCollection.add(score);
				index++;
			}

			HashMap<Ticket,Integer> MrX_tickets = new HashMap<Ticket,Integer>();
			MrX_tickets.put(Ticket.DOUBLE,view.getPlayerTickets(Colour.BLACK,Ticket.DOUBLE).orElse(0));
			MrX_tickets.put(Ticket.BUS,view.getPlayerTickets(Colour.BLACK,Ticket.BUS).orElse(0));
			MrX_tickets.put(Ticket.TAXI,view.getPlayerTickets(Colour.BLACK,Ticket.TAXI).orElse(0));
			MrX_tickets.put(Ticket.SECRET,view.getPlayerTickets(Colour.BLACK,Ticket.SECRET).orElse(0));
			MrX_tickets.put(Ticket.UNDERGROUND,view.getPlayerTickets(Colour.BLACK,Ticket.UNDERGROUND).orElse(0));

			ArrayList<HashMap<Ticket,Integer>> detectives = new ArrayList<>();
			for(Colour colour : view.getPlayers()){
				HashMap<Ticket,Integer> temp = new HashMap<Ticket,Integer>();
				temp.put(Ticket.BUS,view.getPlayerTickets(colour,Ticket.BUS).orElse(0));
				temp.put(Ticket.UNDERGROUND,view.getPlayerTickets(colour,Ticket.UNDERGROUND).orElse(0));
				temp.put(Ticket.TAXI,view.getPlayerTickets(colour,Ticket.TAXI).orElse(0));
			}


			int[] detective_locations = new int[view.getPlayers().size()];
			int count1 = 0;
			for(Colour colour : view.getPlayers()){
				detective_locations[count1] = (view.getPlayerLocation(colour).orElse(0));
				count1++;
			}

			//a Minimax Tree of BoardState with a score that minimax algorithm can evaluate


			int max = 0;
			int chosenMove = 0;
			for(Scores i : scoreCollection){
				if(i.getNearbydetectives() > max ){
					head = i;
					chosenMove = i.getIndex();
				}
			}

			int count = 0;
			for(Move i : moves){
				if(count == chosenMove) callback.accept(i);
				count++;
			}


		}

		/* public int FindMax(int depth, boolean isMax, int scores[], int height, BoardState root){

			if(depth == height)
				return 0;
			//find Maximum node


		} */
	}
}