package br.ufrj.dcc.ad.simulador;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.Event;
import br.ufrj.dcc.ad.simulador.model.EventQueue;
import br.ufrj.dcc.ad.simulador.model.Node;
import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.model.Results;
import br.ufrj.dcc.ad.simulador.model.State;
import br.ufrj.dcc.ad.simulador.model.Transition;
import br.ufrj.dcc.ad.simulador.model.comparator.EventComparator;
import br.ufrj.dcc.ad.simulador.utils.ExponencialGenerator;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;

public class VirusSingleSimulation implements VirusSimulation {

	public ExponencialGenerator genR1;
	public ExponencialGenerator genR2;
	public ExponencialGenerator genR3;
	public ExponencialGenerator genLambda;
	public ExponencialGenerator genR4;
	Node node = new Node();
	EventQueue eventQueue = new EventQueue();

	private Boolean printSteps = false;
	private Boolean printResult = false;
	private Boolean printCSV = false;
	private Boolean printCDF = false;
	private boolean printQueue = false;

	private Rates rates;
	private long MAX_EVENTS;
	private long counter;
	private double piO;
	private double piP;
	private double totalTime;
	private double initialTime;
	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	private List<Integer> prePdf = new ArrayList<>();
	private List<Double> pdf = new ArrayList<>();
	private List<Double> cdf = new ArrayList<>();
	private double tRec;
	private double precision = 0.1;

	FileUtil file1;

	public VirusSingleSimulation(long me, Rates r) {
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponencialGenerator(rates.getR1());
		genR2 = new ExponencialGenerator(rates.getR2());
		genR3 = new ExponencialGenerator(rates.getR3());
		genR4 = new ExponencialGenerator(rates.getR4());
		genLambda = new ExponencialGenerator(rates.getLAMBDA());
	}

	public VirusSingleSimulation(long me, Rates r, FileUtil file) {
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponencialGenerator(rates.getR1());
		genR2 = new ExponencialGenerator(rates.getR2());
		genR3 = new ExponencialGenerator(rates.getR3());
		genR4 = new ExponencialGenerator(rates.getR4());
		genLambda = new ExponencialGenerator(rates.getLAMBDA());
		this.file1 = file;
	}

	@Override
	public void setUpSimulation() {
		setUpSimulation(MAX_EVENTS);
	}

