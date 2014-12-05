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
import br.ufrj.dcc.ad.simulador.model.Results;
import br.ufrj.dcc.ad.simulador.model.State;
import br.ufrj.dcc.ad.simulador.utils.ExponentialGenerator;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;

public class VirusMeshSimulation implements VirusSimulation {

	public ExponentialGenerator genR1;
	public ExponentialGenerator genBETA;
	public ExponentialGenerator genR3;
	public ExponentialGenerator genLambda;
	public ExponentialGenerator genR4;
	Node node;
	EventQueue eventQueue = new EventQueue();

	private Boolean printSteps = false;
	private Boolean printResult = false;
	private Boolean printCSV = false;
	private Boolean printCDF = false;

	private Rates rates;
	private long MAX_EVENTS;
	private long counter;
//	private double piO;
//	private double piP;
//	private double totalTime;
	private TimePerState tps;
	private double initialTime;
	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	private List<Integer> prePdf = new ArrayList<>();
	private List<Double> pdf = new ArrayList<>();
	private List<Double> cdf = new ArrayList<>();
	private List<Node> nodes = new ArrayList<>();
	private double tRec;
	private double precision = 0.1;

	FileUtil file1;

	public VirusMeshSimulation(long me, Rates r, FileUtil file) {
		MAX_EVENTS = me;
		rates = r;
		genR1 = new ExponentialGenerator(rates.getR1());
		genBETA = new ExponentialGenerator(rates.getBETA());
		genR3 = new ExponentialGenerator(rates.getR3());
		genR4 = new ExponentialGenerator(rates.getR4());
		genLambda = new ExponentialGenerator(rates.getLAMBDA());
		this.file1 = file;
	}

	public void setUpSimulation() {
		setUpSimulation(MAX_EVENTS);
	}

	public void setUpSimulation(long me) {
		MAX_EVENTS = me;
		tps = new TimePerState();
		initialTime = 0;
		counter = 0;
		tRec = 0;
		for (int i = 0; i < 10; i++) {
			nodes.add(new Node(State.O,i));
		}

		if (printSteps)
			System.out.println("Event: " + counter + "\t" + "->" + State.O
					+ "\tt:" + dc.format(initialTime));
		for (int i = 0; i<nodes.size();i++){
			eventQueue.add(new Event(nodes.get(i), State.P, initialTime, 0));
		}
		counter++;
	}

