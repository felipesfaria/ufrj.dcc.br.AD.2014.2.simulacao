package br.ufrj.dcc.ad.simulador;

import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.PrintOptions;
import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;
import br.ufrj.dcc.ad.simulador.utils.Printer;
import br.ufrj.dcc.ad.simulador.utils.Statistics;

import java.text.DecimalFormat;
import java.util.Map;

public class HelloVirus {

	private static final int REALY_LARGE_NUM = 10000000;
	private static final int MAX_SIMULATION = 1000;
	public static VirusSimulation simulation;

	public static final double r1 = 2.0;
	public static final double r2 = 0.8;
	public static final double r3 = 3.0;
	public static final double LAMBDA = 1.0 / (12.0 * 24.0 * 30.0);
	public static final double BETA = 0.08;
	
	static Double r4 = 1.0;
	static Double delta = 0.01;
	static Double min_r4 = 0.0;
	
	static int maxEvents = 10000;
	
//	public static Printer printer = new Printer();

	public static void main(String[] args) {
//		printer = new Printer();
		
		// System.out.println("Welcome to the best assignment in the world!");

		if (args.length == 3) {
			r4 = Double.parseDouble(args[0]);
			delta = Double.parseDouble(args[1]);
			min_r4 = Double.parseDouble(args[2]);
		}
		
//		runRingCostAnalysis();
//		runMeshOnce();
		runMeshCostAnalysis();
//      runEndogenousMeshCostAnalysis();
//		runEndogenousRingCostAnalysis();
//		runSingleNodeCostAnalysis();
//		runSingleNodeTimeAnalysis();
		
		System.out.println("Finished");
	}

