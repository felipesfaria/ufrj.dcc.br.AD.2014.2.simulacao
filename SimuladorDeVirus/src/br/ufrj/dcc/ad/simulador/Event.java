package br.ufrj.dcc.ad.simulador;

public class Event implements Comparable<Event> {
	Node currentNd;
	Node infectorAgentNd;
	State next;
	Double time;
	Double delta;
	
	public Event(Node nd, State next, double time) {
		super();
		this.currentNd = nd;
		this.next = next;
		this.time = time;
	}
	
	public Event(Node nd, State next, double time, double delta) {
		super();
		this.currentNd = nd;
		this.next = next;
		this.time = time;
		this.delta = delta;
	}
	
	public Event(Node nd, Node infect , State next, double time, double delta) {
		super();
		this.currentNd = nd;
		this.infectorAgentNd = infect;
		this.next = next;
		this.time = time;
		this.delta = delta;
	}

	@Override
	public int compareTo(Event o) {
		if (this.time < o.time) return -1;
		if (this.time > o.time) return 1;
		return 0;
	}

	public Node getCurrentNd() {
		return currentNd;
	}

	public void setCurrentNd(Node nd) {
		this.currentNd = nd;
	}

	public State getCurrentState() {
		return currentNd.getState();
	}

	public void setCurrent(State current) {
		this.currentNd.setState(current);
	}

	public State getNextState() {
		return next;
	}

	public void setNext(State next) {
		this.next = next;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public Node getInfectorAgentNd() {
		return infectorAgentNd;
	}

	public void setInfectorAgentNd(Node infectorAgentNd) {
		this.infectorAgentNd = infectorAgentNd;
	}

	public Double getDelta() {
		return delta;
	}

	public void setDelta(Double delta) {
		this.delta = delta;
	}

	@Override
	public String toString() {
		return "Event [currentNd=" + currentNd + ", infectorAgentNd="
				+ infectorAgentNd + ", next=" + next + ", time=" + time
				+ ", delta=" + delta + "]";
	}
	
}

