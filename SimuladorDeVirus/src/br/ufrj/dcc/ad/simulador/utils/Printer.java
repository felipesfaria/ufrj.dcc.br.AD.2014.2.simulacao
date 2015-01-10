package br.ufrj.dcc.ad.simulador.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.Event;
import br.ufrj.dcc.ad.simulador.model.EventQueue;
import br.ufrj.dcc.ad.simulador.model.Node;
import br.ufrj.dcc.ad.simulador.model.PrintOptions;
import br.ufrj.dcc.ad.simulador.model.Rates;

public class Printer {
	private Boolean printSteps;
	private Boolean printResult;
	private Boolean printCSV;
	private Boolean printCDF;
	private Boolean printQueue;
	private Boolean printPDF;
	private Boolean printStates;

	private DecimalFormat dc = new DecimalFormat(",000.000000000");
	
	public Printer(){
		this(null);
	}

	public Printer(PrintOptions args[]){
		printSteps = false;
		printResult = false;
		printCSV = false;
		printCDF = false;
		printQueue = false;
		printPDF = false;
		printStates = false;
		setPrintOptions(args);
	}
	
	public void setPrintOptions(PrintOptions args[]) {
		if(args==null) return;
		for (PrintOptions option : args){
			switch(option){
			case steps:
				printSteps = true;
				break;
			case results:
				printResult = true;
				break;
			case CSV:
				printCSV = true;
				break;
			case CDF:
				printCDF = true;
				break;
			case PDF:
				printPDF = true;
				break;
			case stepsQueue:
				printQueue = true;
				break;
			case states:
				printStates = true;
				break;
			default:
				break;
			}
		}
	}
	
	public void disablePrintOptions(PrintOptions args[]) {
		for (int i = 0; i < args.length; i++) {
			switch(args[i]){
			case steps:
				printSteps = false;
				break;
			case results:
				printResult = false;
				break;
			case CSV:
				printCSV = false;
				break;
			case CDF:
				printCDF = false;
				break;
			case PDF:
				printPDF = false;
				break;
			case stepsQueue:
				printQueue = false;
				break;
			case states:
				printStates = false;
				break;
			default:
				break;
			}
		}
	}
	
	public void togglePrintOptions(PrintOptions args[]) {
		for (int i = 0; i < args.length; i++) {
			switch(args[i]){
			case steps:
				printSteps = !printSteps;
				break;
			case results:
				printResult = !printResult;
				break;
			case CSV:
				printCSV = !printCSV;
				break;
			case CDF:
				printCDF = !printCDF;
				break;
			case PDF:
				printPDF = !printPDF;
				break;
			case stepsQueue:
				printQueue = !printQueue;
				break;
			case states:
				printStates = !printStates;
				break;
			default:
				break;
			}
		}
	}
	
	public void printResults(VirusSimulation vSim) {
		if (printResult) {
			vSim.getStats().printResult();
		}
	}
	
	public void printCSV(VirusSimulation vSim) {
		if (printCSV) {
			FileUtil file1 = vSim.getFile();
			Rates rates = vSim.getRates();
			Statistics stats = vSim.getStats();
			file1.saveInFile(
					dc.format(rates.getR4()), 
					dc.format(stats.getPiO()),
					dc.format(stats.getInfectedCost()),
					dc.format(stats.getSamplingCost()),
					dc.format(stats.getTotalCost()));
		}
		
	}
	
	public void printGlobalStats(FileUtil file, Double r4){
		if (printCSV) {
			file.saveInFile(
					dc.format(r4),
					dc.format(Statistics.getGlobalAveragePiO()),
					dc.format(Statistics.GetIntervalosDeConfianca(r4).get("pi0")),
					dc.format(Statistics.getGlobalAveragePiP()),
					dc.format(Statistics.GetIntervalosDeConfianca(r4).get("piP")),
					dc.format(Statistics.getGlobalAveragePiR()),
					dc.format(Statistics.GetIntervalosDeConfianca(r4).get("piR")),
					dc.format(Statistics.getGlobalAveragePiF()),
					dc.format(Statistics.GetIntervalosDeConfianca(r4).get("piF")),
					dc.format(Statistics.getGlobalAverageInfectedCost()),
					dc.format(Statistics.GetIntervalosDeConfianca(r4).get("CustoInfectado")),
					dc.format(Statistics.getGlobalAverageSamplingCost()),
					dc.format(Statistics.GetIntervalosDeConfianca(r4).get("CustoAmostragem")),
					dc.format(Statistics.getGlobalAverageTotalCost()),
					dc.format(Statistics.GetIntervalosDeConfianca(r4).get("CustoTotal")));
		}
	}

	public void printGlobalCDF(FileUtil file, Double r4,  List<Double> cdf){
		double PRECISION = 0.01;
		for ( int i = 0; i < cdf.size();i++) {
			file.saveInFile(i * PRECISION + "",
					cdf.get(i) + "",
					r4 + "");
		}
	}

	
	public void printCDF(VirusSimulation vSim) {
		if (printCDF) { vSim.getCDFCalculator().printCDF(vSim.getRates().getR4()); }
	}
	
	public void printPDF(VirusSimulation vSim) {
		if (printPDF) { vSim.getCDFCalculator().printPDF(); }
		
	}
	
	public void printSteps(Statistics stats,Event event) {
		if(printSteps) { System.out.println("Event: "+stats.getCounter()+"\t" + "Event: " + event); }
	}
	
	public void printQueue(EventQueue eventQueue) {
		if(printQueue) { eventQueue.printQueue(); }
	}
	
	public void printStates(List<Node> nodes) {
		if(printStates){
			String toPrint = "";
			for (Node node : nodes){
				toPrint+="["+node.getState()+"]";
			}
			System.out.println(toPrint);
		}
	}

}
