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
    	State newState;
    	switch(state){
    	case O:
    		newState = State.O;
    		break;
    	case P:
    		newState = State.P;
    		break;
    	case R:
    		newState = State.R;
    		break;
    	case F:
    		newState = State.F;
    		break;
		default:
			newState = null;
			break;
    	}
        this.mState = newState;
    }

	public int getNodeId() {
		return nodeId;
	}

	@Override
	public String toString() {
		return "N[" + nodeId + "," + mState + "]";
	}
	
	
}