	static void runEndogenousRingCostAnalysis() {
		boolean printCSV = true;
		Printer printer = new Printer();

		FileUtil file = new FileUtil("EndogenousRingCostAnalysis.csv", "r4;piO;piP;piR;piF;cV;cS;cT");
		DecimalFormat dc = new DecimalFormat(",000.000000000");

		while (r4 >= min_r4) {

			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
				Statistics stats = null;
				simulation = new VirusRingSimulation(maxEvents, r,file,10);
				//printer.setPrintOptions(new PrintOptions[]{PrintOptions.CSV});
				simulation.setUpSimulation();
				stats = simulation.runFullSimulation();
				
				Statistics.incrementSimulation();
				Statistics.accumulatePiO(stats.getPiO());
				Statistics.accumulatePiP(stats.getPiP());
				Statistics.accumulatePiR(stats.getPiR());
				Statistics.accumulatePiF(stats.getPiF());
				Statistics.accumulateInfectedCost(stats.getInfectedCost());
				Statistics.accumulateSamplingCost(stats.getSamplingCost());
				Statistics.accumulateTotalCost(stats.getTotalCost());
			}
			

			printer.printGlobalStats(file, r4);
			Statistics.resetGlobalStatistics();
			Statistics.GetIntervaloDeConfianca(r4);
			r4 -= delta;
		}

	}

	static void runRingCostAnalysis() {
		boolean printCSV = true;
		Printer printer = new Printer();

		FileUtil file = new FileUtil("RingCostAnalysis.csv", "r4;piO;piP;piR;piF;cV;cS;cT");
		DecimalFormat dc = new DecimalFormat(",000.000000000");

		while (r4 >= min_r4) {

			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
				Statistics stats = null;
				simulation = new VirusRingSimulation(maxEvents, r,file,10);
				//printer.setPrintOptions(new PrintOptions[]{PrintOptions.CSV});
				simulation.setUpSimulation();
				stats = simulation.runFullSimulation();
				
				Statistics.incrementSimulation();
				Statistics.accumulatePiO(stats.getPiO());
				Statistics.accumulatePiP(stats.getPiP());
				Statistics.accumulatePiR(stats.getPiR());
				Statistics.accumulatePiF(stats.getPiF());
				Statistics.accumulateInfectedCost(stats.getInfectedCost());
				Statistics.accumulateSamplingCost(stats.getSamplingCost());
				Statistics.accumulateTotalCost(stats.getTotalCost());
			}

			printer.printGlobalStats(file, r4);
			Statistics.resetGlobalStatistics();
			Statistics.GetIntervaloDeConfianca(r4);
			r4 -= delta;
		}

	}

	static void runMeshCostAnalysis() {
		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CSV});
		double bestR4 = r4;
		double bestTotalCost = REALY_LARGE_NUM;

		FileUtil file = new FileUtil("MeshCostAnalysis.csv", "r4;piO;piP;piR;piF;cV;cS;cT");
		DecimalFormat dc = new DecimalFormat(",000.000000000");

		while (r4 >= min_r4) {

			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
				Statistics stats = null;
				simulation = new VirusMeshSimulation(maxEvents, r,10);
				//printer.setPrintOptions(new PrintOptions[]{PrintOptions.steps,PrintOptions.states,PrintOptions.results,PrintOptions.states});
				simulation.setUpSimulation();
				stats = simulation.runFullSimulation();
				
				Statistics.incrementSimulation();
				Statistics.accumulatePiO(stats.getPiO());
				Statistics.accumulatePiP(stats.getPiP());
				Statistics.accumulatePiR(stats.getPiR());
				Statistics.accumulatePiF(stats.getPiF());
				Statistics.accumulateInfectedCost(stats.getInfectedCost());
				Statistics.accumulateSamplingCost(stats.getSamplingCost());
				Statistics.accumulateTotalCost(stats.getTotalCost());
			}

			printer.printGlobalStats(file, r4);
			if ( Statistics.getGlobalAverageTotalCost() < bestTotalCost ) {
				bestR4 = r4;
				bestTotalCost = Statistics.getGlobalAverageTotalCost();
			}
			Statistics.resetGlobalStatistics();
			Map<String,Double> intervaloDeConfianca = Statistics.GetIntervalosDeConfianca(r4);

			r4 -= delta;
		}

		runMeshTimeAnalysis(bestR4);

	}

	static void runEndogenousMeshCostAnalysis() {
		boolean printCSV = true;
		Printer printer = new Printer();
		FileUtil file = new FileUtil("EndogenousMeshCostAnalysis.csv", "r4;piO;piP;piR;piF;cV;cS;cT");
		DecimalFormat dc = new DecimalFormat(",000.000000000");

		while (r4 >= min_r4) {

			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
				Statistics stats = null;
				simulation = new VirusMeshEndogenousSimulation(maxEvents, r,10);
				//printer.setPrintOptions(new PrintOptions[]{PrintOptions.steps,PrintOptions.states,PrintOptions.results,PrintOptions.states});
				simulation.setUpSimulation();
				stats = simulation.runFullSimulation();

				Statistics.incrementSimulation();
				Statistics.accumulatePiO(stats.getPiO());
				Statistics.accumulatePiP(stats.getPiP());
				Statistics.accumulatePiR(stats.getPiR());
				Statistics.accumulatePiF(stats.getPiF());
				Statistics.accumulateInfectedCost(stats.getInfectedCost());
				Statistics.accumulateSamplingCost(stats.getSamplingCost());
				Statistics.accumulateTotalCost(stats.getTotalCost());
			}

			printer.printGlobalStats(file, r4);
			Statistics.resetGlobalStatistics();
			Statistics.GetIntervaloDeConfianca(r4);
			r4 -= delta;
		}
	}

	static void runMeshOnce(){
		FileUtil file = new FileUtil("CostAnalysis.csv", "r4;piO;cV;cS;cT");
		Rates r = new Rates(r1, BETA, r3, r4, LAMBDA);
		simulation = new VirusMeshSimulation(maxEvents, r, file);
		Printer printer = new Printer();
		printer.setPrintOptions(new PrintOptions[] { PrintOptions.CSV });
		simulation.setUpSimulation();
		simulation.runFullSimulation();

	}


	static void runSingleNodeCostAnalysis() {
		System.out.println("R4;piO;cV;cS;cT");
		FileUtil file = new FileUtil("CostAnalysis.csv", "R4;piO;cV;cS;cT");
		double bestR4 = r4;
		double bestTotalCost = REALY_LARGE_NUM;
		while (r4 >= min_r4) {
			Rates r = new Rates(r1, r2, r3, r4, LAMBDA);

			Printer printer = new Printer(new PrintOptions[] { PrintOptions.CSV });

			simulation = new VirusSingleSimulation(maxEvents, r, file, printer);
			simulation.setUpSimulation();
			Statistics stats = simulation.runFullSimulation();

			if ( stats.getTotalCost() < bestTotalCost ) {
				bestR4 = r4;
				bestTotalCost = stats.getTotalCost();
			}
			r4 -= delta;
		}

		simulation = null;

		runSingleNodeTimeAnalysis(bestR4);
	}

	static void runSingleNodeTimeAnalysis(double bestR4) {
		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CDF});

		Rates r = new Rates(r1, r2, r3, bestR4, LAMBDA);
		simulation = new VirusSingleSimulation(maxEvents, r, printer);
		simulation.setUpSimulation();
		simulation.runFullSimulation();
	}

	static void runMeshTimeAnalysis(double bestR4) {
		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CDF});
//
//		Rates r = new Rates(r1, r2, r3, bestR4, LAMBDA);
//		simulation = new VirusMeshSimulation(maxEvents, r, printer);
//		simulation.setUpSimulation();
//		simulation.runFullSimulation();
		for (int i = 0; i < MAX_SIMULATION; i++) {
			Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
			Statistics stats = null;
			simulation = new VirusMeshSimulation(maxEvents, r,10);
			simulation.setUpSimulation();
			stats = simulation.runFullSimulation();

			Statistics.incrementSimulation();
			Statistics.accumulatePiO(stats.getPiO());
			Statistics.accumulatePiP(stats.getPiP());
			Statistics.accumulatePiR(stats.getPiR());
			Statistics.accumulatePiF(stats.getPiF());
			Statistics.accumulateInfectedCost(stats.getInfectedCost());
			Statistics.accumulateSamplingCost(stats.getSamplingCost());
			Statistics.accumulateTotalCost(stats.getTotalCost());
		}
	}

	static void runRingTimeAnalysis(double bestR4) {
//		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CDF});
//
//		Rates r = new Rates(r1, r2, r3, bestR4, LAMBDA);
//		simulation = new VirusRingSimulation(maxEvents, r, printer);
//		simulation.setUpSimulation();
//		simulation.runFullSimulation();
	}



}
