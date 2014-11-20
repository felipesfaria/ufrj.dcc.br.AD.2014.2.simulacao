package br.ufrj.dcc.ad.simulador;

public class HelloVirus {
	
	public static double piZero = 0;
	public static double piP = 0;
	public static double totalTime = 0;
	public static double initialTime;
	public static long MAX_EVENTS;
	public static ExponencialGenerator genR1;
	public static ExponencialGenerator genR2;
	public static ExponencialGenerator genR3;
	public static ExponencialGenerator genLambda;
	public static ExponencialGenerator genR4; 
	
	
    public static void main(String[] args) {
    	
        System.out.println("Welcome to the best assignment in the world!");

        // Global Variables
        double taxaLambda = 1.0/(12.0*30.0*24.0);
        initialTime = 0.0;
        //MAX_EVENTS = Long.parseLong(args[0]);
        MAX_EVENTS = 100;
        EventQueue queue = new EventQueue();
        
        // Exponecial Generators
        genR1 = new ExponencialGenerator(2.0);
        genR2 = new ExponencialGenerator(0.8);
        genR3 = new ExponencialGenerator(3.0);
        genLambda = new ExponencialGenerator(taxaLambda);
        for(double j=0;j<1.0;j=j+0.1){
        genR4 = new ExponencialGenerator(j); // Special Case
        
        Node node1 = new Node();
        // Next Event in Queue
		Event firstEvent = new Event(node1, State.Propensos_a_falhas, (initialTime+genR2.generate()) );
		queue.add(firstEvent);
        double avg1=0;
        double avg2=0;
        double avg3=0;
        double avg4=0;
        double avglmbd=0;
		for (int i = 0; i < MAX_EVENTS; i++) {
			runProcess(queue);
			//avg1+=genR1.generate();
			//avg2+=genR2.generate();
			//avg3+=genR3.generate();
			//avg4+=genR4.generate();
			//avglmbd+=genLambda.generate();
		}
		//System.out.println("avg1="+(avg1/MAX_EVENTS));
		//System.out.println("avg2="+(avg2/MAX_EVENTS));
		//System.out.println("avg3="+(avg3/MAX_EVENTS));
		//System.out.println("avg4="+(avg4/MAX_EVENTS));
		//System.out.println("avglmbd="+(avglmbd/MAX_EVENTS));
		//System.out.println("r4="+j);
		//System.out.println("pi0="+(piZero/totalTime));
		//System.out.println("piP="+(piP/totalTime));
		//System.out.println(j+";"+(piZero/totalTime)+";"+(piP/totalTime));
		System.out.println((piP/totalTime));
		piZero=0;
		piP=0;
        }
    }
    
    static void runProcess(EventQueue queue){
    	
    	Event currentEvent = queue.pop();
    	Double currentTime;				
		Double nextTime;
		Event nextEvent;
		Double rTime;
		Double fTime;
		Double delta;
		
    	//System.out.println("Node " + currentEvent.getNd().getNodeId() 
    	//		+ " executed event -> went from :" + currentEvent.getCurrentState() + " to " + currentEvent.getNextState()
    	//		+ " At: " + String.format("%.8f",currentEvent.getTime()) );
    	
    	switch (currentEvent.getNextState()) {
			case Suscetiveis:
				// Set the new state of the node
				currentEvent.getNd().setState(currentEvent.getNextState());
				
				// Get the time
				currentTime = currentEvent.getTime();
				delta=genR2.generate();
				nextTime = currentTime + delta;
				piZero+=delta;
				
				// Next Event in Queue
				nextEvent = new Event(currentEvent.getNd(), State.Propensos_a_falhas, nextTime);
				queue.add(nextEvent);
				break;
			case Propensos_a_falhas:
				// Set the new state of the node
				currentEvent.getNd().setState(currentEvent.getNextState());
				
				// Get the time
				currentTime = currentEvent.getTime();
				rTime = genR4.generate();
				fTime = genLambda.generate();
				
				// We are going to execute the faster event
				if (rTime >= fTime){
					nextTime = currentTime+ fTime;
					piP+=fTime;
					// Next Event in Queue
					nextEvent = new Event(currentEvent.getNd(), State.Falhos, nextTime);
					queue.add(nextEvent);
					
				} else {
					nextTime = currentTime + rTime;
					piP+=rTime;
					// Next Event in Queue
					nextEvent = new Event(currentEvent.getNd(), State.Em_rejuvenecimento, nextTime);
					queue.add(nextEvent);
				}
				break;
			case Em_rejuvenecimento:
				// Set the new state of the node
				currentEvent.getNd().setState(currentEvent.getNextState());
				
				// Get the time
				currentTime = currentEvent.getTime();	
				delta = genR3.generate();
				nextTime = currentTime + genR3.generate();
				// Next Event in Queue
				nextEvent = new Event(currentEvent.getNd(), State.Suscetiveis, nextTime);
				queue.add(nextEvent);
				break;
			case Falhos:
				// Set the new state of the node
				currentEvent.getNd().setState(currentEvent.getNextState());
				
				// Get the time
				currentTime = currentEvent.getTime();	
				delta=genR1.generate();
				nextTime = currentTime + delta;
				
				// Next Event in Queue
				nextEvent = new Event(currentEvent.getNd(), State.Suscetiveis, nextTime);
				queue.add(nextEvent);
				break;
			default:
				System.out.println("You should not be here!");
				break;
		}
    	totalTime = queue.getLastTime();
    }
}
