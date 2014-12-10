package br.ufrj.dcc.ad.simulador.utils;

import java.text.DecimalFormat;

import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.model.State;

public class Statistics {
	
	static int simulations=0;
	static Double globalAcumulatedPiO=0.0;
	static Double globalAcumulatedPiP=0.0;
	static Double globalAcumulatedPiR=0.0;
	static Double globalAcumulatedPiF=0.0;
	static Double globalAcumulatedInfectedCost=0.0;
	static Double globalAcumulatedSamplingCost=0.0;
	static Double globalAcumulatedTotalCost=0.0;
	
	Double r4;
	Double timeInO=0.0;
	Double timeInP=0.0;
	Double timeInR=0.0;
	Double timeInF=0.0;
	Double piO;
	Double piP;
	Double piR;
	Double piF;
	
	Double infectedWeight=10.0;//CV
	Double samplingWeight=9.0;//CS
	Double infectedCost;
	Double samplingCost;
	Double totalCost;
	Double totalTime;
	
	Rates rates;

	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	private int counter=0;
	
	public Statistics(Rates r, final Double infectedWeight, final Double samplingWeight) {
		rates = r;
		this.infectedWeight = infectedWeight;
		this.samplingWeight = samplingWeight;
	}
	
	public Statistics(Rates r) {
		rates = r;
	}

	public void addTimePerState(double time, State state){
		switch(state){
		case O:
			timeInO+=time;
			break;
		case P:
			timeInP+=time;
			break;
		case R:
			timeInR+=time;
			break;
		case F:
			timeInF+=time;
			break;
		default:
			System.out.println("Error: invalidState");
			break;
		}
	}
	
	public Double getPiO() {
		return piO;
	}
	public Double getPiP() {
		return piP;
	}
	public Double getPiR() {
		return piR;
	}
	public Double getPiF() {
		return piF;
	}
	public Double getInfectedCost() { return infectedCost; }
	public Double getSamplingCost() { return samplingCost; }
	public Double getTotalCost() { return totalCost; }
		
	public int getCounter() {
		return counter;
	}

	public Double getR4() {
		return rates.getR4();
	}

	public Double getTotalTime() {
		return totalTime;
	}

	public void count(){
		counter++;
	}
	
	public void finish(){
		totalTime = timeInO+timeInP+timeInR+timeInF;
		piO=timeInO/totalTime;
		piP=timeInP/totalTime;
		piR=timeInR/totalTime;
		piF=timeInF/totalTime;
		infectedCost=(1-piO)*infectedWeight;
		samplingCost=(piO+piP)*rates.getR4()*samplingWeight;
		totalCost=samplingCost+infectedCost;
	}
	public void printResult() {
		System.out.println("Simulation finished.");
		System.out.println("Steps: " + counter + "\tTime Simulated: " + dc.format(totalTime));
		System.out.println("pi0: " + dc.format(piO) + "\tpiP: " + dc.format(piP));
		System.out.println("Custo Infectado: " + dc.format(infectedCost) + "\t" + "Custo Amostragem: " + dc.format(samplingCost));
		System.out.println("Custo Total: " + dc.format(totalCost));
	}
	public static void acumulatePiO(Double piO){
		globalAcumulatedPiO+=piO;
	}
	public static void acumulatePiP(Double piP){
		globalAcumulatedPiP+=piP;
	}
	public static Double getGlobalAveragePiO(){
		return globalAcumulatedPiO/simulations;
	}
	public static Double getGlobalAveragePiP(){
		return globalAcumulatedPiP/simulations;
	}
	public static void acumulateInfectedCost(Double InfectedCost){
		globalAcumulatedInfectedCost+=InfectedCost;
	}
	public static void acumulateSamplingCost(Double SamplingCost){
		globalAcumulatedSamplingCost+=SamplingCost;
	}
	public static void acumulateTotalCost(Double TotalCost){
		globalAcumulatedTotalCost+=TotalCost;
	}
	public static Double getGlobalAverageInfectedCost(){
		return globalAcumulatedInfectedCost/simulations;
	}
	public static Double getGlobalAverageSamplingCost(){
		return globalAcumulatedSamplingCost/simulations;
	}
	public static Double getGlobalAverageTotalCost(){
		return globalAcumulatedTotalCost/simulations;
	}
	
	public static void incrementSimulation(){
		simulations++;
	}

}
