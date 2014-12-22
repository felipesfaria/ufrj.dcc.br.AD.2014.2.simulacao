package br.ufrj.dcc.ad.simulador;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.Event;
import br.ufrj.dcc.ad.simulador.model.EventQueue;
import br.ufrj.dcc.ad.simulador.model.Node;
import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.model.State;
import br.ufrj.dcc.ad.simulador.model.Transition;
import br.ufrj.dcc.ad.simulador.utils.CumulativeDensityFunctionCalculator;
import br.ufrj.dcc.ad.simulador.utils.ExponentialGenerator;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;
import br.ufrj.dcc.ad.simulador.utils.Printer;
import br.ufrj.dcc.ad.simulador.utils.Statistics;

public class VirusRingSimulation implements VirusSimulation{
	private int NUM_OF_NODES = 10;
	
	public ExponentialGenerator genR1;
	public ExponentialGenerator genR3;
	public ExponentialGenerator genR4;
	public ExponentialGenerator genLambda;
	public ExponentialGenerator genBeta;

	EventQueue eventQueue = new EventQueue();
	List<Node> nodes = new ArrayList<Node>();

	private Rates rates;
	private long MAX_EVENTS;
	
	private Double initialTime;
	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	CumulativeDensityFunctionCalculator cdfCalc;
	
	Statistics stats;

	FileUtil file1;
	
	Printer printer = new Printer();

	public VirusRingSimulation(long me, Rates r) {
		this( me, r, 10);
	}

	public VirusRingSimulation(long me, Rates r, int numberOfNodes) {
		NUM_OF_NODES = numberOfNodes;
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponentialGenerator(rates.getR1());
		genR3 = new ExponentialGenerator(rates.getR3());
		genR4 = new ExponentialGenerator(rates.getR4());
		genLambda = new ExponentialGenerator(rates.getLAMBDA());
		genBeta = new ExponentialGenerator(rates.getBETA());
	}

	public VirusRingSimulation(long me, Rates r, FileUtil file) {
		this(me, r, file, 10);
	}
	
	public VirusRingSimulation(long me, Rates r, FileUtil file, int numberOfNodes) {
		NUM_OF_NODES = numberOfNodes;
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponentialGenerator(rates.getR1());
		genR3 = new ExponentialGenerator(rates.getR3());
		genR4 = new ExponentialGenerator(rates.getR4());
		genLambda = new ExponentialGenerator(rates.getLAMBDA());
		genBeta = new ExponentialGenerator(rates.getBETA());
		this.file1 = file;
	}

	@Override
	public void setUpSimulation() {
		stats = new Statistics(rates);
		initialTime = 0.0;
		cdfCalc = new CumulativeDensityFunctionCalculator();

		generateNodes();
		setUpFirstEvent();
	}
	
	private void generateNodes(){
		for(int i = 0; i < NUM_OF_NODES; i++)
			nodes.add(new Node(State.O, i));
	}
	
