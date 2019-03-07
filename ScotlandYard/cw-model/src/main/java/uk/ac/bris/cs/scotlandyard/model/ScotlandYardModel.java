package uk.ac.bris.cs.scotlandyard.model;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.BLACK;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.DOUBLE;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.SECRET;

// TODO implement all methods and pass all tests
public class ScotlandYardModel implements ScotlandYardGame,Consumer<Move>{

	private List<Boolean> rounds;
	private final Graph<Integer, Transport> graph;
	private ArrayList<ScotlandYardPlayer> allPlayers;
	private ScotlandYardPlayer mrxPlayer;
	private ArrayList<ScotlandYardPlayer> allDetectives;
	private ScotlandYardPlayer currentPlayer;
	private int currentRound;
	private final List<Spectator> spectators;
	private int lastRevealedLocation;

	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
							 PlayerConfiguration mrX, PlayerConfiguration firstDetective,
							 PlayerConfiguration... restOfTheDetectives) {
		// TODO
		this.graph = requireNonNull(graph);
		this.rounds = requireNonNull(rounds);
		if (rounds.isEmpty()) {
			throw new IllegalArgumentException("Empty rounds");
		}
		if (graph.isEmpty()) {
			throw new IllegalArgumentException("Empty graph");
		}
		if (mrX.colour != BLACK) { // or mrX.colour.isDetective()
			throw new IllegalArgumentException("MrX should be Black");
		}

		ArrayList<PlayerConfiguration> configurations = new ArrayList<>();
		for (PlayerConfiguration configuration : restOfTheDetectives)
			configurations.add(requireNonNull(configuration));
		configurations.add(0, requireNonNull(firstDetective));
		configurations.add(0, requireNonNull(mrX));

		/* insert the player's position 1 by 1 into the set and check if the new position overlaps with
		the existing position in the set */
		Set<Integer> set = new HashSet<>();
		for (PlayerConfiguration configuration : configurations) {
			if (set.contains(configuration.location))
				throw new IllegalArgumentException("Duplicate location");
			set.add(configuration.location);
		}
		/* insert the player's colour 1 by 1 into the set and check if the new colour overlaps with
		the existing colour in the set */
		Set<Colour> set2 = new HashSet<>();
		for (PlayerConfiguration configuration : configurations) {
			if (set2.contains(configuration.colour))
				throw new IllegalArgumentException("Duplicate colour");
			set2.add(configuration.colour);
		}

		this.allPlayers = new ArrayList<>();
		for(PlayerConfiguration x : configurations){
			allPlayers.add(new ScotlandYardPlayer(x.player,x.colour,x.location,x.tickets));
		}

		for(ScotlandYardPlayer player : allPlayers){
			for(Ticket t : Ticket.values()) {
				if (!player.tickets().containsKey(t)) {
					throw new IllegalArgumentException("Detective/MrX is missing ticket");
				}
			}
			if(player.isDetective()) {
				if (player.hasTickets(DOUBLE)) throw new IllegalArgumentException("Detective has DOUBLE ticket.");
				if (player.hasTickets(SECRET)) throw new IllegalArgumentException("Detective has SECRET ticket.");
			}
		}

		this.currentRound = NOT_STARTED;
		this.currentPlayer = allPlayers.get(0);
		this.mrxPlayer = allPlayers.get(0);
		//list of the detectives with mrX removed
		this.allDetectives = new ArrayList<>(allPlayers);
		allDetectives.remove(0);
		this.spectators = new ArrayList<>();
	}

	@Override
	public void registerSpectator(Spectator spectator) {
		requireNonNull(spectator);
		if(spectators.contains(spectator)){
			throw new IllegalArgumentException("the spectator is registered twice");
		}
		else
			spectators.add(spectator);
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		requireNonNull(spectator);
		if(spectators.contains(spectator)) spectators.remove(spectator);
		else throw new IllegalArgumentException("Unregistered Illegal Spectators");
	}

	@Override
	public Collection<Spectator> getSpectators() {
		return Collections.unmodifiableCollection(spectators);
	}

	//Only return true if all the detectives cannot move
	private boolean allDetectivesHavePassMove(){
		for(ScotlandYardPlayer player : allPlayers){
			if(player.isDetective() && !containPassMove(player)) return false;
		}
		return true;
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		Set<Colour> setOfColours = new HashSet<>();
		//Mrx wins
		if( (currentRound == getRounds().size() && currentPlayer.isMrX() )
				|| allDetectivesHavePassMove() ){
			setOfColours.add(mrxPlayer.colour());
		}
		//detectives win
		for(ScotlandYardPlayer detective : allDetectives) {
			if ((mrxPlayer.location() == detective.location()) ||
					(containPassMove(mrxPlayer) && currentPlayer.isMrX()))
				for(ScotlandYardPlayer player : allDetectives){
					setOfColours.add(player.colour());
				}
		}
		return Collections.unmodifiableSet(setOfColours);
	}

	@Override
	public void startRotate() {
		if (currentRound == 0 && isGameOver()) throw new IllegalStateException("Game is already over");
		else {
			currentPlayer.player().makeMove(this, currentPlayer.location(), validMoves(currentPlayer), this);
		}
	}

	//Loop the currentPlayer attribute to next player in the allPlayers list
	private void playerIncrement(){
		int index = allPlayers.indexOf(currentPlayer);
		if(index == allPlayers.size()-1) currentPlayer = mrxPlayer;
		else currentPlayer = allPlayers.get(index+1);
	}

	//Notify the spectators of the move made by the player
	private void notifyOnMoveMade(Move move){
		for(Spectator spectator : spectators){
			spectator.onMoveMade(this,move);
		}
	}

	//Notify the spectators the start of a new round
	private void notifyOnRoundStarted() {
		for (Spectator spectator : spectators) {
			spectator.onRoundStarted(this, currentRound);
		}
	}

	//Notify the spectators that the round is complete
	private void notifyOnRotationComplete(){
		for (Spectator spectator : spectators) {
			spectator.onRotationComplete(this);
		}
	}

	//Notify the Spectators that the game is over
	private void notifyOnGameOver(){
		for (Spectator spectator : spectators) {
			spectator.onGameOver(this,getWinningPlayers());
		}
	}

	//Return true if it is reveal round
	private boolean isRevealedRound(){
		return getPlayerLocation(BLACK).equals(Optional.of(mrxPlayer.location()));
	}


	private TicketMove createMrxTicketMove(Ticket ticket, int destination){
		return new TicketMove(BLACK,ticket,destination);
	}

	@Override
	public void accept(Move move) {
		if (!validMoves(currentPlayer).contains(requireNonNull(move))){
			throw new IllegalArgumentException("invalid move");
		}
		else{
			move.visit(new MoveVisitor() {
				public void visit(TicketMove move) {
					Ticket ticket = move.ticket();
					currentPlayer.location(move.destination());
					currentPlayer.removeTicket(ticket);
					if (currentPlayer.isDetective()) {
						mrxPlayer.addTicket(ticket);
						playerIncrement();
						notifyOnMoveMade(move);
					}
					//current player is MrX
					else {
						currentRound++;
						playerIncrement();
						notifyOnRoundStarted();
						if(isRevealedRound()) notifyOnMoveMade(move);
						else notifyOnMoveMade(createMrxTicketMove(ticket,lastRevealedLocation));
					}
				}
				public void visit(DoubleMove move) {
					int destination1, destination2;
					Ticket firstTicket = move.firstMove().ticket();
					Ticket secondTicket = move.secondMove().ticket();

					//Go ahead of 1 round to check if it is reveal round
					currentRound++;
					if (!isRevealedRound()) {
						destination1 = lastRevealedLocation;
					} else {
						destination1 = move.firstMove().destination();
						lastRevealedLocation = destination1;
					}
					currentRound--;
					//Go ahead of 2 round to check if it is reveal round
					currentRound += 2;
					if (!isRevealedRound()) {
						destination2 = lastRevealedLocation;
					} else{
						destination2 = move.finalDestination();
						lastRevealedLocation = destination2;
					}
					currentRound -= 2;

					TicketMove newTicketMove1 = createMrxTicketMove(firstTicket, destination1);
					TicketMove newTicketMove2 = createMrxTicketMove(secondTicket, destination2);
					playerIncrement();
					mrxPlayer.removeTicket(DOUBLE);
					notifyOnMoveMade(new DoubleMove(BLACK, newTicketMove1, newTicketMove2));
					mrxPlayer.location(move.firstMove().destination());
					mrxPlayer.removeTicket(firstTicket);
					currentRound++;
					notifyOnRoundStarted();
					notifyOnMoveMade(newTicketMove1);
					mrxPlayer.location(move.finalDestination());
					mrxPlayer.removeTicket(secondTicket);
					currentRound++;
					notifyOnRoundStarted();
					notifyOnMoveMade(newTicketMove2);
				}
				public void visit(PassMove move) {
					if(currentPlayer.isMrX()) currentRound++;
					playerIncrement();
					notifyOnMoveMade(move);
				}
			});}
		if(isGameOver()){
			notifyOnGameOver();
		}
		else if(!isGameOver()) {
			if(currentPlayer.isMrX()) notifyOnRotationComplete();
			else{
				currentPlayer.player().makeMove(this, currentPlayer.location(), validMoves(currentPlayer), this);
			}
		}
	}

	//Return a set which contains all the detectives'current location
	private Set<Integer> getDetectivesLocation(){
		Set<Integer> detectivesLocation= new HashSet<>();
		for(ScotlandYardPlayer player : allPlayers){
			if(player.isDetective()) detectivesLocation.add(player.location());
		}
		return detectivesLocation;
	}

	private Set<Move> validMoves(ScotlandYardPlayer player) {
		Collection<Edge<Integer,Transport>> edges = graph.getEdgesFrom(graph.getNode(player.location()));
		Set<Move> validMovesTemp = new HashSet<>();

		for(Edge<Integer,Transport> edge : edges) {
			Ticket myTicket = Ticket.fromTransport(edge.data());
			int destination = edge.destination().value();
			boolean detectivesAtDestination = getDetectivesLocation().contains(destination);

			if (player.hasTickets(myTicket) && !detectivesAtDestination) {
				validMovesTemp.add(new TicketMove(player.colour(), myTicket, destination));
			}
			if (player.hasTickets(SECRET) && !detectivesAtDestination) {
				validMovesTemp.add(new TicketMove(BLACK, SECRET, destination));
			}

			if (player.hasTickets(DOUBLE)
					&& (getRounds().size() - getCurrentRound() > 1)
					&& !detectivesAtDestination) {
				Collection<Edge<Integer, Transport>> secondEdges = graph.getEdgesTo(graph.getNode(destination));
				for (Edge<Integer, Transport> edge2 : secondEdges) {
					Ticket myTicket2 = Ticket.fromTransport(edge2.data());
					int destination2 = edge2.destination().value();
					boolean detectivesAtDestination2 = getDetectivesLocation().contains(destination2);

					if ( !detectivesAtDestination2) {
						TicketMove secretTicket1 = new TicketMove(BLACK, SECRET, destination);
						TicketMove secretTicket2 = new TicketMove(BLACK, SECRET, destination2);
						TicketMove ticketMove1 = new TicketMove(BLACK, myTicket, destination);
						TicketMove ticketMove2 = new TicketMove(BLACK, myTicket2, destination2);
						if(player.hasTickets(SECRET,2)) {
							validMovesTemp.add(new DoubleMove(BLACK, secretTicket1, secretTicket2));
						}
						if(player.hasTickets(SECRET) && player.hasTickets(myTicket)){
							validMovesTemp.add(new DoubleMove(BLACK, ticketMove1, secretTicket2));
						}
						if(player.hasTickets(SECRET) && player.hasTickets(myTicket2)){
							validMovesTemp.add(new DoubleMove(BLACK, secretTicket1, ticketMove2));
						}
						if(myTicket.equals(myTicket2) && player.hasTickets(myTicket,2)) {
							validMovesTemp.add(new DoubleMove(BLACK, ticketMove1, ticketMove2));
						}
						if(player.hasTickets(myTicket2) && player.hasTickets(myTicket)
								&& !myTicket.equals(myTicket2)) {
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

	@Override
	public List<Colour> getPlayers() {
		ArrayList<Colour> playersColour = new ArrayList<>();
		for(ScotlandYardPlayer scotlandYardPlayer : allPlayers){
			playersColour.add(scotlandYardPlayer.colour());
		}
		return Collections.unmodifiableList(playersColour);
	}

	@Override
	public Optional<Integer> getPlayerLocation(Colour colour) {
		if (colour.isMrX()){
			if (currentRound == 0 || !rounds.subList(0,currentRound).contains(true)) return Optional.of(0);
			else if (getRounds().get(getCurrentRound() - 1)) {
				lastRevealedLocation = mrxPlayer.location();
				return Optional.of(mrxPlayer.location());
			}
			else return Optional.of(lastRevealedLocation);
		}
		else {
			for (ScotlandYardPlayer scotlandYardPlayer : allDetectives) {
				if (scotlandYardPlayer.colour() == colour) {
					return Optional.of(scotlandYardPlayer.location());
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<Integer> getPlayerTickets(Colour colour, Ticket ticket) {
		for(ScotlandYardPlayer scotlandYardPlayer : allPlayers){
			if(scotlandYardPlayer.colour() == colour)
				return Optional.of(scotlandYardPlayer.tickets().get(ticket));
		}
		return Optional.empty();
	}

	//Return true if the player cannot move (such that it contains PassMove)
	private boolean containPassMove(ScotlandYardPlayer player){
		return validMoves(player).contains(new PassMove(player.colour()));
	}

	@Override public boolean isGameOver(){
		return !(getWinningPlayers().isEmpty());
	}

	@Override
	public Colour getCurrentPlayer() {
		return currentPlayer.colour();
	}

	@Override
	public int getCurrentRound() {
		return currentRound;
	}

	@Override
	public List<Boolean> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		return new ImmutableGraph<>(graph);
	}

}