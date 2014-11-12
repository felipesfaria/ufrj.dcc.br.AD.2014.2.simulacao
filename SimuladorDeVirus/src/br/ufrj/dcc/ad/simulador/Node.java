package br.ufrj.dcc.ad.simulador;

public class Node {
    
    private State mState;

    public Node(State state) {
	super();
	this.mState = state;
    }
    
    public Node(){
	super();
	this.mState = State.suscetiveis;
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        this.mState = state;
    }
    
    public Double changeState(State state){
	Double cost = 0.0;
	switch(state){
	case suscetiveis:
	case propensos_a_falhas:
	case falhos:
	case em_rejuvenecimento:
	default:
	    break;
	}
	
	return cost;
    }
    
}