	public Results runFullSimulation() {
		while (counter < MAX_EVENTS) {
			if(eventQueue.isEmpty()) break;
			stepSimulation();
		}


		double piO = tps.getPiO() / tps.getTotal();
		double piP = tps.getPiP() / tps.getTotal();
		double cV = 10, cS = 9;
		double custoInfectado = (1 - piO) * cV;
		double custoAmostragem = (piO + piP) * cS * rates.getR4();
		double custoTotal = custoInfectado + custoAmostragem;

		if (printResult) {
			System.out.println("Simulation finished.");
			System.out.println("Steps: " + counter + "\tTime Simulated: "
					+ dc.format(tps.getTotal()));
			System.out.println("pi0: " + dc.format(piO) + "\tpiP: "
					+ dc.format(piP));
			System.out.println("Custo Infectado: " + dc.format(custoInfectado)
					+ "\t" + "Custo Amostragem: " + dc.format(custoAmostragem));
			System.out.println("Custo Total: " + dc.format(custoTotal));
		}
		if (printCSV) {
//			file1.saveInFile(dc.format(rates.getR4()), dc.format(piO),
//					dc.format(custoInfectado), dc.format(custoAmostragem),
//					dc.format(custoTotal));
			file1.saveInFile(dc.format(rates.getR4()), String.valueOf(piO),
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

	public void stepSimulation() { // Ou executeEvent
		double currentTime, nextTime = 0, delta, deltaR, deltaF;
		State nextState = null;
		Event currentEvent, nextEvent;
		Node nd;

		currentEvent = eventQueue.pop();

		nd = currentEvent.getCurrentNd();
		
		currentTime = currentEvent.getTime();

		switch (currentEvent.getNextState()) {
		case O:
			// We need to remove all infections created by this person, he is
			// cured now.
			removeInfections(nd);
			// Now we have to schedule incoming infections
			scheduleIncomingInfections(currentEvent);
			
			if (printCDF) {
				Long index = Math.round(tRec / precision);
				int i = index.intValue();
				while (prePdf.size() <= i)
					prePdf.add(0);

				prePdf.set(i, (prePdf.get(i) + 1));
				tRec = 0;
			}
			if (printSteps)
				System.out.println("Event: " + counter
						+ "\t" + "ndId: "+ currentEvent.getCurrentNd().getNodeId()
						+ "\t" + currentEvent.getCurrentNd().getState() + "->" + currentEvent.getNextState()
						+ "\tt:" + dc.format(currentEvent.getTime()));
			if(nd.getNodeId()==0){
				acumulatePi(currentEvent);
			}
			
			break;
		case P:
			scheduleOutgoingInfections(currentEvent);

			if (nd.getState() != State.O)
				break;

			if (printSteps)
				System.out.println("Event: " + counter
						+ "\t" + "ndId: "+ currentEvent.getCurrentNd().getNodeId()
						+ "\t" + currentEvent.getCurrentNd().getState()	+ "->" + currentEvent.getNextState()
						+ "\tt:" + dc.format(currentEvent.getTime()));
			if(nd.getNodeId()==0){
				acumulatePi(currentEvent);
			}
			
			deltaR = genR4.generate();
			deltaF = genLambda.generate();
			if (deltaR < deltaF) {
				delta = deltaR;
				nextState = State.R;
			} else {
				delta = deltaF;
				nextState = State.F;
			}
			
			if (printCDF) {
				tRec += delta;
			}

			nd.setState(currentEvent.getNextState());
			nextTime = currentTime + delta;
			nextEvent = new Event(nd, nextState, nextTime, delta);
			eventQueue.add(nextEvent);
			
			

			break;
		case R:
			delta = genR3.generate();
			nextState = State.O;
			if (printCDF) {
				tRec += delta;
			}
			if (printSteps)
				System.out.println("Event: " + counter
						+ "\t" + "ndId: "+ currentEvent.getCurrentNd().getNodeId()
						+ "\t" + currentEvent.getCurrentNd().getState()	+ "->" + currentEvent.getNextState()
						+ "\tt:" + dc.format(currentEvent.getTime()));
			if(nd.getNodeId()==0){
				acumulatePi(currentEvent);
			}

			nd.setState(currentEvent.getNextState());
			nextTime = currentTime + delta;
			nextEvent = new Event(nd, nextState, nextTime, delta);
			eventQueue.add(nextEvent);
			break;
		case F:
			delta = genR1.generate();
			nextState = State.O;
			if (printCDF) {
				tRec += delta;
			}
			if (printSteps)
				System.out.println("Event: " + counter
						+ "\t" + "ndId: "+ currentEvent.getCurrentNd().getNodeId()
						+ "\t" + currentEvent.getCurrentNd().getState()	+ "->" + currentEvent.getNextState()
						+ "\tt:" + dc.format(currentEvent.getTime()));
			if(nd.getNodeId()==0){
				acumulatePi(currentEvent);
			}
			
			nd.setState(currentEvent.getNextState());
			
			nextTime = currentTime + delta;
			nextEvent = new Event(nd, nextState, nextTime, delta);
			eventQueue.add(nextEvent);
			break;
		default:
			System.out.println("You should not be here!");
			return;
		}
		
		if(eventQueue.isEmpty() && currentEvent.getCurrentNd().getNodeId() != 0){
			double totalTime = currentEvent.getTime();
			tps.addPiO(totalTime-tps.getTotal());
		}
		
		counter++;
	}

	private void removeInfections(Node nd) {
		Iterator<Event> iteratorQueue = eventQueue.getIterator();
		List<Event> toRemove = new ArrayList<Event>();
		while (iteratorQueue.hasNext()) {
			Event tmp = iteratorQueue.next();
			if (tmp.getInfectionAgentNd() != null
				&& tmp.getInfectionAgentNd().getNodeId() == nd.getNodeId()) {
				toRemove.add(tmp);
			}
		}
		for (Event e : toRemove) {
			eventQueue.removeEvent(e);
		} 

	}

	private void scheduleIncomingInfections(Event e) {
		for (Node neighbour : nodes) {
			if (neighbour.getState() != State.O) {
				double delta = genBETA.generate();
				double nextTime = e.getTime() + delta;
				Event evt = new Event(e.getCurrentNd(), neighbour, State.P, nextTime, delta);
				eventQueue.add(evt);
			}
		}
	}
	
	private void scheduleOutgoingInfections(Event e) {
		for (Node neighbour : nodes) {
			if (neighbour.getState() == State.O) {
				double delta = genBETA.generate();
				double nextTime = e.getTime() + delta;
				Event evt = new Event(neighbour, e.getCurrentNd(), State.P, nextTime, delta);
				eventQueue.add(evt);
			}
		}
	}

	public Boolean getPrint() {
		return printSteps;
	}

	public void setPrint(Boolean print) {
		this.printSteps = print;
	}

	public void setPrintOptions(String args[]) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == "steps")
				printSteps = true;
			if (args[i] == "results")
				printResult = true;
			if (args[i] == "CSV") {
				printCSV = true;
			}
			if (args[i] == "CDF") {
				printCDF = true;
			}

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
	
	private class TimePerState{
		private Double piO;
		private Double piP;
		private Double piR;
		private Double piF;
		
		public TimePerState(){
			piO = 0.0;
			piP = 0.0;
			piR = 0.0;
			piF = 0.0;
		}
		
		public Double getPiO() {
			return piO;
		}
		public void addPiO(Double piO) {
			this.piO += piO;
		}
		public Double getPiP() {
			return piP;
		}
		public void addPiP(Double piP) {
			this.piP += piP;
		}
		public Double getPiR() {
			return piR;
		}
		public void addPiR(Double piR) {
			this.piR += piR;
		}
		public Double getPiF() {
			return piF;
		}
		public void addPiF(Double piF) {
			this.piF += piF;
		}
		public Double getTotal() {
			return piO + piP + piF + piR ;
		}
		public Double getTotalInfected() {
			return piP + piF + piR ;
		}
	}
	
	void acumulatePi(Event currentEvent){
		switch (currentEvent.getCurrentNd().getState()) {
		case O:
			tps.addPiO(currentEvent.getDelta());
			break;
		case P:
			tps.addPiP(currentEvent.getDelta());
			break;
		case F:
			tps.addPiF(currentEvent.getDelta());
			break;
		case R:
			tps.addPiR(currentEvent.getDelta());
			break;
		default:
			break;
		}
	}

}

