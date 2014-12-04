package br.ufrj.dcc.ad.simulador.model;


public class Event{
	private Node currentNd;
	private Node infectionAgentNd;
	private State nextState;
	private Double time;
	Double delta;
	
	public Event(Node nd, State next, Double time) {
		super();
		this.currentNd = nd;
		this.nextState = next;
		this.time = time;
		delta = 0.0;
	}
	
	public Event(Node nd, State next, double time, double delta) {
		super();
		this.currentNd = nd;
		this.nextState = next;
		this.time = time;
		this.delta = delta;
	}
	
	public Event(Node currentNd, Node infectionAgent, State next, double time, double delta) {
		super();
		this.currentNd = currentNd;
		this.infectionAgentNd = infectionAgent;
		this.nextState = next;
		this.time = time;
		this.delta = delta;
	}

	public Node getCurrentNd() {
		return currentNd;
	}

	public Node getInfectionAgentNd() {
		return infectionAgentNd;
	}

	public State getNextState() {
		return nextState;
	}

	public Double getTime() {
		return time;
	}

	public Double getDelta() {
		return delta;
	}

	@Override
	public String toString() {
		return "Event [nd=" + currentNd + ", infectionAgentNd=" + infectionAgentNd
				+ ", nextState=" + nextState + ", time=" + time + "]";
	}
	
	

}

