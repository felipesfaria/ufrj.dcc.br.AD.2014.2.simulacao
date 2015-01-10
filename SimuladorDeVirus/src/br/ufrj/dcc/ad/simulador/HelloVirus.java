/************************************************************************************
 * Main class of the project. It expects 3 doubles and 1 integer as arguments.
 * - First Argument -> r4 value
 * - Second Argument -> delta value
 * - Third Argument -> min_r4 value
 * - Fourth Argument -> Analysis Type
 * 
 * Analysis Types :
 * 1 - runRingCostAnalysis();
 * 2 - runMeshOnce();
 * 3 - runMeshCostAnalysis();
 * 4 - runEndogenousMeshCostAnalysis();
 * 5 - runEndogenousRingCostAnalysis();
 * 6 - runSingleNodeCostAnalysis();
 * 7 - runSingleNodeTimeAnalysis();
 * 
 * @author bkosawa, ffaria, pedrohcb, rney
 * @date January 13, 2015 
 ************************************************************************************/

package br.ufrj.dcc.ad.simulador;
import br.ufrj.dcc.ad.simulador.interfaces.VirusSimulation;
import br.ufrj.dcc.ad.simulador.model.PrintOptions;
import br.ufrj.dcc.ad.simulador.model.Rates;
import br.ufrj.dcc.ad.simulador.utils.FileUtil;
import br.ufrj.dcc.ad.simulador.utils.Printer;
import br.ufrj.dcc.ad.simulador.utils.Statistics;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;

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
	static int analysis_type = 6;
	
	static int maxEvents = 10000;
	
	public static void main(String[] args) {
		
		System.out.println("================ Welcome to the best assignment in the world ================");
		
		if (args.length == 4) {
			try {
				r4 = Double.parseDouble(args[0]);
				delta = Double.parseDouble(args[1]);
				min_r4 = Double.parseDouble(args[2]);
				analysis_type = Integer.parseInt(args[4]);
				
			} catch(Exception e) {
				System.out.println("Wrong parameters! Error: " + e);
				System.out.println("Closing the program");
				return;
			}
		} else {
			boolean ready = false;
			Scanner keyboard = new Scanner(System.in);
			System.out.println("WARNING! You did not start your program with the right parameters!");
			while (!ready) {
				try {
					System.out.println("Please chose your r4 **double value** (eg: 1,0) :");
					r4 = keyboard.nextDouble();
					System.out.println("Now chose your delta **double value** (eg: 0,01) :");
					delta = keyboard.nextDouble();
					System.out.println("Now chose your min_r4 **double value** (eg: 0,0) :");
					min_r4 = keyboard.nextDouble();
					System.out.println("And for last, your analysis_type (choose an integer number): " + 
										"\n 1 - runRingCostAnalysis" +
					 					"\n 2 - runMeshOnce" +
					 					"\n 3 - runMeshCostAnalysis" +
					 					"\n 4 - runEndogenousMeshCostAnalysis" +
					 					"\n 5 - runEndogenousRingCostAnalysis" +
					 					"\n 6 - runSingleNodeCostAnalysis" +
					 					"\n 7 - runSingleNodeTimeAnalysis"
					 					); 
					analysis_type = keyboard.nextInt();
					ready = true;
				} catch(Exception e) {
					System.out.println("An error has occurred! Error: " + e);
					System.out.println("Make sure that your double input is using comma instead of dot for the decimal part!");
					System.out.println("****************************** Try Again ********************************");
					keyboard.next();
				}
			}
			keyboard.close();
		}
		System.out.println("Parameters...");
		System.out.println("r4 : " + r4);
		System.out.println("delta : " + delta);
		System.out.println("min_r4 : " + min_r4);
		System.out.println("analysis_type: " + analysis_type);
		System.out.println("====================== Thanks! Starting the program! =======================");
		
		switch (analysis_type) {
		case 1:
			runRingCostAnalysis();
			break;
		case 2:
			runMeshOnce();
			break;
		case 3:
			runMeshCostAnalysis();
			break;
		case 4:
			runEndogenousMeshCostAnalysis();
			break;
		case 5:
			runEndogenousRingCostAnalysis();
			break;
		case 6:
			runSingleNodeCostAnalysis();
			break;
		case 7:
			runSingleNodeTimeAnalysis(r4);
			break;	
		default:
			System.out.println("Wrong Analysis type!! CLOSING THE PROGRAM!!");
			return;
		}
		System.out.println("====================== .CSVs GENERATED ON ROOT FOLDER ======================");
		System.out.println("================================= Finished =================================");
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
		FileUtil file = new FileUtil("MeshCDFAnalysis.csv", "time;density;r4");

		for (int i = 0; i < MAX_SIMULATION; i++) {
			Rates r = new Rates(r1, r2, r3, bestR4, LAMBDA, BETA);
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
			Statistics.accumulatePrePDF(stats.getPrePDF(), stats.getTotalCount());
		}

		printer.printGlobalCDF(file, r4, Statistics.getGlobalCDF());
		Statistics.resetGlobalStatistics();
//		Statistics.GetIntervaloDeConfianca(r4);

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
