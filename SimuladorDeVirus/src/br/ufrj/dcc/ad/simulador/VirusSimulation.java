package br.ufrj.dcc.ad.simulador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class VirusSimulation {

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

	private Rates rates;
	private long MAX_EVENTS;
	private long counter;
	private double piO;
	private double piP;
	private double totalTime;
	private double initialTime;
	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	
	public VirusSimulation(long me, Rates r){
		MAX_EVENTS = me;
		rates=r;
        genR1 = new ExponencialGenerator(rates.getR1());
        genR2 = new ExponencialGenerator(rates.getR2());
        genR3 = new ExponencialGenerator(rates.getR3());
        genR4 = new ExponencialGenerator(rates.getR4());
        genLambda = new ExponencialGenerator(rates.getLAMBDA());
	}
	
	public void setUpSimulation(){
		setUpSimulation(MAX_EVENTS);
	}
	
	public void setUpSimulation(long me){
		MAX_EVENTS = me;
		piO = 0;
		piP = 0;
		totalTime = 0;
		initialTime = 0;
		counter = 0;
		
		if(printSteps)
			System.out.println("Event: "+counter+"\t"+"->"+State.O+"\tt:"+dc.format(initialTime));
		Event firstEvent= new Event(node,State.O,initialTime);
		eventQueue.add(firstEvent);
		counter++;
	}
	
	public Results runFullSimulation(){
		while(counter<MAX_EVENTS){
			stepSimulation();
		}
		
		piO/=totalTime;
		piP/=totalTime;
    	double cV = 10, cS = 9;
    	double custoInfectado = (1-piO)*cV;
    	double custoAmostragem = (piO+piP)*cS*rates.getR4();
    	double custoTotal = custoInfectado + custoAmostragem;
    	
		if(printResult){
			System.out.println("Simulation finished.");
			System.out.println("Steps: "+counter+"\tTime Simulated: "+dc.format(totalTime));
			System.out.println("pi0: "+dc.format(piO)+"\tpiP: "+dc.format(piP));
			System.out.println("Custo Infectado: "+dc.format(custoInfectado)
								+"\t"+"Custo Amostragem: "+dc.format(custoAmostragem));
			System.out.println("Custo Total: "+dc.format(custoTotal));
		}
		if(printCSV){
//			System.out.println(dc.format(rates.getR4())+";"+dc.format(piO)+";"+dc.format(custoInfectado)+";"+dc.format(custoAmostragem)+";"+dc.format(custoTotal));
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.csv", true)))) {
			    out.println(dc.format(rates.getR4())+";"+dc.format(piO)+";"+dc.format(custoInfectado)+";"+dc.format(custoAmostragem)+";"+dc.format(custoTotal));
			    out.close();
			}catch (IOException e) {
			    //exception handling left as an exercise for the reader 
			} 
		}
		
		return new Results(piO,piP);
	}
	
	public void stepSimulation(){ //Ou executeEvent
		double currentTime, nextTime, delta, deltaR, deltaF;
		State nextState;
		Event currentEvent, nextEvent;
		Node nd;
		
		currentEvent = eventQueue.pop();
		currentTime = currentEvent.getTime();
		nd = currentEvent.getNd(); 
				
		switch(currentEvent.getNextState()){
			case O:
				delta = genR2.generate();
				piO +=delta;
				nextState = State.P;
				break;
			case P:
				deltaR = genR4.generate();
				deltaF = genLambda.generate();
				if(deltaR<deltaF){
					delta = deltaR;
					nextState = State.R;
				}else{
					delta = deltaF;
					nextState = State.F;
				}
				piP +=delta;
				break;
			case R:
				delta = genR3.generate();
				nextState = State.O;
				break;
			case F:
				delta = genR1.generate();
				nextState = State.O;
				break;
			default:
				System.out.println("You should not be here!");
				return;
		}
		
		nd.setState(currentEvent.getNextState());
		totalTime += delta;
		nextTime = currentTime+delta;
		nextEvent = new Event(nd,nextState,nextTime);
		eventQueue.add(nextEvent);
		if(printSteps)
			System.out.println("Event: "+counter+"\t"+nd.getState()+"->"+nextState+"\tt:"+dc.format(nextTime));
		counter++;
	}

	public Boolean getPrint() {
		return printSteps;
	}

	public void setPrint(Boolean print) {
		this.printSteps = print;
	}
	public void setPrintOptions(String args[]){
		for(int i =0;i<args.length;i++){
			if(args[i]=="steps")
				printSteps=true;
			if(args[i]=="results")
				printResult=true;
			if(args[i]=="CSV"){
				printCSV=true;
		    	try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.csv", true)))) {
					out.println("R4;piO;cV;cS;cT");
				    out.close();
				}catch (IOException e) {
				    //exception handling left as an exercise for the reader 
				}
			}
		}
	}
	
}
