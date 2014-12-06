package br.ufrj.dcc.ad.simulador;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.Event;
import br.ufrj.dcc.ad.simulador.model.EventQueue;
import br.ufrj.dcc.ad.simulador.model.Node;
import br.ufrj.dcc.ad.simulador.model.PrintOptions;
import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.model.State;
import br.ufrj.dcc.ad.simulador.model.Transition;
import br.ufrj.dcc.ad.simulador.utils.CumulativeDensityFunctionCalculator;
import br.ufrj.dcc.ad.simulador.utils.ExponentialGenerator;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;
import br.ufrj.dcc.ad.simulador.utils.Statistics;

public class NewVirusMeshSimulation implements VirusSimulation{

	private final int NUM_OF_NODES = 10;
	
	public ExponentialGenerator genR1;
	public ExponentialGenerator genR3;
	public ExponentialGenerator genR4;
	public ExponentialGenerator genLambda;
	public ExponentialGenerator genBeta;

	EventQueue eventQueue = new EventQueue();
	Node nodes[] = new Node[NUM_OF_NODES];

	private Boolean printSteps = false;
	private Boolean printResult = false;
	private Boolean printCSV = false;
	private Boolean printCDF = false;
	private boolean printQueue = false;
	private boolean printPDF = false;
	private boolean printStates = false;

	private Rates rates;
	private long MAX_EVENTS;
	
	private Double initialTime;
	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	CumulativeDensityFunctionCalculator cdfCalc;
	
	Statistics stats;

	FileUtil file1;

	public NewVirusMeshSimulation(long me, Rates r) {
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponentialGenerator(rates.getR1());
		genR3 = new ExponentialGenerator(rates.getR3());
		genR4 = new ExponentialGenerator(rates.getR4());
		genLambda = new ExponentialGenerator(rates.getLAMBDA());
		genBeta = new ExponentialGenerator(rates.getBETA());
	}

	public NewVirusMeshSimulation(long me, Rates r, FileUtil file) {
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
			nodes[i] = new Node(State.O, i);
	}
	
	private void setUpFirstEvent(){
		for (int i = 0; i < NUM_OF_NODES; i++) {
			if (printSteps)
				System.out.println("Event: " + stats.getCounter() + "\t" + "->" + State.O+ "\tt:" + dc.format(initialTime));
			Event firstEvent = new Event(nodes[i], State.P, initialTime);
			eventQueue.add(firstEvent);	
		}
	}

	@Override
	public void setPrintOptions(PrintOptions args[]) {
		for (int i = 0; i < args.length; i++) {
			switch(args[i]){
			case steps:
				printSteps = true;
				break;
			case results:
				printResult = true;
				break;
			case CSV:
				printCSV = true;
				break;
			case CDF:
				printCDF = true;
				break;
			case PDF:
				printPDF = true;
				break;
			case stepsQueue:
				printQueue = true;
				break;
			default:
				break;
			}
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

		if (printResult) {
			stats.printResult();
		}
		if (printCSV) {
			file1.saveInFile(
					dc.format(rates.getR4()), 
					dc.format(stats.getPiO()),
					dc.format(stats.getInfectedCost()),
					dc.format(stats.getSamplingCost()),
					dc.format(stats.getTotalCost()));
		}
		if (printCDF) { cdfCalc.printCDF(); }
		if (printPDF) { cdfCalc.printPDF(); } 

		return stats;
	}

	public void consumeEvent() {
		Event cEvent = eventQueue.pop();
		Event nextEvent = null;
		Node cNode = cEvent.getCurrentNd();
		State cState = cNode.getState();
		boolean isObservedNode = cNode.getNodeId() == 0; 
		State nState = cEvent.getNextState();
		Double now = cEvent.getTime();
		Double timeSpentInThisState = cEvent.getDelta();
		if(isObservedNode)
			stats.addTimePerState(timeSpentInThisState, cState);
		
		
		switch (getTransition(cState, nState)) {
		case O_TO_P:
			
			scheduleOutgoingInfections(cNode,now);
			
			Event pEvent = generatePtoFEvent(cNode, now);
			Event fEvent = generatePtoREvent(cNode, now);
			nextEvent = chooseMin(pEvent, fEvent);
			break;
		case P_TO_R:
			nextEvent = generateRtoOEvent(cNode, now);
			
			if(printCDF || printPDF)
				cdfCalc.inRecuperation(timeSpentInThisState);
			
			break;
		case P_TO_F:
			nextEvent = generateFtoOEvent(cNode, now);
			
			if(printCDF || printPDF)
				cdfCalc.inRecuperation(timeSpentInThisState);
			
			break;
		case R_TO_O:
		case F_TO_O:
			// We need to remove all infections created by this person, he is
			// cured now.
			removeInfections(cNode);
			// Now we have to schedule incoming infections
			scheduleIncomingInfections(cNode,now);
			
			if( eventQueue.isEmpty() && !isObservedNode){
				stats.addTimePerState(timeSpentInThisState, nodes[0].getState());
			}
			
			if(printCDF || printPDF)
				cdfCalc.recupered(timeSpentInThisState);
			//TODO Felipe: Porque esse 'count++;' estava aqui?
			stats.count();
			return;
		default:
			System.out.println("Error: Ilegal transition.");
			return;
		}
		
		cNode.setState(nState);
		eventQueue.add(nextEvent);
		
		if(printSteps) { System.out.println("Event: "+stats.getCounter()+"\t" + "Event: " + cEvent); }
		if(printQueue) { eventQueue.printQueue(); }
		if(printStates){
			String toPrint = "";
			for (Node node : nodes){
				toPrint+="["+node.getState()+"]";
			}
			System.out.println(toPrint);
		}
		
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
	
	private void removeInfections(Node cNode) {
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
			if (neighbour.getState() != State.O) {
				Event evt = generateInfectionvent(cNode, neighbour, now);
				eventQueue.add(evt);
			}
		}
	}
	
	private void scheduleOutgoingInfections(Node cNode, Double now) {
		for (Node neighbour : nodes) {
			if (neighbour.getState() == State.O) {
				Event evt = generateInfectionvent(neighbour, cNode, now); 
				eventQueue.add(evt);
			}
		}
	}

	private Event chooseMin(Event pEvent, Event fEvent) {
		return ( (pEvent.getTime() - fEvent.getTime()) < 0.0 )? pEvent:fEvent;
	}

	private Event generateInfectionvent(Node cNode, Node infectAgent,Double now) {
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

}
