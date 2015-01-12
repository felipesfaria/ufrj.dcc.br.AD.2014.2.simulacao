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
 * 4 - runExogenousMeshCostAnalysis();
 * 5 - runExogenousRingCostAnalysis();
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
import java.util.Scanner;

public class HelloVirus {

	private static final int REALY_LARGE_NUM = 10000000;

	private static final int MAX_SIMULATION = 50000;
	
	private static final double PRECISAO = 0.01; 

	public static VirusSimulation simulation;

	public static final double r1 = 2.0;
	public static final double r2 = 0.8;
	public static final double r3 = 3.0;
	public static final double LAMBDA = 1.0 / (12.0 * 24.0 * 30.0);
	public static final double BETA = 0.08;
	
	static Double max_r4 = 1.0;
	static Double r4 = 1.0;
	static Double delta = 0.01;
	static Double min_r4 = 0.0;
	static int analysis_type = 6;
	
	static int maxEvents = 10000;
	
	public static void main(String[] args) {
		
		System.out.println("================ Welcome to the best assignment in the world ================");
		
		if (args.length == 4) {
			try {
				max_r4 = Double.parseDouble(args[0]);
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
					System.out.println("Please chose your maximum r4 **double value** (eg: 1,0) :");
					max_r4 = keyboard.nextDouble();
					System.out.println("Now chose your delta **double value** (eg: 0,01) :");
					delta = keyboard.nextDouble();
					System.out.println("Now chose your minimum r4 **double value** (eg: 0,0) :");
					min_r4 = keyboard.nextDouble();
					System.out.println("And for last, your analysis_type (choose an integer number): " + 
										"\n 1 - runRingCostAnalysis" +
					 					"\n 2 - runMeshOnce" +
					 					"\n 3 - runMeshCostAnalysis" +
					 					"\n 4 - runExogenousMeshCostAnalysis" +
					 					"\n 5 - runExogenousRingCostAnalysis" +
					 					"\n 6 - runSingleNodeCostAnalysis" +
					 					"\n 7 - runSingleNodeTimeAnalysisWithConfidenceInterval" +
							            "\n 8 - runSingleNodeTimeAnalysis"
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
		System.out.println("r4 : " + max_r4);
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
			runExogenousMeshCostAnalysis();
			break;
		case 5:
			runExogenousRingCostAnalysis();
			break;
		case 6:
			runSingleNodeCostAnalysis();
			break;
        case 7:
			runSingleNodeTimeAnalysisWithConfidenceInterval();
				break;
		case 8:
			runSingleNodeTimeAnalysis(r4);
			break;	
		default:
			System.out.println("Wrong Analysis type!! CLOSING THE PROGRAM!!");
			return;
		}
		System.out.println("====================== .CSVs GENERATED ON ROOT FOLDER ======================");
		System.out.println("================================= Finished =================================");
	}

	static void runExogenousRingCostAnalysis() {
		System.out.println("====================== Starting Exogenous Ring Cost Analysis ==============");
		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CSV});

		FileUtil file = new FileUtil("ExogenousRingCostAnalysis.csv", "r4;piO;ic;pi;ic;piR;ic;piF;ic;cV;ic;cS;ic;cT;ic");
		r4 = max_r4;
		while (r4 >= min_r4) {
			printer.printProgress(max_r4, r4, min_r4);
			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
				Statistics stats = null;
				simulation = new VirusRingExogenousSimulation(maxEvents, r,file,10);
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
				if(i>1000&&Statistics.GetIntervalosDeConfiancaDeCustoTotal()<PRECISAO)
					break;
			}
			

			printer.printGlobalStats(file, r4);
			Statistics.resetGlobalStatistics();
			System.gc();
			r4 -= delta;
		}
		printer.printProgressCompletion();

	}

	static void runRingCostAnalysis() {
		System.out.println("====================== Starting Ring Cost Analysis =========================");
		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CSV});
		double bestR4 = max_r4;
		double bestTotalCost = REALY_LARGE_NUM;

		FileUtil file = new FileUtil("RingCostAnalysis.csv", "r4;piO;ic;pi;ic;piR;ic;piF;ic;cV;ic;cS;ic;cT;ic");

		r4 = max_r4;
		while (r4 >= min_r4) {
			printer.printProgress(max_r4, r4, min_r4);
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
				if(i>1000&&Statistics.GetIntervalosDeConfianca().get("CustoTotal")<PRECISAO)
					break;
			}

			printer.printGlobalStats(file, r4);
			if ( Statistics.getGlobalAverageTotalCost() < bestTotalCost ) {
				bestR4 = r4;
				bestTotalCost = Statistics.getGlobalAverageTotalCost();
			}
			Statistics.resetGlobalStatistics();
			r4 -= delta;
		}
		printer.printProgressCompletion();
		runRingTimeAnalysis(bestR4);
	}

	static void runMeshCostAnalysis() {
		System.out.println("====================== Starting Mesh Cost Analysis =========================");
		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CSV});
		r4 = max_r4;
		double bestR4 = r4;
		double bestTotalCost = REALY_LARGE_NUM;

		FileUtil file = new FileUtil("MeshCostAnalysis.csv", "r4;piO;ic;pi;ic;piR;ic;piF;ic;cV;ic;cS;ic;cT;ic");

		int numOfSims = MAX_SIMULATION;
		while (r4 >= min_r4) {
			printer.printProgress(max_r4, r4, min_r4);

			for (int i = 0; i < numOfSims; i++) {
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
				if(i>1000&&Statistics.GetIntervalosDeConfianca().get("CustoTotal")<PRECISAO)
					break;
			}

			printer.printGlobalStats(file, r4);
			if ( Statistics.getGlobalAverageTotalCost() < bestTotalCost ) {
				bestR4 = r4;
				bestTotalCost = Statistics.getGlobalAverageTotalCost();
			}
			Statistics.resetGlobalStatistics();
			numOfSims -= numOfSims*0.07;
			r4 -= delta;
		}

		printer.printProgressCompletion();
		runMeshTimeAnalysis(bestR4);

	}

	static void runExogenousMeshCostAnalysis() {
		Printer printer = new Printer();
		FileUtil file = new FileUtil("ExogenousMeshCostAnalysis.csv", "r4;piO;ic;pi;ic;piR;ic;piF;ic;cV;ic;cS;ic;cT;ic");
		r4 = max_r4;

		while (r4 >= min_r4) {
			printer.printProgress(max_r4, r4, min_r4);

			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA, BETA);
				Statistics stats = null;
				simulation = new VirusMeshExogenousSimulation(maxEvents, r,10);
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
				if(i>1000&&Statistics.GetIntervalosDeConfianca().get("CustoTotal")<PRECISAO)
					break;
			}

			printer.printGlobalStats(file, r4);
			Statistics.resetGlobalStatistics();
			r4 -= delta;
		}
		printer.printProgressCompletion();
	}

	static void runMeshOnce(){
		FileUtil file = new FileUtil("CostAnalysis.csv", "R4;piO;cV;cS;cT");
		r4 = max_r4;
		Rates r = new Rates(r1, BETA, r3, r4, LAMBDA);
		simulation = new VirusMeshSimulation(maxEvents, r, file);
		Printer printer = new Printer();
		printer.setPrintOptions(new PrintOptions[] { PrintOptions.CSV });
		simulation.setUpSimulation();
		simulation.runFullSimulation();

	}


	static void runSingleNodeCostAnalysis() {
		System.out.println("====================== Starting Single Node Cost Analysis ==================");
		Printer printer = new Printer(new PrintOptions[] { PrintOptions.CSV });
		FileUtil file = new FileUtil("SingleNodeCostAnalysis.csv", "R4;piO;cV;cS;cT");
		r4 = max_r4;
		double bestR4 = r4;
		double bestTotalCost = REALY_LARGE_NUM;
		while (r4 >= min_r4) {
			printer.printProgress(max_r4, r4, min_r4);
			for (int i = 0; i < MAX_SIMULATION; i++) {
				Rates r = new Rates(r1, r2, r3, r4, LAMBDA);
	
	
				simulation = new VirusSingleSimulation(maxEvents, r, file, printer);
				simulation.setUpSimulation();
				Statistics stats = simulation.runFullSimulation();

				Statistics.incrementSimulation();
				Statistics.accumulatePiO(stats.getPiO());
				Statistics.accumulatePiP(stats.getPiP());
				Statistics.accumulatePiR(stats.getPiR());
				Statistics.accumulatePiF(stats.getPiF());
				Statistics.accumulateInfectedCost(stats.getInfectedCost());
				Statistics.accumulateSamplingCost(stats.getSamplingCost());
				Statistics.accumulateTotalCost(stats.getTotalCost());
				if(i>1000&&Statistics.GetIntervalosDeConfianca().get("CustoTotal")<PRECISAO)
					break;
	
			}
			if ( Statistics.getGlobalAverageTotalCost() < bestTotalCost ) {
				bestR4 = r4;
				bestTotalCost = Statistics.getGlobalAverageTotalCost();
			}

			printer.printGlobalStats(file, r4);
			Statistics.resetGlobalStatistics();
			r4 -= delta;
		}

		simulation = null;

		printer.printProgressCompletion();
		runSingleNodeTimeAnalysis(bestR4);
	}

	static void runSingleNodeTimeAnalysisWithConfidenceInterval(){
		Printer printer = new Printer(new PrintOptions[] { PrintOptions.CSV });
		FileUtil file = new FileUtil("SingleNodeCostAnalysis.csv", "r4;piO;ic;pi;ic;piR;ic;piF;ic;cV;ic;cS;ic;cT;ic");
		r4 = max_r4;
		double bestR4 = r4;
		double bestTotalCost = REALY_LARGE_NUM;
		while (r4 >= min_r4) {
			printer.printProgress(max_r4, r4, min_r4);
			Rates r = new Rates(r1, r2, r3, r4, LAMBDA);

			simulation = new VirusSingleSimulation(maxEvents, r, file, printer);
			simulation.setUpSimulation();
			Statistics stats = simulation.runFullSimulation();

			Statistics.incrementSimulation();
			Statistics.accumulatePiO(stats.getPiO());
			Statistics.accumulatePiP(stats.getPiP());
			Statistics.accumulatePiR(stats.getPiR());
			Statistics.accumulatePiF(stats.getPiF());
			Statistics.accumulateInfectedCost(stats.getInfectedCost());
			Statistics.accumulateSamplingCost(stats.getSamplingCost());
			Statistics.accumulateTotalCost(stats.getTotalCost());

			printer.printGlobalStats(file, r4);
			if ( Statistics.getGlobalAverageTotalCost() < bestTotalCost ) {
				bestR4 = r4;
				bestTotalCost = Statistics.getGlobalAverageTotalCost();
			}
			Statistics.resetGlobalStatistics();
			r4 -= delta;
		}

		simulation = null;

		printer.printProgressCompletion();
		runSingleNodeTimeAnalysis(bestR4);	}

	static void runSingleNodeTimeAnalysis(double bestR4) {
		System.out.println("====================== Starting Single Node Time Analysis ==================");
		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CDF});

		Rates r = new Rates(r1, r2, r3, bestR4, LAMBDA);
		simulation = new VirusSingleSimulation(maxEvents, r, printer);
		simulation.setUpSimulation();
		simulation.runFullSimulation();
	}

	static void runMeshTimeAnalysis(double bestR4) {
		System.out.println("====================== Starting Mesh Time Analysis =========================");
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

		Printer printer = new Printer(new PrintOptions[]{PrintOptions.CDF});
		printer.printGlobalCDF(file, bestR4, Statistics.getGlobalCDF());
		Statistics.resetGlobalStatistics();

	}

	static void runRingTimeAnalysis(double bestR4) {
		System.out.println("====================== Starting Ring Time Analysis =========================");
		Printer printer = new Printer();
		FileUtil file = new FileUtil("RingCDFAnalysis.csv", "time;density;r4");

		for (int i = 0; i < MAX_SIMULATION; i++) {
			Rates r = new Rates(r1, r2, r3, bestR4, LAMBDA, BETA);
			Statistics stats = null;
			simulation = new VirusRingSimulation(maxEvents, r,10);
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

		printer.setPrintOptions(new PrintOptions[]{PrintOptions.CDF});
		printer.printGlobalCDF(file, bestR4, Statistics.getGlobalCDF());
		Statistics.resetGlobalStatistics();
	}



}
