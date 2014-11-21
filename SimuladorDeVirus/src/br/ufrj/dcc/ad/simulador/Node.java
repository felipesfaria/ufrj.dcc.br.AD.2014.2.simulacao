package br.ufrj.dcc.ad.simulador;

public class Node {
    
	private int nodeId;
    private State mState;
    private static int id = 0;

    public Node(State state) {
		super();
		this.mState = state;
		this.nodeId = ++id;
    }
    
    public Node(){
		super();
		this.mState = State.O;
		this.nodeId = ++id;
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

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	} 
}
