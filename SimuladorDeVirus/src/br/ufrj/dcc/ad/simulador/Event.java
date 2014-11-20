package br.ufrj.dcc.ad.simulador;

public class Event implements Comparable<Event> {
	Node nd;
	State next;
	Double time;
	
	public Event(Node nd, State next, double time) {
		super();
		this.nd = nd;
		this.next = next;
		this.time = time;
	}

	@Override
	public int compareTo(Event o) {
		if (this.time < o.time) return -1;
		if (this.time > o.time) return 1;
		return 0;
	}

	public Node getNd() {
		return nd;
	}

	public void setNd(Node nd) {
		this.nd = nd;
	}

	public State getCurrentState() {
		return nd.getState();
	}

	public void setCurrent(State current) {
		this.nd.setState(current);
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
	
}

