package br.ufrj.dcc.ad.simulador;

import java.text.DecimalFormat;

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

public class VirusSingleSimulation implements VirusSimulation {

	public ExponentialGenerator genR1;
	public ExponentialGenerator genR2;
	public ExponentialGenerator genR3;
	public ExponentialGenerator genLambda;
	public ExponentialGenerator genR4;
	
	Node node = new Node();
	EventQueue eventQueue = new EventQueue();

	private Boolean printSteps = false;
	private Boolean printResult = false;
	private Boolean printCSV = false;
	private Boolean printCDF = false;
	private boolean printQueue = false;
	private boolean printPDF = false;

	private Rates rates;
	
	private long MAX_EVENTS;
	private Statistics stats;
	
	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	CumulativeDensityFunctionCalculator cdfCalc;

	FileUtil file1;

	public VirusSingleSimulation(long me, Rates r) {
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponentialGenerator(rates.getR1());
		genR2 = new ExponentialGenerator(rates.getR2());
		genR3 = new ExponentialGenerator(rates.getR3());
		genR4 = new ExponentialGenerator(rates.getR4());
		genLambda = new ExponentialGenerator(rates.getLAMBDA());
	}

	public VirusSingleSimulation(long me, Rates r, FileUtil file) {
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponentialGenerator(rates.getR1());
		genR2 = new ExponentialGenerator(rates.getR2());
		genR3 = new ExponentialGenerator(rates.getR3());
		genR4 = new ExponentialGenerator(rates.getR4());
		genLambda = new ExponentialGenerator(rates.getLAMBDA());
		this.file1 = file;
	}

	@Override
	public void setUpSimulation() {
		stats = new Statistics(rates);
		cdfCalc = new CumulativeDensityFunctionCalculator();

		generateNodes();
		setUpFirstEvent();
		stats.count();
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
	
	private void generateNodes(){
		node = new Node(State.F);
	}
	private void setUpFirstEvent(){
		if (printSteps)
			System.out.println("Event: " + stats.getCounter() + "\t" + "->" + State.O+ "\tt:" + dc.format(0.0));
		Event firstEvent = new Event(node, State.O, 0.0);
		eventQueue.add(firstEvent);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.ufrj.dcc.ad.simulador.VirusSimulationInterface#runFullSimulation()
	 */
	@Override
	public Statistics runFullSimulation() {
		while (stats.getCounter() < MAX_EVENTS) {
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
					dc.format(stats.getPiP()),
					dc.format(stats.getPiR()),
					dc.format(stats.getPiF()),
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
		State nState = cEvent.getNextState();
		Double now = cEvent.getTime();
		Double timeSpentInThisState = cEvent.getDelta();
		
		stats.addTimePerState(timeSpentInThisState, cState);
		
		switch (getTransition(cState, nState)) {
		case O_TO_P:
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
			nextEvent = generateOtoPEvent(cNode, now);
			
			if(printCDF || printPDF)
				cdfCalc.recupered(timeSpentInThisState);
			
			break;
		default:
			return;
		}
		
		cNode.setState(nState);
		eventQueue.add(nextEvent);
		
		if(printSteps) { System.out.println("Event: "+stats.getCounter()+"\t" + "Event: " + cEvent); }
		if(printQueue) { eventQueue.printQueue(); }
		
		stats.count();
		
	}
	private Transition getTransition(State cState, State nState){
		if	   ( cState == State.O && nState == State.P) {return Transition.O_TO_P;}
		else if( cState == State.P && nState == State.R) {return Transition.P_TO_R;}
		else if( cState == State.P && nState == State.F) {return Transition.P_TO_F;}
		else if( cState == State.R && nState == State.O) {return Transition.R_TO_O;}
		else if( cState == State.F && nState == State.O) {return Transition.F_TO_O;}
		return null;
	}

	private Event chooseMin(Event pEvent, Event fEvent) {
		return ( (pEvent.getTime() - fEvent.getTime()) < 0.0 )? pEvent:fEvent;
	}

	private Event generateOtoPEvent(Node cNode, Double now) {
		double nextPEventTime = genR2.generate();
		return new Event(cNode, State.P, now + nextPEventTime, nextPEventTime);
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