	public void setUpSimulation(long me) {
		MAX_EVENTS = me;
		piO = 0;
		piP = 0;
		totalTime = 0;
		initialTime = 0;
		counter = 0;
		tRec = 0;

		generateNodes();
		setUpFirstEvent();
		counter++;
	}

	
	private void generateNodes(){
		node = new Node(State.F);
	}
	private void setUpFirstEvent(){
		if (printSteps)
			System.out.println("Event: " + counter + "\t" + "->" + State.O+ "\tt:" + dc.format(initialTime));
		Event firstEvent = new Event(node, State.O, initialTime);
		eventQueue.add(firstEvent);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.ufrj.dcc.ad.simulador.VirusSimulationInterface#runFullSimulation()
	 */
	@Override
	public Results runFullSimulation() {
		while (counter < MAX_EVENTS) {
//			stepSimulation();
			consumeEvent();
		}

		piO /= totalTime;
		piP /= totalTime;
		double cV = 10, cS = 9;
		double custoInfectado = (1 - piO) * cV;
		double custoAmostragem = (piO + piP) * cS * rates.getR4();
		double custoTotal = custoInfectado + custoAmostragem;

		if (printResult) {
			System.out.println("Simulation finished.");
			System.out.println("Steps: " + counter + "\tTime Simulated: "
					+ dc.format(totalTime));
			System.out.println("pi0: " + dc.format(piO) + "\tpiP: "
					+ dc.format(piP));
			System.out.println("Custo Infectado: " + dc.format(custoInfectado)
					+ "\t" + "Custo Amostragem: " + dc.format(custoAmostragem));
			System.out.println("Custo Total: " + dc.format(custoTotal));
		}
		if (printCSV) {
			file1.saveInFile(dc.format(rates.getR4()), dc.format(piO),
					dc.format(custoInfectado), dc.format(custoAmostragem),
					dc.format(custoTotal));
		}
		if (printCDF) {
			getProbabiltyFunctions();
			for (int i = 0; i < cdf.size(); i++) {
				file1.saveInFile((i * precision) + "", cdf.get(i) + "");
			}
		}

		return new Results(piO, piP);
	}

	public void consumeEvent() {
		Event cEvent = eventQueue.pop();
		Event nextEvent = null;
		Node cNode = cEvent.getCurrentNd();
		State cState = cNode.getState();
		State nState = cEvent.getNextState();
		Double now = cEvent.getTime();
		Double timeSpentInThisState = cEvent.getDelta();
		totalTime += timeSpentInThisState;
		
		
		switch (getTransition(cState, nState)) {
		case O_TO_P:
			piO += timeSpentInThisState;
			Event pEvent = generatePtoFEvent(cNode, now);
			Event fEvent = generatePtoREvent(cNode, now);
			nextEvent = chooseMin(pEvent, fEvent);
			break;
		case P_TO_R:
			piP += timeSpentInThisState;
			nextEvent = generateRtoOEvent(cNode, now);
			break;
		case P_TO_F:
			piP += timeSpentInThisState;
			nextEvent = generateFtoOEvent(cNode, now);
			break;
		case R_TO_O:
			nextEvent = generateOtoPEvent(cNode, now);
			break;
		case F_TO_O:
			nextEvent = generateOtoPEvent(cNode, now);
			break;
		default:
			return;
		}
		
		cNode.setState(nState);
		eventQueue.add(nextEvent);
		
		if(printSteps) { System.out.println("Event: "+counter+"\t" + "Event: " + cEvent); }
		if(printQueue) { eventQueue.printQueue(); }
		
		counter++;
		
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
		return (new EventComparator().compare(pEvent, fEvent) < 0)? pEvent:fEvent;
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
	

	// public void stepSimulation(){ //Ou executeEvent
	// double currentTime, nextTime, delta, deltaR, deltaF;
	// State nextState;
	// Event currentEvent, nextEvent;
	// Node nd;
	//
	// currentEvent = eventQueue.pop();
	// currentTime = currentEvent.getTime();
	// nd = currentEvent.getCurrentNd();
	//
	// switch(currentEvent.getNextState()){
	// case O:
	// delta = genR2.generate();
	// piO += delta;
	// nextState = State.P;
	// if(printCDF){
	// Long index = Math.round(tRec/precision);
	// int i = index.intValue();
	// while(prePdf.size()<=i)
	// prePdf.add(0);
	//
	// prePdf.set(i, (prePdf.get(i) + 1));
	// tRec = 0;
	// }
	// break;
	// case P:
	// deltaR = genR4.generate();
	// deltaF = genLambda.generate();
	// if(deltaR<deltaF){
	// delta = deltaR;
	// nextState = State.R;
	// }else{
	// delta = deltaF;
	// nextState = State.F;
	// }
	// piP +=delta;
	// if(printCDF){
	// tRec += delta;
	// }
	// break;
	// case R:
	// delta = genR3.generate();
	// nextState = State.O;
	// if(printCDF){
	// tRec += delta;
	// }
	// break;
	// case F:
	// delta = genR1.generate();
	// nextState = State.O;
	// if(printCDF){
	// tRec += delta;
	// }
	// break;
	// default:
	// System.out.println("You should not be here!");
	// return;
	// }
	//
	// nd.setState(currentEvent.getNextState());
	//
	// totalTime += delta;
	// nextTime = currentTime+delta;
	// nextEvent = new Event(nd,nextState,nextTime);
	// eventQueue.add(nextEvent);
	// if(printSteps)
	// System.out.println(currentEvent);
	// //
	// System.out.println("Event: "+counter+"\t"+nd.getState()+"->"+nextState+"\tt:"+dc.format(nextTime));
	// if(printQueue)
	// eventQueue.printQueue();
	// counter++;
	// }

	public Boolean getPrint() {
		return printSteps;
	}

	public void setPrint(Boolean print) {
		this.printSteps = print;
	}

	@Override
	public void setPrintOptions(String args[]) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == "steps")
				printSteps = true;
			if (args[i] == "results")
				printResult = true;
			if (args[i] == "CSV")
				printCSV = true;
			if (args[i] == "CDF")
				printCDF = true;
			if (args[i] == "stepsQueue")
				printQueue = true;
		}
	}

	void getProbabiltyFunctions() {
		int total = 0;
		for (int i = 0; i < prePdf.size(); i++) {
			total += prePdf.get(i);
		}
		for (int i = 0; i < prePdf.size(); i++) {
			pdf.add(prePdf.get(i) * 1.0 / total);
		}
		double acumulator = 0;
		for (int i = 0; i < pdf.size(); i++) {
			acumulator += pdf.get(i);
			cdf.add(acumulator);
		}
	};

}
