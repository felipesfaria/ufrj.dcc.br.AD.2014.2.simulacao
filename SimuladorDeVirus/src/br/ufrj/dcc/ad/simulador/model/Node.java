package br.ufrj.dcc.ad.simulador.model;

public class Node {
    
	private int nodeId;
    private State mState;
    
    public Node(){
		super();
		this.mState = State.O;
		this.nodeId = 0;
    }
    
    public Node(State state) {
		super();
		this.mState = state;
		this.nodeId = 0;
    }

    public Node(State state, int id) {
		super();
		this.mState = state;
		this.nodeId = id;
    }
    
    public State getState() {
        return mState;
    }

    public void setState(State state) {
        this.mState = state;
    }

	public int getNodeId() {
		return nodeId;
	}

	@Override
	public String toString() {
		return "Node [nodeId=" + nodeId + ", mState=" + mState + "]";
	}
	
	
}