	private void setUpFirstEvent(){
		for(Node node : nodes){
			Event firstEvent = new Event(node, State.P, initialTime);
			eventQueue.add(firstEvent);	
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.ufrj.dcc.ad.simulador.VirusSimulationInterface#runFullSimulation()
	 */
	@Override
	public Statistics runFullSimulation() {
		while (stats.getCounter() < MAX_EVENTS && !eventQueue.isEmpty()) {
			consumeEvent();
		}

		stats.finish();
		
		printer.printResults(this);
		
		printer.printCSV(this);

		printer.printCDF(this);
		printer.printPDF(this); 

		return stats;
	}

	public void consumeEvent() {
		Event cEvent = eventQueue.pop();
		printer.printSteps(getStats(), cEvent);
		Event nextEvent;
		Node cNode = cEvent.getCurrentNd();
		State cState = cNode.getState();
		State nState = cEvent.getNextState();
		Double now = cEvent.getTime();
		Double timeSpentInThisState = cEvent.getDelta();

		boolean isObservedNode = cNode.getNodeId() == 0;

		Transition trans = getTransition(cState, nState);

		cNode.setState(nState); //We must do the transition before we remove Events from the queue

		switch (trans) {
		case O_TO_P:
			if(isObservedNode)
				stats.addTimePerState(timeSpentInThisState, cState);

			//We have to remove incoming infections because the node is now infected
			removeIncomingInfections(cNode);

			//now we can infect other nodes
			scheduleOutgoingInfections(cNode,now);

			Event pEvent = generatePtoFEvent(cNode, now);
			Event fEvent = generatePtoREvent(cNode, now);
			nextEvent = chooseMin(pEvent, fEvent);
			break;
		case P_TO_R:
			if(isObservedNode)
				stats.addTimePerState(timeSpentInThisState, cState);

			//We have to remove incoming infections because the node is now infected
			removeIncomingInfections(cNode);

			//now we can infect other nodes
			scheduleOutgoingInfections(cNode,now);

			nextEvent = generateRtoOEvent(cNode, now);
			
			cdfCalc.inRecuperation(timeSpentInThisState);
			
			break;
		case P_TO_F:
			if(isObservedNode)
					stats.addTimePerState(timeSpentInThisState, cState);

			//We have to remove incoming infections because the node is now infected
			removeIncomingInfections(cNode);

			//now we can infect other nodes
			scheduleOutgoingInfections(cNode,now);

			nextEvent = generateFtoOEvent(cNode, now);
			
			cdfCalc.inRecuperation(timeSpentInThisState);
			
			break;
		case R_TO_O:
		case F_TO_O:
			// We need to remove all infections created by this person, he is
			// cured now.
			removeOutgoingInfections(cNode);
			// Now we have to schedule incoming infections
			scheduleIncomingInfections(cNode,now);

			if( eventQueue.isEmpty() && !isObservedNode){
				stats.addTimePerState(timeSpentInThisState, nodes.get(0).getState());
			}
			
			cdfCalc.recupered(timeSpentInThisState);
			//TODO Felipe: Porque esse 'count++;' estava aqui?
			// Bruno: por que ele da return logo abaixo e não break
			stats.count();
			return;
		default:
			System.out.println("Error: Ilegal transition:"+cEvent);
			return;
		}
		
		
		eventQueue.add(nextEvent);
		
		printer.printStates(nodes);
		
		stats.count();
		
	}
	
	private Transition getTransition(State cState, State nState){
		if	   ( cState == State.O && nState == State.P) {return Transition.O_TO_P;}
		else if( cState == State.P && nState == State.R) {return Transition.P_TO_R;}
		else if( cState == State.P && nState == State.F) {return Transition.P_TO_F;}
		else if( cState == State.R && nState == State.O) {return Transition.R_TO_O;}
		else if( cState == State.F && nState == State.O) {return Transition.F_TO_O;}
		return Transition.ILEGAL;
	}

	private void removeIncomingInfections(Node cNode) {
		Iterator<Event> iteratorQueue = eventQueue.getIterator();
		List<Event> toRemove = new ArrayList<>();
		while (iteratorQueue.hasNext()) {
			Event tmp = iteratorQueue.next();
			if (tmp.getCurrentNd() != null
					&& tmp.getCurrentNd().getNodeId() == cNode.getNodeId()
					&& tmp.getNextState() == State.P) {
				toRemove.add(tmp);
			}
		}
		for (Event e : toRemove) {
			eventQueue.removeEvent(e);
		}
	}
	
	private void removeOutgoingInfections(Node cNode) {
		Iterator<Event> iteratorQueue = eventQueue.getIterator();
		List<Event> toRemove = new ArrayList<Event>();
		while (iteratorQueue.hasNext()) {
			Event tmp = iteratorQueue.next();
			if (tmp.getInfectionAgentNd() != null
				&& tmp.getInfectionAgentNd().getNodeId() == cNode.getNodeId()) {
				toRemove.add(tmp);
			}
		}
		for (Event e : toRemove) {
			eventQueue.removeEvent(e);
		}
	}

	private void scheduleIncomingInfections(Node cNode, Double now) {
		for (Node neighbour : nodes) {
			if (neighbour.getState() != State.O
					&& cNode.isRingNeighbour(neighbour, nodes.size())) {
				Event evt = generateInfectionEvent(cNode, neighbour, now);
				eventQueue.add(evt);
			}
		}
	}
	
	private void scheduleOutgoingInfections(Node cNode, Double now) {
		for (Node neighbour : nodes) {
			if (neighbour.getState() == State.O && cNode.isRingNeighbour(neighbour, nodes.size())) {
				Event evt = generateInfectionEvent(neighbour, cNode, now);
				eventQueue.add(evt);
			}
		}
	}

	private Event chooseMin(Event pEvent, Event fEvent) {
		return ( (pEvent.getTime() - fEvent.getTime()) < 0.0 )? pEvent:fEvent;
	}

	private Event generateInfectionEvent(Node cNode, Node infectAgent, Double now) {
		double nextPEventTime = genBeta.generate();
		return new Event(cNode, infectAgent, State.P, now + nextPEventTime, nextPEventTime);
	}
	
	private Event generatePtoFEvent(Node cNode, Double now) {
		double nextPEventTime = genLambda.generate();
		return new Event(cNode, State.F, now + nextPEventTime, nextPEventTime);
	}
	
	private Event generatePtoREvent(Node cNode, Double now) {
		double nextPEventTime = genR4.generate();
		return new Event(cNode, State.R, now + nextPEventTime, nextPEventTime);
	}
	private Event generateRtoOEvent(Node cNode, Double now){
		double nextPEventTime = genR3.generate();
		return new Event(cNode, State.O, now + nextPEventTime, nextPEventTime);
	}
	private Event generateFtoOEvent(Node cNode, Double now){
		double nextPEventTime = genR1.generate();
		return new Event(cNode, State.O, now + nextPEventTime, nextPEventTime);
	}
	
	public Statistics getStats(){
		return stats;
	}
	
	public FileUtil getFile(){
		return file1;
	}
	
	public Rates getRates(){
		return rates;
	}
	
	public CumulativeDensityFunctionCalculator getCDFCalculator(){
		return cdfCalc;
	}


}
